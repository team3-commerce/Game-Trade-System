# TRD — Game Trade System

> 게임 아이템 거래소 (Technical Requirements Document)
>
> 대응 제품 문서: [PRD.md](PRD.md) · 비즈니스 규칙: [BUSINESS.md](BUSINESS.md) · API 명세: [API.md](API.md)
> 기준 브랜치: `dev`

---

## 1. 기술 스택

| 영역 | 선택 | 비고 |
|---|---|---|
| 언어 / 런타임 | Java 17 (Gradle toolchain) | |
| 프레임워크 | Spring Boot 4.0.3 | Web MVC, Data JPA, Validation |
| 빌드 | Gradle (Wrapper) | `com.diffplug.spotless` + palantir-java-format 2.28.0 |
| DB | MySQL 8 | `hibernate.ddl-auto` 프로파일별 상이 |
| ORM / 쿼리 | Spring Data JPA + QueryDSL 5.0 (jakarta) | 동적 검색/정렬 |
| 캐시 (로컬) | Caffeine | `SimpleCacheManager`에 캐시별 수동 등록 |
| 캐시 / 락 (원격) | Redis + Redisson 3.45.0 | Lettuce(기본) + Redisson 병행 |
| 인증 | Spring Security + JWT (jjwt 0.12.x) | STATELESS |
| 소셜 로그인 | Spring Security OAuth2 Client | Google / Kakao / GitHub |
| 실시간 | Spring WebSocket + STOMP | Redis Pub/Sub 연동 |
| 스케줄링 | Quartz + Spring `@Scheduled` | 만료 처리 |
| 테스트 | JUnit 5, Spring Boot Test, Testcontainers 2.0.2, H2 | |
| 더미 데이터 | datafaker 2.x | 부하 테스트 시딩 |
| 부하 테스트 | k6 + InfluxDB 1.8 + Grafana | `docker-compose-k6.yml` |
| 배포 | GitHub Actions → Docker Hub → AWS EC2 (SSM) | `eclipse-temurin:17-jre-jammy` |

---

## 2. 시스템 구성

### 2.1 런타임 토폴로지 (현재)

```
                 ┌──────────────────────── AWS EC2 (단일 인스턴스) ─────────┐
  Client ──HTTP──▶  docker compose                                          │
         ──WS────▶    ├── trade-app  (Spring Boot, :8080, profile=prod)     │
                      ├── trade-mysql (mysql:8.0, volume: mysql_data)       │
                      └── trade-redis (maxmemory 1gb, allkeys-lru)          │
                 └───────────────────────────────────────────────────────────┘
```

- 세 컨테이너는 `trade-network` 브리지 네트워크에서 통신하며 앱만 외부에 8080을 노출합니다.
- Redis는 `maxmemory 1gb` + `allkeys-lru`로, 캐시 용도의 유실 허용 저장소로 운용합니다.
- MySQL은 named volume에 영속화되고 healthcheck 통과 후에야 앱이 기동합니다.

### 2.2 제약

- **Redis는 단일 노드 구성입니다.** ElastiCache 클러스터 모드에서 `psubscribe`가 지원되지 않아
  채팅 Pub/Sub이 동작하지 않았고, 코드 수정 대신 단일 노드로 회귀하기로 결정했습니다
  (`TROUBLE_SHOOTING.md` 참고). 클러스터 전환 시 패턴 구독 제거가 선행되어야 합니다.
- 앱은 stateless(JWT)이므로 수평 확장 자체는 가능하나, 현재 **로컬 Caffeine 캐시를 사용하는 경로는
  인스턴스 간 캐시 일관성이 보장되지 않습니다.** 다중 인스턴스로 확장하려면 Redis 캐시 경로(V3 이상)만
  사용하거나 캐시 무효화를 Pub/Sub으로 전파해야 합니다.

---

## 3. 애플리케이션 아키텍처

### 3.1 패키지 레이아웃

```
src/main/java/com/example/tradedemo/
├── auth/            인증인가 (횡단 관심사 — domain 밖)
│   ├── config/      SecurityConfig, JwtProperties, OAuth2 성공 핸들러, 401/403 핸들러
│   ├── filter/      JwtAuthenticationFilter
│   ├── interceptor/ StompAuthInterceptor  (WebSocket 인증)
│   ├── provider/    JwtTokenProvider
│   └── service/     AuthService, CustomOAuth2UserService, CustomUserDetailsService
├── common/
│   ├── annotation/  @RedisLock, @RedissonLock
│   ├── aspect/      RedisLockAspect, RedissonLockAspect
│   ├── config/      Cache, Jackson, Querydsl, Redisson, RedisSubscriber, Web, WebSocket
│   ├── dto/         ApiResponse, PageResponse
│   ├── entity/      Base (createdAt / modifiedAt 감사)
│   ├── exception/   ErrorEnum, ServiceException, GlobalExceptionHandler
│   └── initializer/ 시딩(Item/MemberItem/Coupon/RedisCache) + dummy/ 대량 생성
└── domain/          chat · coupon · debug · item · marketlistings · members
                     · order · pending · wallet · scheduler
```

각 도메인은 `controller / service / repository / entity / dto / enums` 구조를 따르며,
도메인 전용 예외나 상수가 필요하면 `exception/`, `consts/`를 추가로 둡니다.

`src/tool/`은 별도 Gradle sourceSet으로, `sourceSets.main.output`을 의존해 애플리케이션 코드를
재사용하지만 운영 jar에는 포함되지 않습니다. docker-java로 더미 데이터가 적재된 MySQL 이미지를
`db-images/`에 tar로 굽는 개발 도구입니다.

### 3.2 계층 규약

```
Controller ──▶ Facade (도메인 조합이 필요할 때만) ──▶ Service ──▶ Repository
                                                        └──▶ CacheService (Redis 직접 접근)
```

- **Controller**는 `PrincipalDetails`에서 회원 ID만 뽑아 서비스에 넘기고 `ApiResponse`로 감쌉니다.
  비즈니스 판단을 하지 않습니다.
- **Service**는 자기 도메인 안에서만 트랜잭션을 엽니다. 다른 도메인 서비스를 직접 호출하지 않습니다.
- **Facade**는 여러 도메인 서비스를 조합하는 트랜잭션 경계입니다. 현재 `OrderFacade`(구매 정산),
  `CouponFacade`(쿠폰 사용 → 지갑 증액)가 있습니다. 도메인 간 조합이 새로 필요하면 서비스끼리
  엮지 말고 facade를 추가합니다.
- **CacheService**(`ItemCacheService`, `MarketListingCacheService`, `MemberItemCacheService`,
  `CouponCacheService`)는 `@Cacheable` 애노테이션으로 표현하기 어려운 Redis 자료구조 조작
  (ZSet 랭킹, 수동 직렬화, TTL 제어)을 담당합니다.

### 3.3 API 버저닝 규약

**엔드포인트의 `v1`/`v2`/`v3`는 하위 호환용 버전이 아니라, 같은 기능의 최적화 단계별 구현입니다.**
성능 비교 실험의 대조군으로 남겨 두는 것이 목적이며, 실험이 끝나면 최종 버전만 남기고 정리할 예정입니다.

| 버전 | 일반적 의미 |
|---|---|
| V1 | 최적화 없는 기준 구현 (no cache, DB 락만) |
| V2 | Caffeine 로컬 캐시 (`@Cacheable` / `@CacheEvict`) |
| V3 | Redis 캐시 / Redis 분산락 |
| V4 | (거래소 등록) Lettuce 기반 `@RedisLock` + 캐시 무효화 |
| V5 | (거래소 등록) Redisson `@RedissonLock` + Redis 캐시 — 최종본 |

예외적으로 쿠폰 선착순 발급은 락 구현 비교를 위해 두 갈래로 나뉩니다.

| 엔드포인트 | 구현 |
|---|---|
| `POST /api/v1/coupon-policies/{id}/issue` | DB 락만 (기준) |
| `POST /api/v2/coupon-policies/{id}/issue` | 수동 분산락 (`LockService`) + 캐시 무효화 |
| `POST /api/v3-1/coupon-policies/{id}/issue` | Lettuce `@RedisLock` AOP |
| `POST /api/v3-2/coupon-policies/{id}/issue` | Redisson `@RedissonLock` AOP + Redis 캐시 |

**기능을 수정할 때는 어느 버전을 건드리는지 먼저 확인해야 합니다.** 낮은 버전은 벤치마크 대조군이므로
성능 개선을 소급 적용하면 측정값이 무의미해집니다.

---

## 4. 데이터 모델

### 4.1 엔티티 개요

모든 엔티티는 `Base`(`@EntityListeners(AuditingEntityListener)`)를 상속해 `created_at` /
`modified_at`을 자동 관리합니다. (채팅 엔티티는 예외적으로 자체 `createdAt`을 둡니다.)

| 테이블 | 핵심 컬럼 | 관계 |
|---|---|---|
| `members` | email(U), password(nullable), nickname(U), role, status, statusChangedAt, statusReason, lastLoginAt, refreshToken | |
| `social_accounts` | provider, providerId, linkedAt | N:1 `members` |
| `items` | name(U), itemType | |
| `member_items` | quantity, acquiredAt | N:1 `members`, N:1 `items` |
| `market_listings` | itemName, unitPrice, totalPrice, quantity, status, saleEndAt | N:1 `member_items`, N:1 `members` |
| `orders` | transactionMoney, transactionStock | N:1 seller/buyer(`members`), N:1 `market_listings`, N:1 `items` |
| `order_snapshots` | price, productName, productQuantity | 1:1 `orders` |
| `pending_asset` | pendingType, type, moneyAmount, itemQuantity, isClaimed, claimedAt, expiredAt | N:1 `market_listings`, N:1 `orders`(nullable), N:1 `members` |
| `wallets` | balance | 1:1 `members` |
| `wallet_histories` | amount, type, balanceSnapshot | N:1 `wallets`/`members`, N:1 `orders`(nullable), N:1 `coupon_history`(nullable) |
| `coupon_policies` | name(U), moneyAmount, issueType, totalQuantity, expendQuantity, policyStartedAt/ExpiredAt, policyDuration, couponDuration | |
| `member_coupons` | status, issuedAt, expiredAt | N:1 `members`, N:1 `coupon_policies` |
| `coupon_history` | moneyAmount, usedAt, status | N:1 `members`, N:1 `member_coupons` |
| `chat_rooms` | name, createdAt | N:1 `market_listings` |
| `chat_room_members` | role(BUYER/SELLER), joinedAt | N:1 `chat_rooms`, N:1 `members` |
| `chat_messages` | content, createdAt | N:1 `chat_rooms`, N:1 sender(`members`) |

ERD 이미지는 `readme/ERD.png`에 있습니다.

### 4.2 설계 규칙

- **모든 `@ManyToOne` / `@OneToOne`은 `FetchType.LAZY`.** N+1은 QueryDSL fetch join 또는
  DTO projection으로 해결하며, EAGER로 되돌리지 않습니다.
- 금액은 전부 `BigDecimal`(`precision = 19, scale = 2`). `double`을 쓰지 않습니다.
- 상태는 전부 `@Enumerated(EnumType.STRING)`. ordinal 저장 금지(순서 변경 시 데이터 파손).
- 이력 테이블(`wallet_histories`, `coupon_history`, `order_snapshots`)은 **변경하지 않고 append만** 합니다.
  `balanceSnapshot`, `OrderSnapshot`처럼 시점 값을 복제해 두어 원본 변경과 무관하게 이력을 재현합니다.
- 유니크 제약: `members.email`, `members.nickname`, `items.name`, `coupon_policies.name`,
  `social_accounts(provider, providerId)`, `chat_room_members(chatRoom, member)`.

### 4.3 상태 전이

```
MarketListingStatus:
   SELLING ──구매──▶ SOLD ──양측 수령 완료──▶ CLAIMED
      │
      ├──판매자/관리자 취소──▶ CANCELLED
      └──만료 배치──────────▶ EXPIRED

PendingAsset:
   생성(isClaimed=false, expiredAt 설정)
      ├──수령──▶ isClaimed=true, claimedAt 기록 → 지갑/인벤토리 반영
      └──만료 배치──▶ 소멸

CouponStatus:  ISSUED ──사용──▶ USED
                  └──만료 배치──▶ EXPIRED
```

`MarketListingStatus`는 채팅 시스템 메시지 분기(F-CHAT-03)에서도 그대로 읽히므로,
새 상태를 추가할 때 채팅 쪽 분기를 함께 갱신해야 합니다.

---

## 5. 동시성 제어

### 5.1 락 애노테이션

`common/annotation` + `common/aspect`에 두 쌍이 있습니다.

| | `@RedisLock` | `@RedissonLock` |
|---|---|---|
| 구현 | Lettuce `RedisTemplate` SETNX 스핀락 | Redisson `RLock` |
| 대기 방식 | 폴링(스핀) | pub/sub 기반 대기 |
| 기본 대기 | `retryDelaySeconds = 10` | `retryDelaySeconds = 10` |
| 기본 TTL | `lockTimeoutSeconds = 5` | `lockTimeoutSeconds = 5`, `timeUnit` 지정 가능 |
| Aspect | `RedisLockAspect` | `RedissonLockAspect` |

`key`는 SpEL이며 메서드 파라미터를 참조합니다.

```java
@RedissonLock(key = "'lock:market-listing:member:' + #memberId + ':item:' + #request.getMemberItemId()")
@Transactional
public GetMarketListingResponse createV5(Long memberId, CreateMarketListingRequest request) { ... }
```

### 5.2 락과 트랜잭션의 순서 (중요)

**Aspect가 `@Transactional`보다 바깥에서 실행되어야 합니다.**

```
락 획득 ──▶ 트랜잭션 시작 ──▶ 비즈니스 로직 ──▶ 커밋 ──▶ 락 해제
```

이 순서가 뒤집혀 **트랜잭션 커밋 전에 락이 풀리면**, 아직 커밋되지 않은 변경을 다음 스레드가
읽지 못해 수량 정합성이 깨집니다. 실제로 선착순 쿠폰 발급에서 `MemberCoupon` 32건 /
`expendQuantity` 18로 어긋난 사례가 있었습니다(150 동시 요청, 총 수량 100). 상세 분석은
`TROUBLE_SHOOTING.md`의 "선착순 쿠폰 발급 DeadLock 문제" 항목에 있습니다.

락 관련 코드를 옮기거나 애노테이션 순서를 바꿀 때는 이 제약을 반드시 확인하고,
`CouponConcurrencyTest`로 회귀를 검증합니다.

### 5.3 락이 필요한 지점

| 지점 | 락 키 | 보호 대상 |
|---|---|---|
| 선착순 쿠폰 발급 | `lock:coupon:{couponPolicyId}` | 총 수량 초과 발급, 발급 수 ↔ 소진 수량 불일치 |
| 거래소 상품 등록 | `lock:market-listing:member:{memberId}:item:{memberItemId}` | 동일 인벤토리 아이템 중복 등록 |
| 상품 구매 | 리스팅 단위 | 이중 판매 |

낙관적 락(`@Version`) 구현은 `dev`에 포함되어 있지 않으며 `feat/coupon-lock` 브랜치에 있습니다.

---

## 6. 캐시 전략

### 6.1 캐시 등록

`common/config/CacheConfig`가 `SimpleCacheManager`에 Caffeine 인스턴스를 **이름별로 명시 등록**합니다.
자동 생성이 아니므로 **새 캐시 이름을 `@Cacheable`에 쓰려면 여기에 먼저 추가해야 합니다.**

| 캐시 이름 | 최대 크기 | 만료 정책 |
|---|---|---|
| `items` | 1,000 | write 후 60분 |
| `itemsSearches` | 1,000 | write 후 60분 |
| `marketListingItem` | 1,000 | write 후 10분 |
| `marketListingsFirstPage` | 100 | write 후 3분 |
| `members` | 1,000 | access 후 30분 |
| `inventoryList` / `inventoryItem` | 1,000 | access 후 60분 |
| `memberAuths` | 1,000 | access 후 30분 |
| `refreshTokens` | 10,000 | write 후 7일 + 1시간 |
| `blacklistedTokens` | 10,000 | write 후 30분 |
| `couponPolicies` | 50 | write 후 10분 |
| `memberCoupons` | 1,000 | write 후 5분 |
| `couponHistories` | 500 | write 후 10분 |

같은 클래스에 Redis용 `RedisTemplate<String, Object>` 빈이 정의되어 있습니다
(key: `StringRedisSerializer`, value: `JacksonJsonRedisSerializer`).

### 6.2 무효화 규칙

쓰기 경로는 자신이 오염시키는 조회 캐시를 반드시 evict 해야 합니다.

```java
@Caching(evict = {
    @CacheEvict(cacheNames = "marketListingsFirstPage", allEntries = true),
    @CacheEvict(cacheNames = "marketListingItem", key = "'listing:' + #marketListingId")
})
```

- 목록 첫 페이지(`marketListingsFirstPage`)는 어떤 등록/구매/취소에도 영향을 받으므로 `allEntries = true`.
- 단건 캐시는 키를 특정해 evict.
- 상품 등록은 판매자 인벤토리(`inventoryList`)도 함께 무효화합니다.

TTL이 짧은 이유(첫 페이지 3분, 단건 10분)는 거래소 데이터의 변동성이 크기 때문이며,
무효화가 누락되더라도 stale 노출 시간을 제한하기 위한 안전장치입니다.

### 6.3 인기 검색어 (Redis ZSet)

`MarketListingCacheService`가 DB 집계 대신 Redis ZSet으로 처리합니다.

- 검색 발생 시 `opsForZSet().incrementScore(dailyKey, keyword, 1)`로 카운트 증가.
- 접두어 검색(자동완성)을 위해 score 0의 보조 ZSet을 병행 유지하고, 사전순 범위 조회 후
  본 랭킹에서 score를 조회해 정렬합니다.
- 일 단위 키를 사용해 **하루가 지나면 자연히 순위가 초기화**됩니다.

---

## 7. 인증인가

### 7.1 필터 체인

`SecurityConfig`:

- CSRF / formLogin / httpBasic 비활성, `SessionCreationPolicy.STATELESS`.
- CORS는 현재 `http://localhost:63342`(IntelliJ 내장 서버)만 허용 — **배포 환경 오리진 추가 필요.**
- 화이트리스트: `/api/v{1,2,3}/auth/**`, `/login/oauth2/**`, `/oauth2/**`, `/ws/**`, `/api/chat/**`.
- `/api/v1/admin/**` 는 `ROLE_ADMIN`, 그 외 전부 인증 필요.
- `JwtAuthenticationFilter`를 `UsernamePasswordAuthenticationFilter` 앞에 배치.

### 7.2 토큰

- Access Token + Refresh Token. 서명 알고리즘은 jjwt 기본 HMAC, 시크릿은 `JWT_SECRET` 환경변수.
- `JwtAuthenticationFilter`가 `CacheManager`(`refreshTokens`, `blacklistedTokens`)와 `RedisTemplate`을
  직접 주입받아 블랙리스트를 확인합니다. 로그아웃한 토큰은 잔여 유효기간 동안 거부됩니다.
- `memberAuths` 캐시로 요청마다 회원을 조회하는 비용을 줄입니다.

### 7.3 예외 응답 일관성

Spring Security가 필터 단계에서 던지는 401/403은 `@RestControllerAdvice`가 잡지 못합니다.
따라서 `CustomAuthenticationEntryPoint`(401), `CustomAccessDeniedHandler`(403)를 등록해
직접 `ApiResponse` 형태로 직렬화합니다. **인증 관련 에러 포맷을 바꿀 때는 이 두 핸들러를 수정합니다.**

### 7.4 소셜 로그인

- `CustomOAuth2UserService`가 제공자별 사용자 정보를 `OAuth2UserInfo` 구현체
  (`GoogleOAuth2UserInfo`, `KakaoOAuth2UserInfo`, `GithubOAuth2UserInfo`)로 정규화합니다.
  **새 제공자를 추가할 때는 이 인터페이스 구현체를 추가하고 `application.yml`에 registration을 등록합니다.**
- 이메일 누락 시 `{providerId}@{provider}.com` 가상 이메일 생성(유니크 제약 충족용, 학습 목적 단순화).
- `SocialAccount`가 `Member`와 1:N이므로 한 회원이 여러 제공자를 연동할 수 있습니다.
- 연동 해제 시 남은 로그인 수단이 0이 되면 차단합니다.
- 인증 성공 후 `OAuth2AuthenticationSuccessHandler`가 토큰을 발급하고
  `GET /api/v2/auth/oauth-success`로 전달합니다.

### 7.5 WebSocket 인증

STOMP 연결은 HTTP 필터 체인을 타지 않으므로 `auth/interceptor/StompAuthInterceptor`가
CONNECT 프레임의 토큰을 검증합니다. `/ws/**`가 시큐리티 화이트리스트인 이유가 이것이며,
**실제 인증은 인터셉터에서 이루어집니다.**

---

## 8. 실시간 채팅

- 엔드포인트 `/ws`, 앱 목적지 prefix `/pub`, 브로커 prefix `/sub` (`enableSimpleBroker`).
- 단일 인스턴스의 SimpleBroker만으로는 다중 서버 전파가 불가능하므로 **Redis Pub/Sub**을 병행합니다
  (`RedisSubscriberConfig`). 메시지를 Redis 채널에 발행하고 각 인스턴스가 구독해 자기 세션에 릴레이합니다.
- 패턴 구독(`psubscribe`)을 사용하기 때문에 Redis 클러스터 모드와 호환되지 않습니다(§2.2).
- 메시지 조회는 커서 기반(마지막 메시지 ID 기준 이전 50건)으로, offset 페이지네이션의
  깊은 페이지 비용을 피합니다.
- 시스템 메시지(입퇴장, 상태 안내)는 **DB에 저장하지 않고** 전송만 합니다.
- 테스트용 클라이언트: `src/main/resources/templates/chat.html`.

---

## 9. 스케줄링

Quartz와 Spring `@Scheduled`가 혼재합니다.

| 작업 | 방식 | 주기 |
|---|---|---|
| 쿠폰 만료 (`CouponExpiryScheduler`) | `@Scheduled` | `0 0 0 * * *` (매일 00시) |
| 회원 장기 미접속 비활성화 (`MemberScheduler`) | `@Scheduled` | `0 0 0 * * *` (매일 00시) |
| 거래소 상품 만료 (`MarketListingExpireJob`) | Quartz `Job` | 00 / 06 / 12 / 18시 |
| 수령 대기 자산 소멸 (`PendingAssetScheduler`) | Quartz | 만료 시각 기준 |

만료 처리 Job은 `domain/scheduler/BaseExpireJob`을 상속합니다. 새 만료 배치를 추가할 때는
이 추상 클래스를 상속해 공통 실행/로깅 흐름을 재사용합니다.

**거래소 만료 시각 계산 규칙**: 판매자가 선택한 노출 시간(12/24/48h)보다 실제 노출 시간이
짧아지면 안 되므로, 등록 시각 + 선택 시간 이후의 **첫 번째 슬롯**에서 만료시킵니다.
예) 03시 등록 + 12시간 → 15시가 아닌 다음 슬롯 18시 만료(실 노출 15시간).

**분산 환경 주의**: 다중 인스턴스로 확장하면 `@Scheduled` 작업이 인스턴스마다 중복 실행됩니다.
Quartz JDBC JobStore 또는 ShedLock 도입이 선행되어야 합니다.

---

## 10. 예외 처리 규약

- 도메인 예외는 **단일 예외 클래스** `ServiceException(ErrorEnum)`으로 통일합니다.
  도메인마다 예외 클래스를 새로 만들지 않습니다.
- `ErrorEnum`이 HTTP 상태와 메시지를 함께 보유하고, 메시지 문자열은 `ErrorMessage` 상수로 분리합니다.

```java
ERR_COUPON_POLICY_SOLD_OUT(HttpStatus.CONFLICT, ErrorMessage.MSG_COUPON_POLICY_SOLD_OUT),
```

- `GlobalExceptionHandler`(`@RestControllerAdvice`)가 `ServiceException` / 검증 실패 /
  그 외 예외를 `ApiResponse`로 변환합니다.
- **새 에러를 추가할 때는 `ErrorEnum` 상수 + `ErrorMessage` 상수만 추가하면 됩니다.**
- 필터 단계 예외(401/403)는 이 핸들러를 타지 않습니다(§7.3).

---

## 11. 응답 규약

| 타입 | 용도 |
|---|---|
| `ApiResponse<T>` | 모든 응답의 공통 래퍼 (상태 코드 + 데이터) |
| `PageResponse<T>` | 목록 응답. 페이지 메타 포함 |

컨트롤러는 `ResponseEntity<ApiResponse<...>>`를 반환합니다.
`JacksonConfig` + `jackson-datatype-jsr310`으로 `LocalDateTime` 직렬화를 처리합니다.

---

## 12. 빌드 · 테스트 · 실행

### 12.1 빌드

```bash
./gradlew build -x test      # 테스트 제외 빌드
./gradlew bootJar -x test    # 배포용 jar (CD가 사용)
./gradlew spotlessApply      # 포매팅 적용
```

Spotless는 `enforceCheck false`로 꺼져 있어 빌드를 깨뜨리지 않습니다("나중에 몰아서" 정책).
설정: UNIX 줄바꿈 통일, 미사용 import 제거, palantir-java-format.

### 12.2 테스트

테스트는 **MySQL과 Redis 인스턴스를 요구합니다.** `test-all.bat`이 전 과정을 자동화합니다.

```bash
./test-all.bat
# = docker compose -f ./misc/docker-compose-test-all.yml up -d   (Redis:42000, MySQL:43000)
#   ./gradlew testAll
#   docker compose ... down
```

`testAll` 태스크는 `gradle.taskGraph.whenReady` 훅에서 `spring.config.additional-location`을
`misc/application-test-all.yml`로 주입합니다. 일반 `./gradlew test`는 이 주입을 받지 않으므로
로컬 기본 DB(localhost:3306)를 바라봅니다.

단일 테스트:

```bash
./gradlew test --tests "com.example.tradedemo.domain.order.facade.OrderFacadeTest"
./gradlew test --tests "*CouponConcurrencyTest*"
```

주요 테스트: `CouponConcurrencyTest`(동시성 회귀), `OrderFacadeTest`(거래 정산),
`MemerItemServiceCacheTest`(캐시 동작), `ScenarioTest1`(엔드투엔드 시나리오).

### 12.3 프로파일

| 프로파일 | DDL | 특징 |
|---|---|---|
| `local` (기본) | `create-drop` | 더미 데이터 생성, `show-sql: true`, 디버그 API 활성 |
| `prod` | `update` | SQL 로그 off, Redis/JWT 환경변수 주입, 디버그·더미 비활성 |
| `test-all` | `create` | Redis 42000 / MySQL 43000 |

### 12.4 환경변수

| 키 | 용도 |
|---|---|
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | MySQL 접속 |
| `REDIS_HOST` / `REDIS_PORT` | Redis 접속 |
| `JWT_SECRET` | JWT 서명키 (최소 32자) |
| `GOOGLE_/KAKAO_/GITHUB_CLIENT_ID`, `..._SECRET` | OAuth2 클라이언트 |
| `DOCKERHUB_USERNAME` / `IMAGE_TAG` / `MYSQL_ROOT_PASSWORD` | 배포 compose |

`application.yml`의 기본값은 전부 플레이스홀더입니다. 운영에서는 반드시 주입해야 합니다.

### 12.5 더미 데이터

`common/initializer/dummy/`는 `prod`가 아닐 때만 동작합니다.

```yaml
dummy:
  enabled: true
  mode: market_listing_with_base   # member_only | item_only | member_item_with_base | market_listing_with_base
  member-count: 40000
  item-count: 1000
  member-item-count: 300000
  market-listing-count: 300000     # member-item-count 이하여야 함
```

`rewriteBatchedStatements=true`(local JDBC URL)와 배치 삽입으로 수십만 건 시딩 시간을 줄입니다.

---

## 13. 성능 테스트

```bash
docker compose -f docker-compose-k6.yml up -d influxdb grafana
docker compose -f docker-compose-k6.yml run --rm k6 \
  run --out influxdb=http://influxdb:8086/k6 /k6/item/browse-items.js
```

- 스크립트는 `k6/` 아래 도메인별로 정리되어 있으며, 캐시 유/무 대조 쌍으로 작성되어 있습니다
  (예: `all-memberItem-search-cache-test.js` ↔ `all-memberItem-search-no-cache-test.js`).
- 부하 프로파일별 스크립트: baseline / burst / spike.
- 공용 로그인·요청 헬퍼는 `k6/util/user-actions.js`, `k6/memberItem/common.js`.
- InfluxDB 1.8 초기 스키마는 `influxdb/init.iql`, 시각화는 Grafana(:3000).

**측정 시 주의**: k6 러너와 대상 애플리케이션을 같은 호스트에서 돌리면 ephemeral port 고갈로
클라이언트가 먼저 병목이 됩니다. 관련 분석이 `TROUBLE_SHOOTING.md`에 기록되어 있습니다.
InfluxDB/Grafana 출력 여부에 따라서도 수치가 달라지므로 비교군은 동일 조건에서 측정해야 합니다.

---

## 14. CI / CD

| 워크플로 | 트리거 | 내용 |
|---|---|---|
| `sanity-check.yml` | `dev` PR | `./gradlew build -x test -x spotlessCheck` — 빌드 가능 여부만 확인 |
| `ci.yml` | `main` PR | Redis 서비스 컨테이너 기동 후 `build -x test -x compileTestJava` |
| `cd.yml` | `main` / `dev` push | `bootJar` → Buildx(linux/arm64) → Docker Hub → AWS SSM으로 EC2 재배포 |

- **두 CI 모두 테스트를 실행하지 않습니다.** 외부 인프라(MySQL/Redis) 의존 때문이며,
  테스트 통과 여부는 로컬 `test-all`로 확인해야 합니다. Testcontainers 의존성은 이미 추가되어 있으므로
  CI 내 테스트 활성화가 향후 과제입니다.
- 배포 타깃이 `linux/arm64`입니다(EC2 Graviton). QEMU + Buildx로 크로스 빌드합니다.
- 이미지 태그는 `${{ github.sha }}`와 `latest` 두 개를 push하고, 배포는 SHA 태그로 고정해 재현성을 확보합니다.
- 컨테이너는 non-root(`spring:spring`) 사용자로 실행됩니다.
- 브랜치 전략: 기본/통합 브랜치는 `dev`, PR 대상도 `dev`. `main`은 배포 브랜치입니다.

---

## 15. 기술 부채 및 개선 항목

| 항목 | 현황 | 제안 |
|---|---|---|
| 실험용 다중 버전 API | V1~V5가 모두 노출 | 측정 종료 후 최종 버전만 유지 |
| CI 테스트 미실행 | 인프라 의존 | Testcontainers로 CI 내 테스트 활성화 |
| 로컬 캐시 일관성 | Caffeine은 인스턴스 로컬 | 다중 인스턴스 시 Redis 캐시 경로만 사용하거나 무효화 전파 |
| 스케줄러 중복 실행 | `@Scheduled`는 인스턴스마다 실행 | Quartz JDBC JobStore 또는 ShedLock |
| Redis 단일 노드 | `psubscribe` 제약으로 클러스터 회귀 | 패턴 구독 제거 후 클러스터 재검토 |
| 인프라 결합 | 단일 EC2에 App/DB/Redis 동거 | MySQL → RDS, Redis → ElastiCache 분리 |
| CORS 허용 오리진 | localhost:63342만 | 배포 오리진을 환경변수로 주입 |
| `ddl-auto` 사용 | `update` / `create-drop` | Flyway 등 마이그레이션 도구 도입 |
| 시크릿 기본값 | `application.yml`에 플레이스홀더 존재 | 운영 주입 강제 및 기본값 제거 |
| 낙관적 락 | `dev` 미포함 | `feat/coupon-lock` 통합 여부 결정 |

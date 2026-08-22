# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

가상 게임의 아이템 거래소를 구현한 Spring Boot e-commerce 학습 프로젝트입니다.
동시성 제어(비관적 락 / 분산락), 캐싱(Caffeine + Redis), 실시간 채팅(WebSocket + STOMP),
k6 부하 테스트를 실험하는 것이 목적이며, 그래서 **동일 기능이 여러 버전으로 공존합니다**(아래 "API 버저닝 규약" 참고).

비즈니스 규칙 원문은 `BUSINESS.md`, API 명세는 `API.md`, 팀별 트러블슈팅 기록은 `TROUBLE_SHOOTING.md`에 있습니다.

## 개발 명령어

빌드 (JDK 17 필요):

```bash
./gradlew build -x test          # 테스트 제외 빌드
./gradlew bootJar -x test        # 배포용 jar (CD가 쓰는 명령)
./gradlew spotlessApply          # palantir-java-format 포매팅 적용
```

`spotless`는 `enforceCheck false`로 꺼져 있습니다. 빌드가 포맷 때문에 깨지지 않으므로
포매팅은 필요할 때 명시적으로 `spotlessApply`를 실행합니다.

테스트 — **MySQL과 Redis가 떠 있어야 통과합니다.** Windows에서는 `test-all.bat`이 전 과정을 자동화합니다:

```bash
./test-all.bat                   # docker compose 기동 → gradlew testAll → 정리
```

수동으로 하려면:

```bash
docker compose -f ./misc/docker-compose-test-all.yml up -d   # Redis:42000, MySQL:43000
./gradlew testAll                                            # misc/application-test-all.yml 주입
docker compose -f ./misc/docker-compose-test-all.yml down
```

`testAll` 태스크는 `gradle.taskGraph.whenReady` 훅에서 `spring.config.additional-location`을
`misc/application-test-all.yml`로 덮어씁니다. 평범한 `./gradlew test`는 이 설정을 받지 않으므로
로컬 기본 DB(localhost:3306)를 바라봅니다.

단일 테스트 실행:

```bash
./gradlew test --tests "com.example.tradedemo.domain.order.facade.OrderFacadeTest"
./gradlew test --tests "*CouponConcurrencyTest.동시성*"
```

부하 테스트 (k6 + InfluxDB + Grafana):

```bash
docker compose -f docker-compose-k6.yml up -d influxdb grafana
docker compose -f docker-compose-k6.yml run --rm k6 run --out influxdb=http://influxdb:8086/k6 /k6/item/browse-items.js
```

개발용 도구 — Docker 이미지에 더미 데이터가 심긴 MySQL tar를 굽습니다(`db-images/`에 저장):

```bash
./gradlew runDockerBuilder      # src/tool/java/tool/TradeDockerBuilder
```

## 아키텍처

### 패키지 구조

`src/main/java/com/example/tradedemo/` 아래 세 축으로 나뉩니다.

- `auth/` — JWT + OAuth2(Google/Kakao/GitHub) 인증인가. 도메인이 아닌 횡단 관심사라 `domain/` 밖에 있습니다.
- `common/` — 공통 응답 DTO(`ApiResponse`, `PageResponse`), `Base` 감사 엔티티, 캐시/Redisson/WebSocket 설정,
  분산락 애노테이션+AOP, 전역 예외 처리, 더미 데이터 시딩.
- `domain/{chat,coupon,debug,item,marketlistings,members,order,pending,wallet}/` — 도메인별
  `controller / service / repository / entity / dto / enums`.

`src/tool/`은 별도 sourceSet입니다. `sourceSets.main.output`을 의존하므로 애플리케이션 코드를 재사용하지만
운영 jar에는 들어가지 않습니다.

### API 버저닝 규약 (이 저장소의 핵심 관습)

컨트롤러 경로에 `/api/v1`, `/api/v2`, `/api/v3` … 가 붙어 있는데 이는 **하위 호환용 버전이 아니라
같은 기능의 최적화 단계별 구현**입니다. 성능 비교 실험을 위해 이전 버전을 지우지 않고 남겨 둡니다.

대략적인 규칙:

- **V1** — 최적화 없는 기준 구현(no cache, DB 락만).
- **V2** — Caffeine 로컬 캐시(`@Cacheable` / `@CacheEvict`) 적용.
- **V3** — Redis 캐시 / Redis 분산락 적용.
- 도메인에 따라 V4, V5까지 있습니다. 예: `MarketListingService.createV4`는 Lettuce 기반 `@RedisLock`,
  `createV5`는 Redisson 기반 `@RedissonLock` + Redis 캐시가 최종본입니다.
- 쿠폰 선착순 발급은 `issueFirstComeCouponV3_1`(Lettuce `@RedisLock`)과
  `issueFirstComeCouponV3_2`(Redisson `@RedissonLock`)로 갈라져 `/api/v3-1/...`, `/api/v3-2/...`에 매핑됩니다.

기능을 수정할 때는 **어느 버전을 건드리는지 먼저 확인**하세요. 보통 최신 버전이 실제 사용 대상이고
낮은 버전은 벤치마크 대조군이라 손대면 안 됩니다.

### 동시성 제어

두 가지 커스텀 애노테이션이 있고 둘 다 `common/annotation` + `common/aspect`에 짝으로 있습니다.

- `@RedisLock` — Lettuce `RedisTemplate` 기반 SETNX 스핀락 (`RedisLockAspect`).
- `@RedissonLock` — Redisson `RLock` 기반 (`RedissonLockAspect`). pub/sub 대기라 스핀 대비 부하가 낮습니다.

둘 다 `key`가 SpEL이며 메서드 파라미터를 참조합니다
(예: `key = "'lock:market-listing:member:' + #memberId + ':item:' + #request.getMemberItemId()"`).

**락과 트랜잭션의 순서가 중요합니다.** Aspect가 `@Transactional`보다 바깥에서 돌아 락 획득 → 트랜잭션 시작 →
커밋 → 락 해제 순서를 보장합니다. 이 순서가 깨져 쿠폰 수량 정합성이 무너진 사례가 `TROUBLE_SHOOTING.md`에
기록돼 있으니, 락 관련 코드를 옮길 때 반드시 참고하세요.

### 캐시 계층

`common/config/CacheConfig`가 캐시 이름별로 Caffeine 인스턴스를 **명시적으로 등록**합니다
(`SimpleCacheManager`). 새 캐시 이름을 `@Cacheable`에 쓰려면 여기에 먼저 추가해야 하며,
등록하지 않으면 런타임에 캐시를 찾지 못합니다. TTL/최대 크기도 캐시별로 다릅니다.

같은 클래스에 Redis용 `RedisTemplate<String, Object>` 빈도 함께 정의돼 있습니다
(key는 String, value는 Jackson JSON 직렬화).

인기 검색어는 `MarketListingCacheService`에서 Redis ZSet(`opsForZSet().incrementScore`)으로 집계하고,
접두어 검색용 별도 ZSet(score 0)을 병행 유지합니다. 하루 단위로 초기화됩니다.

### 도메인 거래 흐름

거래는 즉시 정산이 아니라 **수령 대기(PendingAsset)** 를 거칩니다.

1. 판매자가 `MemberItem`을 `MarketListing`으로 등록 (status `SELLING`, 만료 12/24/48h).
2. 구매자가 구매 → `OrderFacade`가 한 트랜잭션에서 `Order` 생성, `Wallet` 차감(`WalletHistories` 기록),
   양측 `PendingAsset` 생성, 리스팅 status `SOLD`로 전이.
3. 판매자는 대금, 구매자는 아이템을 `PendingAsset` 수령 API로 회수. 미수령 시 스케줄러가 소멸시킵니다.

즉, `OrderFacade`는 여러 도메인 서비스를 조합하는 트랜잭션 경계이며, 도메인 서비스끼리는 서로를
직접 호출하지 않습니다. 도메인 간 조합이 필요하면 facade를 추가하세요(`order/facade`, `coupon/facade` 참고).

`MarketListingStatus`는 `SELLING → SOLD → CLAIMED` / `CANCELLED` / `EXPIRED`로 전이하며,
채팅 시스템 메시지도 이 status를 그대로 읽어 분기합니다.

### 스케줄링

Quartz(`spring-boot-starter-quartz`)와 Spring `@Scheduled`가 혼재합니다.

- `@Scheduled(cron = "0 0 0 * * *")` — 쿠폰 만료(`CouponExpiryScheduler`), 회원 장기 미접속 처리(`MemberScheduler`).
- Quartz `Job` — `domain/scheduler/BaseExpireJob`을 상속한 만료 처리
  (`MarketListingExpireJob`). 리스팅 만료는 00/06/12/18시에 확인하며, 등록 시각 기준으로
  최소 노출 시간을 보장하려고 다음 슬롯으로 밀립니다(`BUSINESS.md` 참고).

### 인증인가

`SecurityConfig`는 세션 없는(STATELESS) JWT 필터 체인입니다.
`JwtAuthenticationFilter`가 `CacheManager`(refreshTokens / blacklistedTokens 캐시)와 `RedisTemplate`을
직접 주입받아 토큰 블랙리스트를 확인합니다.

Spring Security가 던지는 401/403도 `ApiResponse`로 감싸기 위해 `CustomAuthenticationEntryPoint`,
`CustomAccessDeniedHandler`를 등록합니다. 필터 단계 예외는 `GlobalExceptionHandler`가 잡지 못하므로
이 핸들러 쪽을 수정해야 합니다.

소셜 로그인은 `SocialAccount` 엔티티로 `Member` 1: N 연동입니다. 소셜에서 email을 못 받는 경우
`{providerId}@{provider}.com` 형태의 가상 이메일을 만듭니다(학습용 정책, `BUSINESS.md`에 근거 명시).

STOMP 연결의 인증은 HTTP 필터가 아니라 `auth/interceptor/StompAuthInterceptor`가 담당합니다.

### 예외 처리

모든 도메인 예외는 `ServiceException(ErrorEnum.ERR_...)` 하나로 던집니다.
`ErrorEnum`이 HTTP status와 메시지를 함께 들고 있고 `GlobalExceptionHandler`가 `ApiResponse`로 변환합니다.
새 에러를 만들 때 예외 클래스를 새로 만들지 말고 `ErrorEnum` 상수를 추가하세요.

### 더미 데이터

`common/initializer/dummy/`가 `prod` 프로파일이 아닐 때만 동작합니다.
`dummy.mode`로 `member_only / item_only / member_item_with_base / market_listing_with_base` 중 선택하며,
부하 테스트용으로 수십만 건을 배치 삽입합니다(`application-local.yml`이 300k 건 설정을 담고 있음).
`market-listing-count`는 `member-item-count` 이하여야 합니다.

## 프로파일과 실행

- `local` (기본) — `ddl-auto: create-drop`, 더미 데이터 생성, `show-sql: true`.
- `prod` — SQL 로그 off, Redis 호스트/JWT를 환경변수에서 주입, 디버그 API 비활성.
- `test-all` — `misc/application-test-all.yml`, Redis 42000 / MySQL 43000.

실행에는 JDK 17, MySQL, Redis가 필요합니다. 로컬 인프라는 루트 `docker-compose.yml`이 아니라
(그건 배포용 이미지 태그를 참조합니다) `misc/docker-compose-test-all.yml`을 쓰는 편이 빠릅니다.

## CI/CD

- `sanity-check.yml` — `dev` PR에서 `./gradlew build -x test -x spotlessCheck`. **테스트를 돌리지 않습니다.**
- `ci.yml` — `main` PR에서 빌드(역시 `-x test`). Redis 서비스 컨테이너만 띄웁니다.
- `cd.yml` — `main`/`dev` push 시 `bootJar` → linux/arm64 Docker 이미지 → Docker Hub → AWS SSM으로 EC2 배포.

CI가 테스트를 건너뛰므로 **테스트 통과 여부는 로컬 `test-all`로 직접 확인해야 합니다.**

기본 브랜치는 `dev`이고 PR도 `dev`로 갑니다. `main`은 배포 브랜치입니다.

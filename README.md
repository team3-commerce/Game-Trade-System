# Game-Trade-System

게임 거래소를 주제로한 e-commerce구현 프로젝트 입니다.

이 프로젝트를 통해 저희는
- 동시성 제어
- caching
- 실시간 채팅
- CI/CD
- 성능 테스팅

을 공부해보고자 했습니다.

# 프로젝트 주제 소개

## 간단한 소개
저희는 가상의 게임서비스를 위한 아이템 거래소를 생성하였습니다.

거래서에서 사용자는 다음과 같은 행위들을 할 수 있습니다.

- 자신의 아이템을 판매
- 타인의 아이템을 구매
- 쿠폰신청
- 쿠폰 사용으로 게임머니 얻기

자세한 비지니스 규칙은 [BUSINESS.md](BUSINESS.md) 파일을 참고해주세요.

## 아이템 거래 흐름도

![diagram](readme/diagram.png)

# API 명세서

API 명세서는 [API.md](API.md)를 참고해주세요.

# ERD

![ERD](readme/ERD.png)

# 빌드

build를 위해서는 jdk 17이 필요합니다.

터미널에서 
```
./gradlew build -x test
```
입력하시면 됩니다.

# 실행

실행을 위해서는 jdk 17, Reids, MySql이 필요합니다.

## 실행 설정

기본적인 설정은 application.yml에 있습니다.

### 인증인가 설정

social login에 관한 설정은 https://docs.spring.io/spring-security/reference/servlet/oauth2/login/core.html을 참고해 주세요.

소셜 서비스별 Client ID 발급처

- Google : Google Cloud Console (https://console.cloud.google.com/) - OAuth 2.0 클라이언트 ID 생성
- Kakao : Kakao Developers (https://developers.kakao.com/) - 애플리케이션 등록 후 'REST API 키' 사용
- GitHub : GitHub Developer Settings (https://github.com/settings/developers) - New OAuth App 생성

### 디버깅/개발 설정

```
# 아래 설정들은 프로파일이 prod가 아닐 경우에만 돌아갑니다.

# dummy data들을 추가합니다
dummy:
  # dummy data를 추가합니다.
  enabled: true

  # dummy data를 무엇을 추가할지 설정합니다.
  #   member_only - 회원만 추가
  #   item_only - 아이템만 추가
  #   member_item_with_base - 회원, 아이템, 회원 아이템 추가
  #   market_listing_with_base - 회원, 아이템, 회원 아이템, 거래소 목록 추가
  #
  mode: member_only

  # 각 항목의 횟수를 정합니다.
  member-count: 100000
  item-count: 1000
  member-item-count: 100000
  market-listing-count: 50000 # member-item-count 보다 작거나 같은 경우에만 더미 데이터 생성 가능

app:
  debug-api:
    # debugging api를 활성화 합니다.
    enabled : true
  # 초기 쿠폰을 등록합니다.
  add-test-signup-coupon: true 
  # 초기 아이템을 등록합니다
  add-test-items: true
  # 초기 아이템, 회원, 회원 아이템을 등록합니다
  add-test-memberitems: true
  # 초기 아이템, 회원, 회원 아이템을 등록합니다
```

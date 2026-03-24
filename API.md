---
title: OpenAPI definition v0
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
highlight_theme: darkula
headingLevel: 2

---

<!-- Generator: Widdershins v4.0.1 -->

<h1 id="openapi-definition-auth-controller">auth-controller</h1>

## login

<a id="opIdlogin"></a>

`POST /api/v1/auth/login`

> Body parameter

```json
{
  "email": "string",
  "password": "string"
}
```

<h3 id="login-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[LoginAuthRequest](#schemaloginauthrequest)|true|none|

> Example responses

> 200 Response

<h3 id="login-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseTokenAuthResponse](#schemaapiresponsetokenauthresponse)|

 

## loginV2

<a id="opIdloginV2"></a>

`POST /api/v2/auth/login`

> Body parameter

```json
{
  "email": "string",
  "password": "string"
}
```

<h3 id="loginv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[LoginAuthRequest](#schemaloginauthrequest)|true|none|

> Example responses

> 200 Response

<h3 id="loginv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseTokenAuthResponse](#schemaapiresponsetokenauthresponse)|

 

## loginV3

<a id="opIdloginV3"></a>

`POST /api/v3/auth/login`

> Body parameter

```json
{
  "email": "string",
  "password": "string"
}
```

<h3 id="loginv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[LoginAuthRequest](#schemaloginauthrequest)|true|none|

> Example responses

> 200 Response

<h3 id="loginv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseTokenAuthResponse](#schemaapiresponsetokenauthresponse)|

 

## logout

<a id="opIdlogout"></a>

`POST /api/v1/auth/logout`

> Example responses

> 200 Response

<h3 id="logout-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## logoutV2

<a id="opIdlogoutV2"></a>

`POST /api/v2/auth/logout`

> Example responses

> 200 Response

<h3 id="logoutv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## logoutV3

<a id="opIdlogoutV3"></a>

`POST /api/v3/auth/logout`

> Example responses

> 200 Response

<h3 id="logoutv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## oauthSuccess

<a id="opIdoauthSuccess"></a>

`GET /api/auth/oauth-success`

<h3 id="oauthsuccess-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|accessToken|query|string|true|none|
|refreshToken|query|string|true|none|

> Example responses

> 200 Response

<h3 id="oauthsuccess-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseTokenAuthResponse](#schemaapiresponsetokenauthresponse)|

 

## reissue

<a id="opIdreissue"></a>

`POST /api/v1/auth/reissue`

> Body parameter

```json
{
  "refreshToken": "string"
}
```

<h3 id="reissue-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[TokenReissueRequest](#schematokenreissuerequest)|true|none|

> Example responses

> 200 Response

<h3 id="reissue-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseTokenAuthResponse](#schemaapiresponsetokenauthresponse)|

 

## reissueV2

<a id="opIdreissueV2"></a>

`POST /api/v2/auth/reissue`

> Body parameter

```json
{
  "refreshToken": "string"
}
```

<h3 id="reissuev2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[TokenReissueRequest](#schematokenreissuerequest)|true|none|

> Example responses

> 200 Response

<h3 id="reissuev2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseTokenAuthResponse](#schemaapiresponsetokenauthresponse)|

 

## reissueV3

<a id="opIdreissueV3"></a>

`POST /api/v3/auth/reissue`

> Body parameter

```json
{
  "refreshToken": "string"
}
```

<h3 id="reissuev3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[TokenReissueRequest](#schematokenreissuerequest)|true|none|

> Example responses

> 200 Response

<h3 id="reissuev3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseTokenAuthResponse](#schemaapiresponsetokenauthresponse)|

 

## setPasswordV2

<a id="opIdsetPasswordV2"></a>

`POST /api/v2/auth/set-password`

> Body parameter

```json
{
  "newPassword": "stringst"
}
```

<h3 id="setpasswordv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[SetPasswordRequest](#schemasetpasswordrequest)|true|none|

> Example responses

> 200 Response

<h3 id="setpasswordv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## setPasswordV3

<a id="opIdsetPasswordV3"></a>

`POST /api/v3/auth/set-password`

> Body parameter

```json
{
  "newPassword": "stringst"
}
```

<h3 id="setpasswordv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[SetPasswordRequest](#schemasetpasswordrequest)|true|none|

> Example responses

> 200 Response

<h3 id="setpasswordv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## signup

<a id="opIdsignup"></a>

`POST /api/v1/auth/signup`

> Body parameter

```json
{
  "email": "user@example.com",
  "nickname": "string",
  "password": "string",
  "role": "USER"
}
```

<h3 id="signup-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[SignupAuthRequest](#schemasignupauthrequest)|true|none|

> Example responses

> 200 Response

<h3 id="signup-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## unlinkSocialV2

<a id="opIdunlinkSocialV2"></a>

`POST /api/v2/auth/unlink-social`

> Body parameter

```json
{
  "provider": "GOOGLE"
}
```

<h3 id="unlinksocialv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[UnlinkSocialRequest](#schemaunlinksocialrequest)|true|none|

> Example responses

> 200 Response

<h3 id="unlinksocialv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## unlinkSocialV3

<a id="opIdunlinkSocialV3"></a>

`POST /api/v3/auth/unlink-social`

> Body parameter

```json
{
  "provider": "GOOGLE"
}
```

<h3 id="unlinksocialv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[UnlinkSocialRequest](#schemaunlinksocialrequest)|true|none|

> Example responses

> 200 Response

<h3 id="unlinksocialv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

<h1 id="openapi-definition-chat-controller">chat-controller</h1>

## getListingStatus

<a id="opIdgetListingStatus"></a>

`GET /api/chat/rooms/{roomId}/listing-status`

<h3 id="getlistingstatus-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|roomId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getlistingstatus-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseMarketListingStatus](#schemaapiresponsemarketlistingstatus)|

 

## getMyBuyerRooms

<a id="opIdgetMyBuyerRooms"></a>

`GET /api/chat/rooms/buyer`

> Example responses

> 200 Response

<h3 id="getmybuyerrooms-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseListChatRoomResponse](#schemaapiresponselistchatroomresponse)|

 

## getMyRooms

<a id="opIdgetMyRooms"></a>

`GET /api/chat/rooms`

> Example responses

> 200 Response

<h3 id="getmyrooms-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseListChatRoomResponse](#schemaapiresponselistchatroomresponse)|

 

## createRoom

<a id="opIdcreateRoom"></a>

`POST /api/chat/rooms`

> Body parameter

```json
{
  "listingId": 0,
  "sellerEmail": "string"
}
```

<h3 id="createroom-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[CreateRoomRequest](#schemacreateroomrequest)|true|none|

> Example responses

> 201 Response

<h3 id="createroom-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|success|[ApiResponseChatRoomResponse](#schemaapiresponsechatroomresponse)|

 

## getMySellerRooms

<a id="opIdgetMySellerRooms"></a>

`GET /api/chat/rooms/seller`

> Example responses

> 200 Response

<h3 id="getmysellerrooms-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseListChatRoomResponse](#schemaapiresponselistchatroomresponse)|

 

## getRoomMessages

<a id="opIdgetRoomMessages"></a>

`GET /api/chat/rooms/{roomId}/messages`

<h3 id="getroommessages-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|roomId|path|integer(int64)|true|none|
|lastMessageId|query|integer(int64)|false|none|
|size|query|integer(int32)|false|none|

> Example responses

> 200 Response

<h3 id="getroommessages-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseListChatMessageResponse](#schemaapiresponselistchatmessageresponse)|

 

<h1 id="openapi-definition-coupon-controller">coupon-controller</h1>

## createCouponPolicy

<a id="opIdcreateCouponPolicy"></a>

`POST /api/v1/admin/coupon-policies`

> Body parameter

```json
{
  "couponDuration": {
    "days": 0,
    "hours": 0,
    "minutes": 0,
    "seconds": 0
  },
  "issueType": "AUTO_SIGNUP",
  "moneyAmount": 0,
  "name": "string",
  "policyDuration": {
    "days": 0,
    "hours": 0,
    "minutes": 0,
    "seconds": 0
  },
  "totalQuantity": 0
}
```

<h3 id="createcouponpolicy-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[CreateCouponPolicyRequest](#schemacreatecouponpolicyrequest)|true|none|

> Example responses

> 201 Response

<h3 id="createcouponpolicy-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|success|[ApiResponseCreateCouponPolicyResponse](#schemaapiresponsecreatecouponpolicyresponse)|

 

## createCouponPolicyV2

<a id="opIdcreateCouponPolicyV2"></a>

`POST /api/v2/admin/coupon-policies`

> Body parameter

```json
{
  "couponDuration": {
    "days": 0,
    "hours": 0,
    "minutes": 0,
    "seconds": 0
  },
  "issueType": "AUTO_SIGNUP",
  "moneyAmount": 0,
  "name": "string",
  "policyDuration": {
    "days": 0,
    "hours": 0,
    "minutes": 0,
    "seconds": 0
  },
  "totalQuantity": 0
}
```

<h3 id="createcouponpolicyv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[CreateCouponPolicyRequest](#schemacreatecouponpolicyrequest)|true|none|

> Example responses

> 201 Response

<h3 id="createcouponpolicyv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|success|[ApiResponseCreateCouponPolicyResponse](#schemaapiresponsecreatecouponpolicyresponse)|

 

## createCouponPolicyV3

<a id="opIdcreateCouponPolicyV3"></a>

`POST /api/v3/admin/coupon-policies`

> Body parameter

```json
{
  "couponDuration": {
    "days": 0,
    "hours": 0,
    "minutes": 0,
    "seconds": 0
  },
  "issueType": "AUTO_SIGNUP",
  "moneyAmount": 0,
  "name": "string",
  "policyDuration": {
    "days": 0,
    "hours": 0,
    "minutes": 0,
    "seconds": 0
  },
  "totalQuantity": 0
}
```

<h3 id="createcouponpolicyv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[CreateCouponPolicyRequest](#schemacreatecouponpolicyrequest)|true|none|

> Example responses

> 201 Response

<h3 id="createcouponpolicyv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|success|[ApiResponseCreateCouponPolicyResponse](#schemaapiresponsecreatecouponpolicyresponse)|

 

## getAllCouponHistory

<a id="opIdgetAllCouponHistory"></a>

`GET /api/v1/me/coupons-histories`

<h3 id="getallcouponhistory-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int32)|false|none|
|status|query|string|false|none|
|sortCreatedAt|query|string|false|none|

> Example responses

> 200 Response

<h3 id="getallcouponhistory-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseSearchAllCouponHistoryResponse](#schemaapiresponsepageresponsesearchallcouponhistoryresponse)|

 

## getAllCouponHistoryV2

<a id="opIdgetAllCouponHistoryV2"></a>

`GET /api/v2/me/coupons-histories`

<h3 id="getallcouponhistoryv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int32)|false|none|
|status|query|string|false|none|
|sortCreatedAt|query|string|false|none|

> Example responses

> 200 Response

<h3 id="getallcouponhistoryv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseSearchAllCouponHistoryResponse](#schemaapiresponsepageresponsesearchallcouponhistoryresponse)|

 

## getAllCouponHistoryV3

<a id="opIdgetAllCouponHistoryV3"></a>

`GET /api/v3/me/coupons-histories`

<h3 id="getallcouponhistoryv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int32)|false|none|
|status|query|string|false|none|
|sortCreatedAt|query|string|false|none|

> Example responses

> 200 Response

<h3 id="getallcouponhistoryv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseSearchAllCouponHistoryResponse](#schemaapiresponsepageresponsesearchallcouponhistoryresponse)|

 

## getAllMemberCoupon

<a id="opIdgetAllMemberCoupon"></a>

`GET /api/v1/me/coupons`

<h3 id="getallmembercoupon-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int32)|false|none|
|status|query|string|false|none|

> Example responses

> 200 Response

<h3 id="getallmembercoupon-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseSearchAllMemberCouponResponse](#schemaapiresponsepageresponsesearchallmembercouponresponse)|

 

## getAllMemberCouponV2

<a id="opIdgetAllMemberCouponV2"></a>

`GET /api/v2/me/coupons`

<h3 id="getallmembercouponv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int32)|false|none|
|status|query|string|false|none|

> Example responses

> 200 Response

<h3 id="getallmembercouponv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseSearchAllMemberCouponResponse](#schemaapiresponsepageresponsesearchallmembercouponresponse)|

 

## getAllMemberCouponV3

<a id="opIdgetAllMemberCouponV3"></a>

`GET /api/v3/me/coupons`

<h3 id="getallmembercouponv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int32)|false|none|
|status|query|string|false|none|

> Example responses

> 200 Response

<h3 id="getallmembercouponv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseSearchAllMemberCouponResponse](#schemaapiresponsepageresponsesearchallmembercouponresponse)|

 

## getMemberCoupon

<a id="opIdgetMemberCoupon"></a>

`GET /api/v1/me/coupons/{couponId}`

<h3 id="getmembercoupon-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|couponId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getmembercoupon-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseSearchAllMemberCouponResponse](#schemaapiresponsesearchallmembercouponresponse)|

 

## getMemberCouponV2

<a id="opIdgetMemberCouponV2"></a>

`GET /api/v2/me/coupons/{couponId}`

<h3 id="getmembercouponv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|couponId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getmembercouponv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseSearchAllMemberCouponResponse](#schemaapiresponsesearchallmembercouponresponse)|

 

## getMemberCouponV3

<a id="opIdgetMemberCouponV3"></a>

`GET /api/v3/me/coupons/{couponId}`

<h3 id="getmembercouponv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|couponId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getmembercouponv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseSearchAllMemberCouponResponse](#schemaapiresponsesearchallmembercouponresponse)|

 

## issueFirstComeCoupon

<a id="opIdissueFirstComeCoupon"></a>

`POST /api/v1/coupon-policies/{couponPolicyId}/issue`

<h3 id="issuefirstcomecoupon-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|couponPolicyId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="issuefirstcomecoupon-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseString](#schemaapiresponsestring)|

 

## issueFirstComeCouponV2

<a id="opIdissueFirstComeCouponV2"></a>

`POST /api/v2/coupon-policies/{couponPolicyId}/issue`

<h3 id="issuefirstcomecouponv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|couponPolicyId|path|integer(int64)|true|none|

> Example responses

> 201 Response

<h3 id="issuefirstcomecouponv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|success|[ApiResponseString](#schemaapiresponsestring)|

 

## issueFirstComeCouponV3_1

<a id="opIdissueFirstComeCouponV3_1"></a>

`POST /api/v3-1/coupon-policies/{couponPolicyId}/issue`

<h3 id="issuefirstcomecouponv3_1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|couponPolicyId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="issuefirstcomecouponv3_1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseString](#schemaapiresponsestring)|

 

## issueFirstComeCouponV3_2

<a id="opIdissueFirstComeCouponV3_2"></a>

`POST /api/v3-2/coupon-policies/{couponPolicyId}/issue`

<h3 id="issuefirstcomecouponv3_2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|couponPolicyId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="issuefirstcomecouponv3_2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseString](#schemaapiresponsestring)|

 

## searchAllCouponPolicies

<a id="opIdsearchAllCouponPolicies"></a>

`GET /api/v1/couponPolicies`

<h3 id="searchallcouponpolicies-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int32)|false|none|
|sortCreatedAt|query|string|false|none|
|issueType|query|string|false|none|

> Example responses

> 200 Response

<h3 id="searchallcouponpolicies-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseSearchAllCouponPolicyResponse](#schemaapiresponsepageresponsesearchallcouponpolicyresponse)|

 

## searchAllCouponPoliciesV2

<a id="opIdsearchAllCouponPoliciesV2"></a>

`GET /api/v2/couponPolicies`

<h3 id="searchallcouponpoliciesv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int32)|false|none|
|sortCreatedAt|query|string|false|none|
|issueType|query|string|false|none|

> Example responses

> 200 Response

<h3 id="searchallcouponpoliciesv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseSearchAllCouponPolicyResponse](#schemaapiresponsepageresponsesearchallcouponpolicyresponse)|

 

## searchAllCouponPoliciesV3

<a id="opIdsearchAllCouponPoliciesV3"></a>

`GET /api/v3/couponPolicies`

<h3 id="searchallcouponpoliciesv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int32)|false|none|
|sortCreatedAt|query|string|false|none|
|issueType|query|string|false|none|

> Example responses

> 200 Response

<h3 id="searchallcouponpoliciesv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseSearchAllCouponPolicyResponse](#schemaapiresponsepageresponsesearchallcouponpolicyresponse)|

 

## useCoupon

<a id="opIduseCoupon"></a>

`POST /api/v1/me/coupons/{memberCouponId}/use`

<h3 id="usecoupon-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|memberCouponId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="usecoupon-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseString](#schemaapiresponsestring)|

 

## useCouponV2

<a id="opIduseCouponV2"></a>

`POST /api/v2/me/coupons/{memberCouponId}/use`

<h3 id="usecouponv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|memberCouponId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="usecouponv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseString](#schemaapiresponsestring)|

 

## useCouponV3

<a id="opIduseCouponV3"></a>

`POST /api/v3/me/coupons/{memberCouponId}/use`

<h3 id="usecouponv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|memberCouponId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="usecouponv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseString](#schemaapiresponsestring)|

 

<h1 id="openapi-definition-debug-controller">debug-controller</h1>

## getItem

<a id="opIdgetItem"></a>

`POST /api/debug/give-member-item`

> Body parameter

```json
{
  "acquiredAt": "2019-08-24T14:15:22Z",
  "itemId": 0,
  "memberEmail": "user@example.com",
  "quantity": 1
}
```

<h3 id="getitem-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[GiveMemberItemRequest](#schemagivememberitemrequest)|true|none|

> Example responses

> 200 Response

<h3 id="getitem-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseGetMemberItemResponse](#schemaapiresponsegetmemberitemresponse)|

 

<h1 id="openapi-definition-item-controller">item-controller</h1>

## getItem_1

<a id="opIdgetItem_1"></a>

`GET /api/v1/items/{itemId}`

<h3 id="getitem_1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|itemId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getitem_1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseGetItemResponse](#schemaapiresponsegetitemresponse)|

 

## getItemV2

<a id="opIdgetItemV2"></a>

`GET /api/v2/items/{itemId}`

<h3 id="getitemv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|itemId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getitemv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseGetItemResponse](#schemaapiresponsegetitemresponse)|

 

## getItemV3

<a id="opIdgetItemV3"></a>

`GET /api/v3/items/{itemId}`

<h3 id="getitemv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|itemId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getitemv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseGetItemResponse](#schemaapiresponsegetitemresponse)|

 

## getManyItem

<a id="opIdgetManyItem"></a>

`GET /api/v1/items`

<h3 id="getmanyitem-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|req|query|[SearchItemRequest](#schemasearchitemrequest)|true|none|

> Example responses

> 200 Response

<h3 id="getmanyitem-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseGetItemResponse](#schemaapiresponsepageresponsegetitemresponse)|

 

## getManyItemV2

<a id="opIdgetManyItemV2"></a>

`GET /api/v2/items`

<h3 id="getmanyitemv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|req|query|[SearchItemRequest](#schemasearchitemrequest)|true|none|

> Example responses

> 200 Response

<h3 id="getmanyitemv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseGetItemResponse](#schemaapiresponsepageresponsegetitemresponse)|

 

## getManyItemV3

<a id="opIdgetManyItemV3"></a>

`GET /api/v3/items`

<h3 id="getmanyitemv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|req|query|[SearchItemRequest](#schemasearchitemrequest)|true|none|

> Example responses

> 200 Response

<h3 id="getmanyitemv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseGetItemResponse](#schemaapiresponsepageresponsegetitemresponse)|

 

<h1 id="openapi-definition-market-listing-controller">market-listing-controller</h1>

## cancelMarketListingAdmin

<a id="opIdcancelMarketListingAdmin"></a>

`PATCH /api/v1/admin/market-listings/{marketListingId}`

<h3 id="cancelmarketlistingadmin-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|marketListingId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="cancelmarketlistingadmin-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseSearchMarketListingResponse](#schemaapiresponsesearchmarketlistingresponse)|

 

## createMarketListingV4

<a id="opIdcreateMarketListingV4"></a>

`POST /api/v4/market-listings`

> Body parameter

```json
{
  "memberItemId": 0,
  "quantity": 0,
  "salesDuration": "HOURS_12",
  "totalPrice": 0
}
```

<h3 id="createmarketlistingv4-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[CreateMarketListingRequest](#schemacreatemarketlistingrequest)|true|none|

> Example responses

> 200 Response

<h3 id="createmarketlistingv4-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseGetMarketListingResponse](#schemaapiresponsegetmarketlistingresponse)|

 

## createMarketListingV5

<a id="opIdcreateMarketListingV5"></a>

`POST /api/v5/market-listings`

> Body parameter

```json
{
  "memberItemId": 0,
  "quantity": 0,
  "salesDuration": "HOURS_12",
  "totalPrice": 0
}
```

<h3 id="createmarketlistingv5-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[CreateMarketListingRequest](#schemacreatemarketlistingrequest)|true|none|

> Example responses

> 200 Response

<h3 id="createmarketlistingv5-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseGetMarketListingResponse](#schemaapiresponsegetmarketlistingresponse)|

 

## getAllMarketListing

<a id="opIdgetAllMarketListing"></a>

`GET /api/v1/market-listings`

<h3 id="getallmarketlisting-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|none|
|sortTotalPrice|query|string|false|none|
|sortSaleEndAt|query|string|false|none|
|page|query|integer(int32)|false|none|

> Example responses

> 200 Response

<h3 id="getallmarketlisting-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseSearchAllMarketListingResponse](#schemaapiresponsepageresponsesearchallmarketlistingresponse)|

 

## createMarketListing

<a id="opIdcreateMarketListing"></a>

`POST /api/v1/market-listings`

> Body parameter

```json
{
  "memberItemId": 0,
  "quantity": 0,
  "salesDuration": "HOURS_12",
  "totalPrice": 0
}
```

<h3 id="createmarketlisting-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[CreateMarketListingRequest](#schemacreatemarketlistingrequest)|true|none|

> Example responses

> 200 Response

<h3 id="createmarketlisting-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseGetMarketListingResponse](#schemaapiresponsegetmarketlistingresponse)|

 

## getAllMarketListingV2

<a id="opIdgetAllMarketListingV2"></a>

`GET /api/v2/market-listings`

<h3 id="getallmarketlistingv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|none|
|sortTotalPrice|query|string|false|none|
|sortSaleEndAt|query|string|false|none|
|page|query|integer(int32)|false|none|

> Example responses

> 200 Response

<h3 id="getallmarketlistingv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseSearchAllMarketListingResponse](#schemaapiresponsepageresponsesearchallmarketlistingresponse)|

 

## createMarketListingV2

<a id="opIdcreateMarketListingV2"></a>

`POST /api/v2/market-listings`

> Body parameter

```json
{
  "memberItemId": 0,
  "quantity": 0,
  "salesDuration": "HOURS_12",
  "totalPrice": 0
}
```

<h3 id="createmarketlistingv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[CreateMarketListingRequest](#schemacreatemarketlistingrequest)|true|none|

> Example responses

> 200 Response

<h3 id="createmarketlistingv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseGetMarketListingResponse](#schemaapiresponsegetmarketlistingresponse)|

 

## getAllMarketListingV3

<a id="opIdgetAllMarketListingV3"></a>

`GET /api/v3/market-listings`

<h3 id="getallmarketlistingv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|none|
|sortTotalPrice|query|string|false|none|
|sortSaleEndAt|query|string|false|none|
|page|query|integer(int32)|false|none|

> Example responses

> 200 Response

<h3 id="getallmarketlistingv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseSearchAllMarketListingResponse](#schemaapiresponsepageresponsesearchallmarketlistingresponse)|

 

## createMarketListingV3

<a id="opIdcreateMarketListingV3"></a>

`POST /api/v3/market-listings`

> Body parameter

```json
{
  "memberItemId": 0,
  "quantity": 0,
  "salesDuration": "HOURS_12",
  "totalPrice": 0
}
```

<h3 id="createmarketlistingv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[CreateMarketListingRequest](#schemacreatemarketlistingrequest)|true|none|

> Example responses

> 200 Response

<h3 id="createmarketlistingv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseGetMarketListingResponse](#schemaapiresponsegetmarketlistingresponse)|

 

## getAllMeMarketListing

<a id="opIdgetAllMeMarketListing"></a>

`GET /api/v1/me/market-listings`

<h3 id="getallmemarketlisting-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyword|query|string|false|none|
|sortTotalPrice|query|string|false|none|
|sortSaleEndAt|query|string|false|none|
|page|query|integer(int32)|false|none|

> Example responses

> 200 Response

<h3 id="getallmemarketlisting-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseSearchAllMarketListingResponse](#schemaapiresponsepageresponsesearchallmarketlistingresponse)|

 

## getMarketListing

<a id="opIdgetMarketListing"></a>

`GET /api/v1/market-listings/{marketListingId}`

<h3 id="getmarketlisting-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|marketListingId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getmarketlisting-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseSearchMarketListingResponse](#schemaapiresponsesearchmarketlistingresponse)|

 

## cancelMarketListing

<a id="opIdcancelMarketListing"></a>

`PATCH /api/v1/market-listings/{marketListingId}`

<h3 id="cancelmarketlisting-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|marketListingId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="cancelmarketlisting-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseSearchMarketListingResponse](#schemaapiresponsesearchmarketlistingresponse)|

 

## getMarketListingV2

<a id="opIdgetMarketListingV2"></a>

`GET /api/v2/market-listings/{marketListingId}`

<h3 id="getmarketlistingv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|marketListingId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getmarketlistingv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseSearchMarketListingResponse](#schemaapiresponsesearchmarketlistingresponse)|

 

## cancelMarketListingV2

<a id="opIdcancelMarketListingV2"></a>

`PATCH /api/v2/market-listings/{marketListingId}`

<h3 id="cancelmarketlistingv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|marketListingId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="cancelmarketlistingv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseSearchMarketListingResponse](#schemaapiresponsesearchmarketlistingresponse)|

 

## getMarketListingV3

<a id="opIdgetMarketListingV3"></a>

`GET /api/v3/market-listings/{marketListingId}`

<h3 id="getmarketlistingv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|marketListingId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getmarketlistingv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseSearchMarketListingResponse](#schemaapiresponsesearchmarketlistingresponse)|

 

## cancelMarketListingV3

<a id="opIdcancelMarketListingV3"></a>

`PATCH /api/v3/market-listings/{marketListingId}`

<h3 id="cancelmarketlistingv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|marketListingId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="cancelmarketlistingv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseSearchMarketListingResponse](#schemaapiresponsesearchmarketlistingresponse)|

 

## getTrendingKeywords

<a id="opIdgetTrendingKeywords"></a>

`GET /api/v1/market-listings/search-popular`

<h3 id="gettrendingkeywords-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|prefixKeyword|query|string|false|none|

> Example responses

> 200 Response

<h3 id="gettrendingkeywords-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseListSearchTrendingKeywordResponse](#schemaapiresponselistsearchtrendingkeywordresponse)|

 

<h1 id="openapi-definition-order-controller">order-controller</h1>

## purchase

<a id="opIdpurchase"></a>

`POST /api/v1/market-listings/{marketListingId}`

<h3 id="purchase-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|marketListingId|path|integer(int64)|true|none|

<h3 id="purchase-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|None|

 

## purchaseV2

<a id="opIdpurchaseV2"></a>

`POST /api/v2/market-listings/{marketListingId}`

<h3 id="purchasev2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|marketListingId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="purchasev2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseCreateOrderResponse](#schemaapiresponsecreateorderresponse)|

 

## purchaseV3

<a id="opIdpurchaseV3"></a>

`POST /api/v3/market-listings/{marketListingId}`

<h3 id="purchasev3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|marketListingId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="purchasev3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseCreateOrderResponse](#schemaapiresponsecreateorderresponse)|

 

## getMyBuyer

<a id="opIdgetMyBuyer"></a>

`GET /api/v1/me/purchases`

> Example responses

> 200 Response

<h3 id="getmybuyer-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|Inline|

<h3 id="getmybuyer-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[GetTransactionResponse](#schemagettransactionresponse)]|false|none|none|
|» createdAt|string(date-time)|false|none|none|
|» marketListingId|integer(int64)|false|none|none|
|» orderId|integer(int64)|false|none|none|
|» transactionMoney|number|false|none|none|
|» transactionStock|integer(int64)|false|none|none|

 

## getMySeller

<a id="opIdgetMySeller"></a>

`GET /api/v1/me/sales`

> Example responses

> 200 Response

<h3 id="getmyseller-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|Inline|

<h3 id="getmyseller-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[GetTransactionResponse](#schemagettransactionresponse)]|false|none|none|
|» createdAt|string(date-time)|false|none|none|
|» marketListingId|integer(int64)|false|none|none|
|» orderId|integer(int64)|false|none|none|
|» transactionMoney|number|false|none|none|
|» transactionStock|integer(int64)|false|none|none|

 

<h1 id="openapi-definition-member-controller">member-controller</h1>

## deleteMyinfo

<a id="opIddeleteMyinfo"></a>

`DELETE /api/v1/me`

> Example responses

> 200 Response

<h3 id="deletemyinfo-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## getMyInfo

<a id="opIdgetMyInfo"></a>

`GET /api/v1/me`

> Example responses

> 200 Response

<h3 id="getmyinfo-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseGetMyInfoResponse](#schemaapiresponsegetmyinforesponse)|

 

## deleteMyinfoV2

<a id="opIddeleteMyinfoV2"></a>

`DELETE /api/v2/me`

> Example responses

> 200 Response

<h3 id="deletemyinfov2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## getMyInfoV2

<a id="opIdgetMyInfoV2"></a>

`GET /api/v2/me`

> Example responses

> 200 Response

<h3 id="getmyinfov2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseGetMyInfoResponse](#schemaapiresponsegetmyinforesponse)|

 

## deleteMyinfoV3

<a id="opIddeleteMyinfoV3"></a>

`DELETE /api/v3/me`

> Example responses

> 200 Response

<h3 id="deletemyinfov3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## getMyInfoV3

<a id="opIdgetMyInfoV3"></a>

`GET /api/v3/me`

> Example responses

> 200 Response

<h3 id="getmyinfov3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseGetMyInfoResponse](#schemaapiresponsegetmyinforesponse)|

 

## suspend

<a id="opIdsuspend"></a>

`PATCH /api/v1/admin/suspend`

> Body parameter

```json
{
  "email": "string",
  "reason": "string"
}
```

<h3 id="suspend-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[SuspendMemberRequest](#schemasuspendmemberrequest)|true|none|

> Example responses

> 200 Response

<h3 id="suspend-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## suspendV2

<a id="opIdsuspendV2"></a>

`PATCH /api/v2/admin/suspend`

> Body parameter

```json
{
  "email": "string",
  "reason": "string"
}
```

<h3 id="suspendv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[SuspendMemberRequest](#schemasuspendmemberrequest)|true|none|

> Example responses

> 200 Response

<h3 id="suspendv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## suspendV3

<a id="opIdsuspendV3"></a>

`PATCH /api/v3/admin/suspend`

> Body parameter

```json
{
  "email": "string",
  "reason": "string"
}
```

<h3 id="suspendv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[SuspendMemberRequest](#schemasuspendmemberrequest)|true|none|

> Example responses

> 200 Response

<h3 id="suspendv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## updateNickname

<a id="opIdupdateNickname"></a>

`PATCH /api/v1/me/nickname`

> Body parameter

```json
{
  "nickname": "string"
}
```

<h3 id="updatenickname-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[UpdateNicknameRequest](#schemaupdatenicknamerequest)|true|none|

> Example responses

> 200 Response

<h3 id="updatenickname-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## updateNicknameV2

<a id="opIdupdateNicknameV2"></a>

`PATCH /api/v2/me/nickname`

> Body parameter

```json
{
  "nickname": "string"
}
```

<h3 id="updatenicknamev2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[UpdateNicknameRequest](#schemaupdatenicknamerequest)|true|none|

> Example responses

> 200 Response

<h3 id="updatenicknamev2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## updateNicknameV3

<a id="opIdupdateNicknameV3"></a>

`PATCH /api/v3/me/nickname`

> Body parameter

```json
{
  "nickname": "string"
}
```

<h3 id="updatenicknamev3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[UpdateNicknameRequest](#schemaupdatenicknamerequest)|true|none|

> Example responses

> 200 Response

<h3 id="updatenicknamev3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## updatePassword

<a id="opIdupdatePassword"></a>

`PATCH /api/v1/me/password`

> Body parameter

```json
{
  "currentPassword": "string",
  "newPassword": "string"
}
```

<h3 id="updatepassword-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[UpdatePasswordRequest](#schemaupdatepasswordrequest)|true|none|

> Example responses

> 200 Response

<h3 id="updatepassword-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## updatePasswordV2

<a id="opIdupdatePasswordV2"></a>

`PATCH /api/v2/me/password`

> Body parameter

```json
{
  "currentPassword": "string",
  "newPassword": "string"
}
```

<h3 id="updatepasswordv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[UpdatePasswordRequest](#schemaupdatepasswordrequest)|true|none|

> Example responses

> 200 Response

<h3 id="updatepasswordv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## updatePasswordV3

<a id="opIdupdatePasswordV3"></a>

`PATCH /api/v3/me/password`

> Body parameter

```json
{
  "currentPassword": "string",
  "newPassword": "string"
}
```

<h3 id="updatepasswordv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[UpdatePasswordRequest](#schemaupdatepasswordrequest)|true|none|

> Example responses

> 200 Response

<h3 id="updatepasswordv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

<h1 id="openapi-definition-member-item-controller">member-item-controller</h1>

## getAllMemberItem

<a id="opIdgetAllMemberItem"></a>

`GET /api/v1/me/items`

<h3 id="getallmemberitem-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int32)|false|none|

> Example responses

> 200 Response

<h3 id="getallmemberitem-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseGetAllMemberItemResponse](#schemaapiresponsepageresponsegetallmemberitemresponse)|

 

## getAllMemberItemV2

<a id="opIdgetAllMemberItemV2"></a>

`GET /api/v2/me/items`

<h3 id="getallmemberitemv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int32)|false|none|

> Example responses

> 200 Response

<h3 id="getallmemberitemv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseGetAllMemberItemResponse](#schemaapiresponsepageresponsegetallmemberitemresponse)|

 

## getAllMemberItemV3

<a id="opIdgetAllMemberItemV3"></a>

`GET /api/v3/me/items`

<h3 id="getallmemberitemv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|page|query|integer(int32)|false|none|

> Example responses

> 200 Response

<h3 id="getallmemberitemv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponsePageResponseGetAllMemberItemResponse](#schemaapiresponsepageresponsegetallmemberitemresponse)|

 

## getMemberItem

<a id="opIdgetMemberItem"></a>

`GET /api/v1/me/items/{memberItemId}`

<h3 id="getmemberitem-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|memberItemId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getmemberitem-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseGetMemberItemResponse](#schemaapiresponsegetmemberitemresponse)|

 

## getMemberItemV2

<a id="opIdgetMemberItemV2"></a>

`GET /api/v2/me/items/{memberItemId}`

<h3 id="getmemberitemv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|memberItemId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getmemberitemv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseGetMemberItemResponse](#schemaapiresponsegetmemberitemresponse)|

 

## getMemberItemV3

<a id="opIdgetMemberItemV3"></a>

`GET /api/v3/me/items/{memberItemId}`

<h3 id="getmemberitemv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|memberItemId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getmemberitemv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseGetMemberItemResponse](#schemaapiresponsegetmemberitemresponse)|

 

<h1 id="openapi-definition-pending-asset-controller">pending-asset-controller</h1>

## getPendingAssets

<a id="opIdgetPendingAssets"></a>

`GET /api/v1/me/pending-assets`

> Example responses

> 200 Response

<h3 id="getpendingassets-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseListPendingAssetResponse](#schemaapiresponselistpendingassetresponse)|

 

## settlement

<a id="opIdsettlement"></a>

`POST /api/v1/me/pending-assets/{pendingAssetId}`

<h3 id="settlement-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|pendingAssetId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="settlement-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## settlementV2

<a id="opIdsettlementV2"></a>

`POST /api/v2/me/pending-assets/{pendingAssetId}`

<h3 id="settlementv2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|pendingAssetId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="settlementv2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

## settlementV3

<a id="opIdsettlementV3"></a>

`POST /api/v3/me/pending-assets/{pendingAssetId}`

<h3 id="settlementv3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|pendingAssetId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="settlementv3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[ApiResponseVoid](#schemaapiresponsevoid)|

 

<h1 id="openapi-definition-wallet-controller">wallet-controller</h1>

## getMyWallet

<a id="opIdgetMyWallet"></a>

`GET /api/v1/me/wallet`

> Example responses

> 200 Response

<h3 id="getmywallet-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|success|[WalletResponse](#schemawalletresponse)|

 

# Schemas

<h2 id="tocS_ApiResponseChatRoomResponse">ApiResponseChatRoomResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsechatroomresponse"></a>
<a id="schema_ApiResponseChatRoomResponse"></a>
<a id="tocSapiresponsechatroomresponse"></a>
<a id="tocsapiresponsechatroomresponse"></a>

```json
{
  "code": "string",
  "data": {
    "createdAt": "2019-08-24T14:15:22Z",
    "displayName": "string",
    "id": 0,
    "listingId": 0,
    "listingName": "string",
    "myRole": "string",
    "name": "string"
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[ChatRoomResponse](#schemachatroomresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseCreateCouponPolicyResponse">ApiResponseCreateCouponPolicyResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsecreatecouponpolicyresponse"></a>
<a id="schema_ApiResponseCreateCouponPolicyResponse"></a>
<a id="tocSapiresponsecreatecouponpolicyresponse"></a>
<a id="tocsapiresponsecreatecouponpolicyresponse"></a>

```json
{
  "code": "string",
  "data": {
    "couponDurationSeconds": 0,
    "createdAt": "2019-08-24T14:15:22Z",
    "expendQuantity": 0,
    "id": 0,
    "issueType": "AUTO_SIGNUP",
    "modifiedAt": "2019-08-24T14:15:22Z",
    "moneyAmount": 0,
    "name": "string",
    "policyDurationSeconds": 0,
    "policyExpiredAt": "2019-08-24T14:15:22Z",
    "policyStartedAt": "2019-08-24T14:15:22Z",
    "totalQuantity": 0
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[CreateCouponPolicyResponse](#schemacreatecouponpolicyresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseCreateOrderResponse">ApiResponseCreateOrderResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsecreateorderresponse"></a>
<a id="schema_ApiResponseCreateOrderResponse"></a>
<a id="tocSapiresponsecreateorderresponse"></a>
<a id="tocsapiresponsecreateorderresponse"></a>

```json
{
  "code": "string",
  "data": {
    "itemName": "string",
    "orderId": 0,
    "transactionMoney": 0,
    "transactionStock": 0
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[CreateOrderResponse](#schemacreateorderresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseGetItemResponse">ApiResponseGetItemResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsegetitemresponse"></a>
<a id="schema_ApiResponseGetItemResponse"></a>
<a id="tocSapiresponsegetitemresponse"></a>
<a id="tocsapiresponsegetitemresponse"></a>

```json
{
  "code": "string",
  "data": {
    "itemId": 0,
    "itemName": "string",
    "itemType": "EQUIPMENT"
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[GetItemResponse](#schemagetitemresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseGetMarketListingResponse">ApiResponseGetMarketListingResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsegetmarketlistingresponse"></a>
<a id="schema_ApiResponseGetMarketListingResponse"></a>
<a id="tocSapiresponsegetmarketlistingresponse"></a>
<a id="tocsapiresponsegetmarketlistingresponse"></a>

```json
{
  "code": "string",
  "data": {
    "createdAt": "2019-08-24T14:15:22Z",
    "item": {
      "itemId": 0,
      "itemName": "string",
      "itemType": "EQUIPMENT"
    },
    "marketListingId": 0,
    "marketListingStatus": "SELLING",
    "modifiedAt": "2019-08-24T14:15:22Z",
    "quantity": 0,
    "saleEndAt": "2019-08-24T14:15:22Z",
    "totalPrice": 0,
    "unitPrice": 0
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[GetMarketListingResponse](#schemagetmarketlistingresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseGetMemberItemResponse">ApiResponseGetMemberItemResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsegetmemberitemresponse"></a>
<a id="schema_ApiResponseGetMemberItemResponse"></a>
<a id="tocSapiresponsegetmemberitemresponse"></a>
<a id="tocsapiresponsegetmemberitemresponse"></a>

```json
{
  "code": "string",
  "data": {
    "acquiredAt": "2019-08-24T14:15:22Z",
    "createdAt": "2019-08-24T14:15:22Z",
    "itemName": "string",
    "memberItemId": 0,
    "modifiedAt": "2019-08-24T14:15:22Z",
    "quantity": 0
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[GetMemberItemResponse](#schemagetmemberitemresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseGetMyInfoResponse">ApiResponseGetMyInfoResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsegetmyinforesponse"></a>
<a id="schema_ApiResponseGetMyInfoResponse"></a>
<a id="tocSapiresponsegetmyinforesponse"></a>
<a id="tocsapiresponsegetmyinforesponse"></a>

```json
{
  "code": "string",
  "data": {
    "email": "string",
    "nickname": "string",
    "role": "USER"
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[GetMyInfoResponse](#schemagetmyinforesponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseListChatMessageResponse">ApiResponseListChatMessageResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselistchatmessageresponse"></a>
<a id="schema_ApiResponseListChatMessageResponse"></a>
<a id="tocSapiresponselistchatmessageresponse"></a>
<a id="tocsapiresponselistchatmessageresponse"></a>

```json
{
  "code": "string",
  "data": [
    {
      "content": "string",
      "createdAt": "2019-08-24T14:15:22Z",
      "messageId": 0,
      "senderId": 0,
      "senderNickname": "string"
    }
  ],
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[[ChatMessageResponse](#schemachatmessageresponse)]|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseListChatRoomResponse">ApiResponseListChatRoomResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselistchatroomresponse"></a>
<a id="schema_ApiResponseListChatRoomResponse"></a>
<a id="tocSapiresponselistchatroomresponse"></a>
<a id="tocsapiresponselistchatroomresponse"></a>

```json
{
  "code": "string",
  "data": [
    {
      "createdAt": "2019-08-24T14:15:22Z",
      "displayName": "string",
      "id": 0,
      "listingId": 0,
      "listingName": "string",
      "myRole": "string",
      "name": "string"
    }
  ],
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[[ChatRoomResponse](#schemachatroomresponse)]|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseListPendingAssetResponse">ApiResponseListPendingAssetResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselistpendingassetresponse"></a>
<a id="schema_ApiResponseListPendingAssetResponse"></a>
<a id="tocSapiresponselistpendingassetresponse"></a>
<a id="tocsapiresponselistpendingassetresponse"></a>

```json
{
  "code": "string",
  "data": [
    {
      "itemQuantity": 0,
      "moneyAmount": 0,
      "pendingAssetId": 0,
      "type": "MONEY"
    }
  ],
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[[PendingAssetResponse](#schemapendingassetresponse)]|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseListSearchTrendingKeywordResponse">ApiResponseListSearchTrendingKeywordResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselistsearchtrendingkeywordresponse"></a>
<a id="schema_ApiResponseListSearchTrendingKeywordResponse"></a>
<a id="tocSapiresponselistsearchtrendingkeywordresponse"></a>
<a id="tocsapiresponselistsearchtrendingkeywordresponse"></a>

```json
{
  "code": "string",
  "data": [
    {
      "keyword": "string",
      "searchCount": 0
    }
  ],
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[[SearchTrendingKeywordResponse](#schemasearchtrendingkeywordresponse)]|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseMarketListingStatus">ApiResponseMarketListingStatus</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsemarketlistingstatus"></a>
<a id="schema_ApiResponseMarketListingStatus"></a>
<a id="tocSapiresponsemarketlistingstatus"></a>
<a id="tocsapiresponsemarketlistingstatus"></a>

```json
{
  "code": "string",
  "data": "SELLING",
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|string|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|data|SELLING|
|data|SOLD|
|data|CLAIMED|
|data|CANCELLED|
|data|EXPIRED|

<h2 id="tocS_ApiResponsePageResponseGetAllMemberItemResponse">ApiResponsePageResponseGetAllMemberItemResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsepageresponsegetallmemberitemresponse"></a>
<a id="schema_ApiResponsePageResponseGetAllMemberItemResponse"></a>
<a id="tocSapiresponsepageresponsegetallmemberitemresponse"></a>
<a id="tocsapiresponsepageresponsegetallmemberitemresponse"></a>

```json
{
  "code": "string",
  "data": {
    "content": [
      {
        "acquiredAt": "2019-08-24T14:15:22Z",
        "createdAt": "2019-08-24T14:15:22Z",
        "itemName": "string",
        "memberItemId": 0,
        "modifiedAt": "2019-08-24T14:15:22Z",
        "quantity": 0
      }
    ],
    "pageNumber": 0,
    "pageSize": 0,
    "totalElements": 0,
    "totalPages": 0
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[PageResponseGetAllMemberItemResponse](#schemapageresponsegetallmemberitemresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponsePageResponseGetItemResponse">ApiResponsePageResponseGetItemResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsepageresponsegetitemresponse"></a>
<a id="schema_ApiResponsePageResponseGetItemResponse"></a>
<a id="tocSapiresponsepageresponsegetitemresponse"></a>
<a id="tocsapiresponsepageresponsegetitemresponse"></a>

```json
{
  "code": "string",
  "data": {
    "content": [
      {
        "itemId": 0,
        "itemName": "string",
        "itemType": "EQUIPMENT"
      }
    ],
    "pageNumber": 0,
    "pageSize": 0,
    "totalElements": 0,
    "totalPages": 0
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[PageResponseGetItemResponse](#schemapageresponsegetitemresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponsePageResponseSearchAllCouponHistoryResponse">ApiResponsePageResponseSearchAllCouponHistoryResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsepageresponsesearchallcouponhistoryresponse"></a>
<a id="schema_ApiResponsePageResponseSearchAllCouponHistoryResponse"></a>
<a id="tocSapiresponsepageresponsesearchallcouponhistoryresponse"></a>
<a id="tocsapiresponsepageresponsesearchallcouponhistoryresponse"></a>

```json
{
  "code": "string",
  "data": {
    "content": [
      {
        "couponHistoryId": 0,
        "couponName": "string",
        "createdAt": "2019-08-24T14:15:22Z",
        "modifiedAt": "2019-08-24T14:15:22Z",
        "moneyAmount": 0,
        "status": "USED",
        "usedAt": "2019-08-24T14:15:22Z"
      }
    ],
    "pageNumber": 0,
    "pageSize": 0,
    "totalElements": 0,
    "totalPages": 0
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[PageResponseSearchAllCouponHistoryResponse](#schemapageresponsesearchallcouponhistoryresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponsePageResponseSearchAllCouponPolicyResponse">ApiResponsePageResponseSearchAllCouponPolicyResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsepageresponsesearchallcouponpolicyresponse"></a>
<a id="schema_ApiResponsePageResponseSearchAllCouponPolicyResponse"></a>
<a id="tocSapiresponsepageresponsesearchallcouponpolicyresponse"></a>
<a id="tocsapiresponsepageresponsesearchallcouponpolicyresponse"></a>

```json
{
  "code": "string",
  "data": {
    "content": [
      {
        "couponDuration": {
          "days": 0,
          "hours": 0,
          "minutes": 0,
          "seconds": 0
        },
        "createdAt": "2019-08-24T14:15:22Z",
        "expendQuantity": 0,
        "id": 0,
        "issueType": "AUTO_SIGNUP",
        "modifiedAt": "2019-08-24T14:15:22Z",
        "moneyAmount": 0,
        "name": "string",
        "policyDuration": {
          "days": 0,
          "hours": 0,
          "minutes": 0,
          "seconds": 0
        },
        "policyExpiredAt": "2019-08-24T14:15:22Z",
        "policyStartedAt": "2019-08-24T14:15:22Z",
        "totalQuantity": 0
      }
    ],
    "pageNumber": 0,
    "pageSize": 0,
    "totalElements": 0,
    "totalPages": 0
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[PageResponseSearchAllCouponPolicyResponse](#schemapageresponsesearchallcouponpolicyresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponsePageResponseSearchAllMarketListingResponse">ApiResponsePageResponseSearchAllMarketListingResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsepageresponsesearchallmarketlistingresponse"></a>
<a id="schema_ApiResponsePageResponseSearchAllMarketListingResponse"></a>
<a id="tocSapiresponsepageresponsesearchallmarketlistingresponse"></a>
<a id="tocsapiresponsepageresponsesearchallmarketlistingresponse"></a>

```json
{
  "code": "string",
  "data": {
    "content": [
      {
        "createdAt": "2019-08-24T14:15:22Z",
        "itemName": "string",
        "marketListingId": 0,
        "modifiedAt": "2019-08-24T14:15:22Z",
        "quantity": 0,
        "saleEndAt": "2019-08-24T14:15:22Z",
        "status": "SELLING",
        "totalPrice": 0
      }
    ],
    "pageNumber": 0,
    "pageSize": 0,
    "totalElements": 0,
    "totalPages": 0
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[PageResponseSearchAllMarketListingResponse](#schemapageresponsesearchallmarketlistingresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponsePageResponseSearchAllMemberCouponResponse">ApiResponsePageResponseSearchAllMemberCouponResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsepageresponsesearchallmembercouponresponse"></a>
<a id="schema_ApiResponsePageResponseSearchAllMemberCouponResponse"></a>
<a id="tocSapiresponsepageresponsesearchallmembercouponresponse"></a>
<a id="tocsapiresponsepageresponsesearchallmembercouponresponse"></a>

```json
{
  "code": "string",
  "data": {
    "content": [
      {
        "couponDuration": {
          "days": 0,
          "hours": 0,
          "minutes": 0,
          "seconds": 0
        },
        "couponName": "string",
        "createdAt": "2019-08-24T14:15:22Z",
        "expiredAt": "2019-08-24T14:15:22Z",
        "issuedAt": "2019-08-24T14:15:22Z",
        "memberCouponId": 0,
        "modifiedAt": "2019-08-24T14:15:22Z",
        "moneyAmount": 0,
        "status": "UNUSED"
      }
    ],
    "pageNumber": 0,
    "pageSize": 0,
    "totalElements": 0,
    "totalPages": 0
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[PageResponseSearchAllMemberCouponResponse](#schemapageresponsesearchallmembercouponresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseSearchAllMemberCouponResponse">ApiResponseSearchAllMemberCouponResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsesearchallmembercouponresponse"></a>
<a id="schema_ApiResponseSearchAllMemberCouponResponse"></a>
<a id="tocSapiresponsesearchallmembercouponresponse"></a>
<a id="tocsapiresponsesearchallmembercouponresponse"></a>

```json
{
  "code": "string",
  "data": {
    "couponDuration": {
      "days": 0,
      "hours": 0,
      "minutes": 0,
      "seconds": 0
    },
    "couponName": "string",
    "createdAt": "2019-08-24T14:15:22Z",
    "expiredAt": "2019-08-24T14:15:22Z",
    "issuedAt": "2019-08-24T14:15:22Z",
    "memberCouponId": 0,
    "modifiedAt": "2019-08-24T14:15:22Z",
    "moneyAmount": 0,
    "status": "UNUSED"
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[SearchAllMemberCouponResponse](#schemasearchallmembercouponresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseSearchMarketListingResponse">ApiResponseSearchMarketListingResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsesearchmarketlistingresponse"></a>
<a id="schema_ApiResponseSearchMarketListingResponse"></a>
<a id="tocSapiresponsesearchmarketlistingresponse"></a>
<a id="tocsapiresponsesearchmarketlistingresponse"></a>

```json
{
  "code": "string",
  "data": {
    "createdAt": "2019-08-24T14:15:22Z",
    "itemName": "string",
    "marketListingId": 0,
    "modifiedAt": "2019-08-24T14:15:22Z",
    "quantity": 0,
    "saleEndAt": "2019-08-24T14:15:22Z",
    "sellerEmail": "string",
    "sellerNickname": "string",
    "status": "SELLING",
    "totalPrice": 0
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[SearchMarketListingResponse](#schemasearchmarketlistingresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseString">ApiResponseString</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsestring"></a>
<a id="schema_ApiResponseString"></a>
<a id="tocSapiresponsestring"></a>
<a id="tocsapiresponsestring"></a>

```json
{
  "code": "string",
  "data": "string",
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|string|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseTokenAuthResponse">ApiResponseTokenAuthResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsetokenauthresponse"></a>
<a id="schema_ApiResponseTokenAuthResponse"></a>
<a id="tocSapiresponsetokenauthresponse"></a>
<a id="tocsapiresponsetokenauthresponse"></a>

```json
{
  "code": "string",
  "data": {
    "accessToken": "string",
    "grantType": "string",
    "refreshToken": "string"
  },
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|[TokenAuthResponse](#schematokenauthresponse)|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ApiResponseVoid">ApiResponseVoid</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsevoid"></a>
<a id="schema_ApiResponseVoid"></a>
<a id="tocSapiresponsevoid"></a>
<a id="tocsapiresponsevoid"></a>

```json
{
  "code": "string",
  "data": null,
  "error": "string",
  "success": true,
  "timestamp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|string|false|none|none|
|data|any|false|none|none|
|error|string|false|none|none|
|success|boolean|false|none|none|
|timestamp|string|false|none|none|

<h2 id="tocS_ChatMessageResponse">ChatMessageResponse</h2>
<!-- backwards compatibility -->
<a id="schemachatmessageresponse"></a>
<a id="schema_ChatMessageResponse"></a>
<a id="tocSchatmessageresponse"></a>
<a id="tocschatmessageresponse"></a>

```json
{
  "content": "string",
  "createdAt": "2019-08-24T14:15:22Z",
  "messageId": 0,
  "senderId": 0,
  "senderNickname": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|content|string|false|none|none|
|createdAt|string(date-time)|false|none|none|
|messageId|integer(int64)|false|none|none|
|senderId|integer(int64)|false|none|none|
|senderNickname|string|false|none|none|

<h2 id="tocS_ChatRoomResponse">ChatRoomResponse</h2>
<!-- backwards compatibility -->
<a id="schemachatroomresponse"></a>
<a id="schema_ChatRoomResponse"></a>
<a id="tocSchatroomresponse"></a>
<a id="tocschatroomresponse"></a>

```json
{
  "createdAt": "2019-08-24T14:15:22Z",
  "displayName": "string",
  "id": 0,
  "listingId": 0,
  "listingName": "string",
  "myRole": "string",
  "name": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|createdAt|string(date-time)|false|none|none|
|displayName|string|false|none|none|
|id|integer(int64)|false|none|none|
|listingId|integer(int64)|false|none|none|
|listingName|string|false|none|none|
|myRole|string|false|none|none|
|name|string|false|none|none|

<h2 id="tocS_CreateCouponPolicyRequest">CreateCouponPolicyRequest</h2>
<!-- backwards compatibility -->
<a id="schemacreatecouponpolicyrequest"></a>
<a id="schema_CreateCouponPolicyRequest"></a>
<a id="tocScreatecouponpolicyrequest"></a>
<a id="tocscreatecouponpolicyrequest"></a>

```json
{
  "couponDuration": {
    "days": 0,
    "hours": 0,
    "minutes": 0,
    "seconds": 0
  },
  "issueType": "AUTO_SIGNUP",
  "moneyAmount": 0,
  "name": "string",
  "policyDuration": {
    "days": 0,
    "hours": 0,
    "minutes": 0,
    "seconds": 0
  },
  "totalQuantity": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|couponDuration|[DurationRequest](#schemadurationrequest)|false|none|none|
|issueType|string|true|none|none|
|moneyAmount|number|true|none|none|
|name|string|true|none|none|
|policyDuration|[DurationRequest](#schemadurationrequest)|false|none|none|
|totalQuantity|integer(int64)|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|issueType|AUTO_SIGNUP|
|issueType|FIRST_COME|

<h2 id="tocS_CreateCouponPolicyResponse">CreateCouponPolicyResponse</h2>
<!-- backwards compatibility -->
<a id="schemacreatecouponpolicyresponse"></a>
<a id="schema_CreateCouponPolicyResponse"></a>
<a id="tocScreatecouponpolicyresponse"></a>
<a id="tocscreatecouponpolicyresponse"></a>

```json
{
  "couponDurationSeconds": 0,
  "createdAt": "2019-08-24T14:15:22Z",
  "expendQuantity": 0,
  "id": 0,
  "issueType": "AUTO_SIGNUP",
  "modifiedAt": "2019-08-24T14:15:22Z",
  "moneyAmount": 0,
  "name": "string",
  "policyDurationSeconds": 0,
  "policyExpiredAt": "2019-08-24T14:15:22Z",
  "policyStartedAt": "2019-08-24T14:15:22Z",
  "totalQuantity": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|couponDurationSeconds|integer(int64)|false|none|none|
|createdAt|string(date-time)|false|none|none|
|expendQuantity|integer(int64)|false|none|none|
|id|integer(int64)|false|none|none|
|issueType|string|false|none|none|
|modifiedAt|string(date-time)|false|none|none|
|moneyAmount|number|false|none|none|
|name|string|false|none|none|
|policyDurationSeconds|integer(int64)|false|none|none|
|policyExpiredAt|string(date-time)|false|none|none|
|policyStartedAt|string(date-time)|false|none|none|
|totalQuantity|integer(int64)|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|issueType|AUTO_SIGNUP|
|issueType|FIRST_COME|

<h2 id="tocS_CreateMarketListingRequest">CreateMarketListingRequest</h2>
<!-- backwards compatibility -->
<a id="schemacreatemarketlistingrequest"></a>
<a id="schema_CreateMarketListingRequest"></a>
<a id="tocScreatemarketlistingrequest"></a>
<a id="tocscreatemarketlistingrequest"></a>

```json
{
  "memberItemId": 0,
  "quantity": 0,
  "salesDuration": "HOURS_12",
  "totalPrice": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|memberItemId|integer(int64)|false|none|none|
|quantity|integer(int64)|false|none|none|
|salesDuration|string|false|none|none|
|totalPrice|number|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|salesDuration|HOURS_12|
|salesDuration|HOURS_24|
|salesDuration|HOURS_48|

<h2 id="tocS_CreateOrderResponse">CreateOrderResponse</h2>
<!-- backwards compatibility -->
<a id="schemacreateorderresponse"></a>
<a id="schema_CreateOrderResponse"></a>
<a id="tocScreateorderresponse"></a>
<a id="tocscreateorderresponse"></a>

```json
{
  "itemName": "string",
  "orderId": 0,
  "transactionMoney": 0,
  "transactionStock": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|itemName|string|false|none|none|
|orderId|integer(int64)|false|none|none|
|transactionMoney|number|false|none|none|
|transactionStock|integer(int64)|false|none|none|

<h2 id="tocS_CreateRoomRequest">CreateRoomRequest</h2>
<!-- backwards compatibility -->
<a id="schemacreateroomrequest"></a>
<a id="schema_CreateRoomRequest"></a>
<a id="tocScreateroomrequest"></a>
<a id="tocscreateroomrequest"></a>

```json
{
  "listingId": 0,
  "sellerEmail": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|listingId|integer(int64)|false|none|none|
|sellerEmail|string|false|none|none|

<h2 id="tocS_DurationRequest">DurationRequest</h2>
<!-- backwards compatibility -->
<a id="schemadurationrequest"></a>
<a id="schema_DurationRequest"></a>
<a id="tocSdurationrequest"></a>
<a id="tocsdurationrequest"></a>

```json
{
  "days": 0,
  "hours": 0,
  "minutes": 0,
  "seconds": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|days|integer(int64)|false|none|none|
|hours|integer(int64)|false|none|none|
|minutes|integer(int64)|false|none|none|
|seconds|integer(int64)|false|none|none|

<h2 id="tocS_DurationResponse">DurationResponse</h2>
<!-- backwards compatibility -->
<a id="schemadurationresponse"></a>
<a id="schema_DurationResponse"></a>
<a id="tocSdurationresponse"></a>
<a id="tocsdurationresponse"></a>

```json
{
  "days": 0,
  "hours": 0,
  "minutes": 0,
  "seconds": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|days|integer(int64)|false|none|none|
|hours|integer(int64)|false|none|none|
|minutes|integer(int64)|false|none|none|
|seconds|integer(int64)|false|none|none|

<h2 id="tocS_GetAllMemberItemResponse">GetAllMemberItemResponse</h2>
<!-- backwards compatibility -->
<a id="schemagetallmemberitemresponse"></a>
<a id="schema_GetAllMemberItemResponse"></a>
<a id="tocSgetallmemberitemresponse"></a>
<a id="tocsgetallmemberitemresponse"></a>

```json
{
  "acquiredAt": "2019-08-24T14:15:22Z",
  "createdAt": "2019-08-24T14:15:22Z",
  "itemName": "string",
  "memberItemId": 0,
  "modifiedAt": "2019-08-24T14:15:22Z",
  "quantity": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|acquiredAt|string(date-time)|false|none|none|
|createdAt|string(date-time)|false|none|none|
|itemName|string|false|none|none|
|memberItemId|integer(int64)|false|none|none|
|modifiedAt|string(date-time)|false|none|none|
|quantity|integer(int64)|false|none|none|

<h2 id="tocS_GetItemResponse">GetItemResponse</h2>
<!-- backwards compatibility -->
<a id="schemagetitemresponse"></a>
<a id="schema_GetItemResponse"></a>
<a id="tocSgetitemresponse"></a>
<a id="tocsgetitemresponse"></a>

```json
{
  "itemId": 0,
  "itemName": "string",
  "itemType": "EQUIPMENT"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|itemId|integer(int64)|false|none|none|
|itemName|string|false|none|none|
|itemType|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|itemType|EQUIPMENT|
|itemType|CONSUMABLE|

<h2 id="tocS_GetMarketListingResponse">GetMarketListingResponse</h2>
<!-- backwards compatibility -->
<a id="schemagetmarketlistingresponse"></a>
<a id="schema_GetMarketListingResponse"></a>
<a id="tocSgetmarketlistingresponse"></a>
<a id="tocsgetmarketlistingresponse"></a>

```json
{
  "createdAt": "2019-08-24T14:15:22Z",
  "item": {
    "itemId": 0,
    "itemName": "string",
    "itemType": "EQUIPMENT"
  },
  "marketListingId": 0,
  "marketListingStatus": "SELLING",
  "modifiedAt": "2019-08-24T14:15:22Z",
  "quantity": 0,
  "saleEndAt": "2019-08-24T14:15:22Z",
  "totalPrice": 0,
  "unitPrice": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|createdAt|string(date-time)|false|none|none|
|item|[GetItemResponse](#schemagetitemresponse)|false|none|none|
|marketListingId|integer(int64)|false|none|none|
|marketListingStatus|string|false|none|none|
|modifiedAt|string(date-time)|false|none|none|
|quantity|integer(int64)|false|none|none|
|saleEndAt|string(date-time)|false|none|none|
|totalPrice|number|false|none|none|
|unitPrice|number|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|marketListingStatus|SELLING|
|marketListingStatus|SOLD|
|marketListingStatus|CLAIMED|
|marketListingStatus|CANCELLED|
|marketListingStatus|EXPIRED|

<h2 id="tocS_GetMemberItemResponse">GetMemberItemResponse</h2>
<!-- backwards compatibility -->
<a id="schemagetmemberitemresponse"></a>
<a id="schema_GetMemberItemResponse"></a>
<a id="tocSgetmemberitemresponse"></a>
<a id="tocsgetmemberitemresponse"></a>

```json
{
  "acquiredAt": "2019-08-24T14:15:22Z",
  "createdAt": "2019-08-24T14:15:22Z",
  "itemName": "string",
  "memberItemId": 0,
  "modifiedAt": "2019-08-24T14:15:22Z",
  "quantity": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|acquiredAt|string(date-time)|false|none|none|
|createdAt|string(date-time)|false|none|none|
|itemName|string|false|none|none|
|memberItemId|integer(int64)|false|none|none|
|modifiedAt|string(date-time)|false|none|none|
|quantity|integer(int64)|false|none|none|

<h2 id="tocS_GetMyInfoResponse">GetMyInfoResponse</h2>
<!-- backwards compatibility -->
<a id="schemagetmyinforesponse"></a>
<a id="schema_GetMyInfoResponse"></a>
<a id="tocSgetmyinforesponse"></a>
<a id="tocsgetmyinforesponse"></a>

```json
{
  "email": "string",
  "nickname": "string",
  "role": "USER"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|email|string|false|none|none|
|nickname|string|false|none|none|
|role|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|role|USER|
|role|ADMIN|

<h2 id="tocS_GetTransactionResponse">GetTransactionResponse</h2>
<!-- backwards compatibility -->
<a id="schemagettransactionresponse"></a>
<a id="schema_GetTransactionResponse"></a>
<a id="tocSgettransactionresponse"></a>
<a id="tocsgettransactionresponse"></a>

```json
{
  "createdAt": "2019-08-24T14:15:22Z",
  "marketListingId": 0,
  "orderId": 0,
  "transactionMoney": 0,
  "transactionStock": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|createdAt|string(date-time)|false|none|none|
|marketListingId|integer(int64)|false|none|none|
|orderId|integer(int64)|false|none|none|
|transactionMoney|number|false|none|none|
|transactionStock|integer(int64)|false|none|none|

<h2 id="tocS_GiveMemberItemRequest">GiveMemberItemRequest</h2>
<!-- backwards compatibility -->
<a id="schemagivememberitemrequest"></a>
<a id="schema_GiveMemberItemRequest"></a>
<a id="tocSgivememberitemrequest"></a>
<a id="tocsgivememberitemrequest"></a>

```json
{
  "acquiredAt": "2019-08-24T14:15:22Z",
  "itemId": 0,
  "memberEmail": "user@example.com",
  "quantity": 1
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|acquiredAt|string(date-time)|false|none|none|
|itemId|integer(int64)|true|none|none|
|memberEmail|string(email)|true|none|none|
|quantity|integer(int64)|true|none|none|

<h2 id="tocS_LoginAuthRequest">LoginAuthRequest</h2>
<!-- backwards compatibility -->
<a id="schemaloginauthrequest"></a>
<a id="schema_LoginAuthRequest"></a>
<a id="tocSloginauthrequest"></a>
<a id="tocsloginauthrequest"></a>

```json
{
  "email": "string",
  "password": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|email|string|true|none|none|
|password|string|true|none|none|

<h2 id="tocS_PageResponseGetAllMemberItemResponse">PageResponseGetAllMemberItemResponse</h2>
<!-- backwards compatibility -->
<a id="schemapageresponsegetallmemberitemresponse"></a>
<a id="schema_PageResponseGetAllMemberItemResponse"></a>
<a id="tocSpageresponsegetallmemberitemresponse"></a>
<a id="tocspageresponsegetallmemberitemresponse"></a>

```json
{
  "content": [
    {
      "acquiredAt": "2019-08-24T14:15:22Z",
      "createdAt": "2019-08-24T14:15:22Z",
      "itemName": "string",
      "memberItemId": 0,
      "modifiedAt": "2019-08-24T14:15:22Z",
      "quantity": 0
    }
  ],
  "pageNumber": 0,
  "pageSize": 0,
  "totalElements": 0,
  "totalPages": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|content|[[GetAllMemberItemResponse](#schemagetallmemberitemresponse)]|false|none|none|
|pageNumber|integer(int32)|false|none|none|
|pageSize|integer(int32)|false|none|none|
|totalElements|integer(int64)|false|none|none|
|totalPages|integer(int32)|false|none|none|

<h2 id="tocS_PageResponseGetItemResponse">PageResponseGetItemResponse</h2>
<!-- backwards compatibility -->
<a id="schemapageresponsegetitemresponse"></a>
<a id="schema_PageResponseGetItemResponse"></a>
<a id="tocSpageresponsegetitemresponse"></a>
<a id="tocspageresponsegetitemresponse"></a>

```json
{
  "content": [
    {
      "itemId": 0,
      "itemName": "string",
      "itemType": "EQUIPMENT"
    }
  ],
  "pageNumber": 0,
  "pageSize": 0,
  "totalElements": 0,
  "totalPages": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|content|[[GetItemResponse](#schemagetitemresponse)]|false|none|none|
|pageNumber|integer(int32)|false|none|none|
|pageSize|integer(int32)|false|none|none|
|totalElements|integer(int64)|false|none|none|
|totalPages|integer(int32)|false|none|none|

<h2 id="tocS_PageResponseSearchAllCouponHistoryResponse">PageResponseSearchAllCouponHistoryResponse</h2>
<!-- backwards compatibility -->
<a id="schemapageresponsesearchallcouponhistoryresponse"></a>
<a id="schema_PageResponseSearchAllCouponHistoryResponse"></a>
<a id="tocSpageresponsesearchallcouponhistoryresponse"></a>
<a id="tocspageresponsesearchallcouponhistoryresponse"></a>

```json
{
  "content": [
    {
      "couponHistoryId": 0,
      "couponName": "string",
      "createdAt": "2019-08-24T14:15:22Z",
      "modifiedAt": "2019-08-24T14:15:22Z",
      "moneyAmount": 0,
      "status": "USED",
      "usedAt": "2019-08-24T14:15:22Z"
    }
  ],
  "pageNumber": 0,
  "pageSize": 0,
  "totalElements": 0,
  "totalPages": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|content|[[SearchAllCouponHistoryResponse](#schemasearchallcouponhistoryresponse)]|false|none|none|
|pageNumber|integer(int32)|false|none|none|
|pageSize|integer(int32)|false|none|none|
|totalElements|integer(int64)|false|none|none|
|totalPages|integer(int32)|false|none|none|

<h2 id="tocS_PageResponseSearchAllCouponPolicyResponse">PageResponseSearchAllCouponPolicyResponse</h2>
<!-- backwards compatibility -->
<a id="schemapageresponsesearchallcouponpolicyresponse"></a>
<a id="schema_PageResponseSearchAllCouponPolicyResponse"></a>
<a id="tocSpageresponsesearchallcouponpolicyresponse"></a>
<a id="tocspageresponsesearchallcouponpolicyresponse"></a>

```json
{
  "content": [
    {
      "couponDuration": {
        "days": 0,
        "hours": 0,
        "minutes": 0,
        "seconds": 0
      },
      "createdAt": "2019-08-24T14:15:22Z",
      "expendQuantity": 0,
      "id": 0,
      "issueType": "AUTO_SIGNUP",
      "modifiedAt": "2019-08-24T14:15:22Z",
      "moneyAmount": 0,
      "name": "string",
      "policyDuration": {
        "days": 0,
        "hours": 0,
        "minutes": 0,
        "seconds": 0
      },
      "policyExpiredAt": "2019-08-24T14:15:22Z",
      "policyStartedAt": "2019-08-24T14:15:22Z",
      "totalQuantity": 0
    }
  ],
  "pageNumber": 0,
  "pageSize": 0,
  "totalElements": 0,
  "totalPages": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|content|[[SearchAllCouponPolicyResponse](#schemasearchallcouponpolicyresponse)]|false|none|none|
|pageNumber|integer(int32)|false|none|none|
|pageSize|integer(int32)|false|none|none|
|totalElements|integer(int64)|false|none|none|
|totalPages|integer(int32)|false|none|none|

<h2 id="tocS_PageResponseSearchAllMarketListingResponse">PageResponseSearchAllMarketListingResponse</h2>
<!-- backwards compatibility -->
<a id="schemapageresponsesearchallmarketlistingresponse"></a>
<a id="schema_PageResponseSearchAllMarketListingResponse"></a>
<a id="tocSpageresponsesearchallmarketlistingresponse"></a>
<a id="tocspageresponsesearchallmarketlistingresponse"></a>

```json
{
  "content": [
    {
      "createdAt": "2019-08-24T14:15:22Z",
      "itemName": "string",
      "marketListingId": 0,
      "modifiedAt": "2019-08-24T14:15:22Z",
      "quantity": 0,
      "saleEndAt": "2019-08-24T14:15:22Z",
      "status": "SELLING",
      "totalPrice": 0
    }
  ],
  "pageNumber": 0,
  "pageSize": 0,
  "totalElements": 0,
  "totalPages": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|content|[[SearchAllMarketListingResponse](#schemasearchallmarketlistingresponse)]|false|none|none|
|pageNumber|integer(int32)|false|none|none|
|pageSize|integer(int32)|false|none|none|
|totalElements|integer(int64)|false|none|none|
|totalPages|integer(int32)|false|none|none|

<h2 id="tocS_PageResponseSearchAllMemberCouponResponse">PageResponseSearchAllMemberCouponResponse</h2>
<!-- backwards compatibility -->
<a id="schemapageresponsesearchallmembercouponresponse"></a>
<a id="schema_PageResponseSearchAllMemberCouponResponse"></a>
<a id="tocSpageresponsesearchallmembercouponresponse"></a>
<a id="tocspageresponsesearchallmembercouponresponse"></a>

```json
{
  "content": [
    {
      "couponDuration": {
        "days": 0,
        "hours": 0,
        "minutes": 0,
        "seconds": 0
      },
      "couponName": "string",
      "createdAt": "2019-08-24T14:15:22Z",
      "expiredAt": "2019-08-24T14:15:22Z",
      "issuedAt": "2019-08-24T14:15:22Z",
      "memberCouponId": 0,
      "modifiedAt": "2019-08-24T14:15:22Z",
      "moneyAmount": 0,
      "status": "UNUSED"
    }
  ],
  "pageNumber": 0,
  "pageSize": 0,
  "totalElements": 0,
  "totalPages": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|content|[[SearchAllMemberCouponResponse](#schemasearchallmembercouponresponse)]|false|none|none|
|pageNumber|integer(int32)|false|none|none|
|pageSize|integer(int32)|false|none|none|
|totalElements|integer(int64)|false|none|none|
|totalPages|integer(int32)|false|none|none|

<h2 id="tocS_PendingAssetResponse">PendingAssetResponse</h2>
<!-- backwards compatibility -->
<a id="schemapendingassetresponse"></a>
<a id="schema_PendingAssetResponse"></a>
<a id="tocSpendingassetresponse"></a>
<a id="tocspendingassetresponse"></a>

```json
{
  "itemQuantity": 0,
  "moneyAmount": 0,
  "pendingAssetId": 0,
  "type": "MONEY"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|itemQuantity|integer(int64)|false|none|none|
|moneyAmount|number|false|none|none|
|pendingAssetId|integer(int64)|false|none|none|
|type|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|type|MONEY|
|type|ITEM|

<h2 id="tocS_SearchAllCouponHistoryResponse">SearchAllCouponHistoryResponse</h2>
<!-- backwards compatibility -->
<a id="schemasearchallcouponhistoryresponse"></a>
<a id="schema_SearchAllCouponHistoryResponse"></a>
<a id="tocSsearchallcouponhistoryresponse"></a>
<a id="tocssearchallcouponhistoryresponse"></a>

```json
{
  "couponHistoryId": 0,
  "couponName": "string",
  "createdAt": "2019-08-24T14:15:22Z",
  "modifiedAt": "2019-08-24T14:15:22Z",
  "moneyAmount": 0,
  "status": "USED",
  "usedAt": "2019-08-24T14:15:22Z"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|couponHistoryId|integer(int64)|false|none|none|
|couponName|string|false|none|none|
|createdAt|string(date-time)|false|none|none|
|modifiedAt|string(date-time)|false|none|none|
|moneyAmount|number|false|none|none|
|status|string|false|none|none|
|usedAt|string(date-time)|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|status|USED|
|status|EXPIRED|

<h2 id="tocS_SearchAllCouponPolicyResponse">SearchAllCouponPolicyResponse</h2>
<!-- backwards compatibility -->
<a id="schemasearchallcouponpolicyresponse"></a>
<a id="schema_SearchAllCouponPolicyResponse"></a>
<a id="tocSsearchallcouponpolicyresponse"></a>
<a id="tocssearchallcouponpolicyresponse"></a>

```json
{
  "couponDuration": {
    "days": 0,
    "hours": 0,
    "minutes": 0,
    "seconds": 0
  },
  "createdAt": "2019-08-24T14:15:22Z",
  "expendQuantity": 0,
  "id": 0,
  "issueType": "AUTO_SIGNUP",
  "modifiedAt": "2019-08-24T14:15:22Z",
  "moneyAmount": 0,
  "name": "string",
  "policyDuration": {
    "days": 0,
    "hours": 0,
    "minutes": 0,
    "seconds": 0
  },
  "policyExpiredAt": "2019-08-24T14:15:22Z",
  "policyStartedAt": "2019-08-24T14:15:22Z",
  "totalQuantity": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|couponDuration|[DurationResponse](#schemadurationresponse)|false|none|none|
|createdAt|string(date-time)|false|none|none|
|expendQuantity|integer(int64)|false|none|none|
|id|integer(int64)|false|none|none|
|issueType|string|false|none|none|
|modifiedAt|string(date-time)|false|none|none|
|moneyAmount|number|false|none|none|
|name|string|false|none|none|
|policyDuration|[DurationResponse](#schemadurationresponse)|false|none|none|
|policyExpiredAt|string(date-time)|false|none|none|
|policyStartedAt|string(date-time)|false|none|none|
|totalQuantity|integer(int64)|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|issueType|AUTO_SIGNUP|
|issueType|FIRST_COME|

<h2 id="tocS_SearchAllMarketListingResponse">SearchAllMarketListingResponse</h2>
<!-- backwards compatibility -->
<a id="schemasearchallmarketlistingresponse"></a>
<a id="schema_SearchAllMarketListingResponse"></a>
<a id="tocSsearchallmarketlistingresponse"></a>
<a id="tocssearchallmarketlistingresponse"></a>

```json
{
  "createdAt": "2019-08-24T14:15:22Z",
  "itemName": "string",
  "marketListingId": 0,
  "modifiedAt": "2019-08-24T14:15:22Z",
  "quantity": 0,
  "saleEndAt": "2019-08-24T14:15:22Z",
  "status": "SELLING",
  "totalPrice": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|createdAt|string(date-time)|false|none|none|
|itemName|string|false|none|none|
|marketListingId|integer(int64)|false|none|none|
|modifiedAt|string(date-time)|false|none|none|
|quantity|integer(int64)|false|none|none|
|saleEndAt|string(date-time)|false|none|none|
|status|string|false|none|none|
|totalPrice|number|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|status|SELLING|
|status|SOLD|
|status|CLAIMED|
|status|CANCELLED|
|status|EXPIRED|

<h2 id="tocS_SearchAllMemberCouponResponse">SearchAllMemberCouponResponse</h2>
<!-- backwards compatibility -->
<a id="schemasearchallmembercouponresponse"></a>
<a id="schema_SearchAllMemberCouponResponse"></a>
<a id="tocSsearchallmembercouponresponse"></a>
<a id="tocssearchallmembercouponresponse"></a>

```json
{
  "couponDuration": {
    "days": 0,
    "hours": 0,
    "minutes": 0,
    "seconds": 0
  },
  "couponName": "string",
  "createdAt": "2019-08-24T14:15:22Z",
  "expiredAt": "2019-08-24T14:15:22Z",
  "issuedAt": "2019-08-24T14:15:22Z",
  "memberCouponId": 0,
  "modifiedAt": "2019-08-24T14:15:22Z",
  "moneyAmount": 0,
  "status": "UNUSED"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|couponDuration|[DurationResponse](#schemadurationresponse)|false|none|none|
|couponName|string|false|none|none|
|createdAt|string(date-time)|false|none|none|
|expiredAt|string(date-time)|false|none|none|
|issuedAt|string(date-time)|false|none|none|
|memberCouponId|integer(int64)|false|none|none|
|modifiedAt|string(date-time)|false|none|none|
|moneyAmount|number|false|none|none|
|status|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|status|UNUSED|
|status|USED|
|status|EXPIRED|

<h2 id="tocS_SearchItemRequest">SearchItemRequest</h2>
<!-- backwards compatibility -->
<a id="schemasearchitemrequest"></a>
<a id="schema_SearchItemRequest"></a>
<a id="tocSsearchitemrequest"></a>
<a id="tocssearchitemrequest"></a>

```json
{
  "itemType": "EQUIPMENT",
  "keyword": "string",
  "normalizedKeyword": "string",
  "page": 0,
  "sortCreatedAt": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|itemType|string|false|none|none|
|keyword|string|false|none|none|
|normalizedKeyword|string|false|none|none|
|page|integer(int32)|true|none|none|
|sortCreatedAt|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|itemType|EQUIPMENT|
|itemType|CONSUMABLE|

<h2 id="tocS_SearchMarketListingResponse">SearchMarketListingResponse</h2>
<!-- backwards compatibility -->
<a id="schemasearchmarketlistingresponse"></a>
<a id="schema_SearchMarketListingResponse"></a>
<a id="tocSsearchmarketlistingresponse"></a>
<a id="tocssearchmarketlistingresponse"></a>

```json
{
  "createdAt": "2019-08-24T14:15:22Z",
  "itemName": "string",
  "marketListingId": 0,
  "modifiedAt": "2019-08-24T14:15:22Z",
  "quantity": 0,
  "saleEndAt": "2019-08-24T14:15:22Z",
  "sellerEmail": "string",
  "sellerNickname": "string",
  "status": "SELLING",
  "totalPrice": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|createdAt|string(date-time)|false|none|none|
|itemName|string|false|none|none|
|marketListingId|integer(int64)|false|none|none|
|modifiedAt|string(date-time)|false|none|none|
|quantity|integer(int64)|false|none|none|
|saleEndAt|string(date-time)|false|none|none|
|sellerEmail|string|false|none|none|
|sellerNickname|string|false|none|none|
|status|string|false|none|none|
|totalPrice|number|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|status|SELLING|
|status|SOLD|
|status|CLAIMED|
|status|CANCELLED|
|status|EXPIRED|

<h2 id="tocS_SearchTrendingKeywordResponse">SearchTrendingKeywordResponse</h2>
<!-- backwards compatibility -->
<a id="schemasearchtrendingkeywordresponse"></a>
<a id="schema_SearchTrendingKeywordResponse"></a>
<a id="tocSsearchtrendingkeywordresponse"></a>
<a id="tocssearchtrendingkeywordresponse"></a>

```json
{
  "keyword": "string",
  "searchCount": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|keyword|string|false|none|none|
|searchCount|integer(int64)|false|none|none|

<h2 id="tocS_SetPasswordRequest">SetPasswordRequest</h2>
<!-- backwards compatibility -->
<a id="schemasetpasswordrequest"></a>
<a id="schema_SetPasswordRequest"></a>
<a id="tocSsetpasswordrequest"></a>
<a id="tocssetpasswordrequest"></a>

```json
{
  "newPassword": "stringst"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|newPassword|string|true|none|none|

<h2 id="tocS_SignupAuthRequest">SignupAuthRequest</h2>
<!-- backwards compatibility -->
<a id="schemasignupauthrequest"></a>
<a id="schema_SignupAuthRequest"></a>
<a id="tocSsignupauthrequest"></a>
<a id="tocssignupauthrequest"></a>

```json
{
  "email": "user@example.com",
  "nickname": "string",
  "password": "string",
  "role": "USER"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|email|string(email)|true|none|none|
|nickname|string|true|none|none|
|password|string|true|none|none|
|role|string|true|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|role|USER|
|role|ADMIN|

<h2 id="tocS_SuspendMemberRequest">SuspendMemberRequest</h2>
<!-- backwards compatibility -->
<a id="schemasuspendmemberrequest"></a>
<a id="schema_SuspendMemberRequest"></a>
<a id="tocSsuspendmemberrequest"></a>
<a id="tocssuspendmemberrequest"></a>

```json
{
  "email": "string",
  "reason": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|email|string|true|none|none|
|reason|string|true|none|none|

<h2 id="tocS_TokenAuthResponse">TokenAuthResponse</h2>
<!-- backwards compatibility -->
<a id="schematokenauthresponse"></a>
<a id="schema_TokenAuthResponse"></a>
<a id="tocStokenauthresponse"></a>
<a id="tocstokenauthresponse"></a>

```json
{
  "accessToken": "string",
  "grantType": "string",
  "refreshToken": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|accessToken|string|false|none|none|
|grantType|string|false|none|none|
|refreshToken|string|false|none|none|

<h2 id="tocS_TokenReissueRequest">TokenReissueRequest</h2>
<!-- backwards compatibility -->
<a id="schematokenreissuerequest"></a>
<a id="schema_TokenReissueRequest"></a>
<a id="tocStokenreissuerequest"></a>
<a id="tocstokenreissuerequest"></a>

```json
{
  "refreshToken": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|refreshToken|string|true|none|none|

<h2 id="tocS_UnlinkSocialRequest">UnlinkSocialRequest</h2>
<!-- backwards compatibility -->
<a id="schemaunlinksocialrequest"></a>
<a id="schema_UnlinkSocialRequest"></a>
<a id="tocSunlinksocialrequest"></a>
<a id="tocsunlinksocialrequest"></a>

```json
{
  "provider": "GOOGLE"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|provider|string|true|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|provider|GOOGLE|
|provider|KAKAO|
|provider|GITHUB|

<h2 id="tocS_UpdateNicknameRequest">UpdateNicknameRequest</h2>
<!-- backwards compatibility -->
<a id="schemaupdatenicknamerequest"></a>
<a id="schema_UpdateNicknameRequest"></a>
<a id="tocSupdatenicknamerequest"></a>
<a id="tocsupdatenicknamerequest"></a>

```json
{
  "nickname": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|nickname|string|true|none|none|

<h2 id="tocS_UpdatePasswordRequest">UpdatePasswordRequest</h2>
<!-- backwards compatibility -->
<a id="schemaupdatepasswordrequest"></a>
<a id="schema_UpdatePasswordRequest"></a>
<a id="tocSupdatepasswordrequest"></a>
<a id="tocsupdatepasswordrequest"></a>

```json
{
  "currentPassword": "string",
  "newPassword": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|currentPassword|string|true|none|none|
|newPassword|string|true|none|none|

<h2 id="tocS_WalletResponse">WalletResponse</h2>
<!-- backwards compatibility -->
<a id="schemawalletresponse"></a>
<a id="schema_WalletResponse"></a>
<a id="tocSwalletresponse"></a>
<a id="tocswalletresponse"></a>

```json
{
  "balance": 0,
  "memberId": 0,
  "walletId": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|balance|number|false|none|none|
|memberId|integer(int64)|false|none|none|
|walletId|integer(int64)|false|none|none|


import http from 'k6/http';
import { check, fail } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { URL } from 'https://jslib.k6.io/url/1.0.0/index.js';

/**
* 입력:
*     req:
*     null 혹은 object
*     {
*         email: null일 경우 random
*         password: null일 경우 random
*         nickname: null일 경우 random
*         role: null일 경웅 USER
*     }
* 
*     version:
*     api version 정보
* 
* 반환:
* 
*     성공시 reqest를 돌려줍니다, 실패시 null
*
*/
export function signup (req, version) {
    if (req === null || req == undefined) {
        req = {}
    }

    if (req.email === null || req.email === undefined) {
        req.email = `${randomString(8)}@gmail.com`;
    }
    if (req.password === null || req.password === undefined) {
        req.password = randomString(20);
    }
    if (req.nickname === null || req.nickname === undefined) {
        req.nickname = randomString(20);
    }
    if (req.role === null || req.role === undefined ) {
        req.role = 'USER'
    }

    // define URL and request body
    const url = `http://localhost:8080/api/${version}/auth/signup`;

    const payload = JSON.stringify(req);

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // send a post request and save response as a variable
    const res = http.post(url, payload, params);

    // check that response is 200
    const success = check(res, {
        'response code was 200': (res) => res.status == 200,
    });

    if (!success) {
        return null;
    }

    return req;
}

/**
 * 로그인을 합니다
 *
 * 입력:
 *     {
 *         email
 *         password
 *     }
 *
 *     version:
 *     api version 정보
 *
 * 반환:
 *     실패시 null, 성공시 jwt token
 */
export function login(req, version) {
    // define URL and request body
    const url = `http://localhost:8080/api/${version}/auth/login`;

    const payload = JSON.stringify({
        email: req.email,
        password: req.password,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    // check that response is 200
    const success = check(res, {
        'response code was 200': (res) => res.status == 200,
    });

    if (!success) {
        return null;
    }

    return JSON.parse(res.body).data.accessToken
}

/**
 *
 * 입력:
 *     req:
 *     {
 *         page : 숫자, null일 수 있음
 *         keyword : 검색어, null일 수 있음,
 *         itemType : EQUIPMENT, CONSUMABLE 둘중 하나, null일 수 있음,
 *         sortCreatedAt : ASC, DSC, null 일 수 있음
 *     }
 *
 *     version:
 *     api version 정보
 * 
 * 반환:
 *     body, 실패시 null
 *
 */
export function getManyItem(token, req, version) {
    token = toBearerToken(token)

    // define URL and request body
    const url = new URL(`http://localhost:8080/api/${version}/items`);

    if (req.page != null) {
        url.searchParams.append('page', req.page);
    }
    if (req.keyword != null) {
        url.searchParams.append('keyword', req.keyword);
    }
    if (req.itemType != null) {
        url.searchParams.append('itemType', req.itemType);
    }
    if (req.sortCreatedAt != null) {
        url.searchParams.append('sortCreatedAt', req.sortCreatedAt);
    }

    const params = {
        headers: {
            'Authorization': token,
        },
    };


    const res = http.get(url.toString(), params);

    // check that response is 200
    const success = check(res, {
        'response code was 200': (res) => res.status == 200,
    });

    if (!success) {
        return null;
    }

    return JSON.parse(res.body)
}

export function getAllMemberItem(token, page, version) {
    token = toBearerToken(token)

    const url = `http://localhost:8080/api/${version}/me/items?page=${page}`;

    const params = {
        headers: {
            'Authorization' : token
        },
    };

    const res = http.get(url, params);
    const success = check(res, {
        'successfully got member items': (res) => res.status == 200,
    });

    if (!success) {
        return null;
    }

    return JSON.parse(res.body)
}

function toBearerToken(token) {
    return "Bearer " + token;
}

/**
 * 입력:
 *     token : jwt token
 *     req:
 *     {
 *         name
 *         moneyAmount
 *         issueType
 *         totalQuantity
 * 
 *         policyDuration
 *         couponDuration;
 *     }
 *     version:
 *     api version 정보
 * 
 * 반환:
 *     body, 실패시 null
 *
 */

export function submitCoupon(token, req, version) {
    token = toBearerToken(token)

    const url = `http://localhost:8080/api/${version}/admin/coupon-policies`;

    const payload = JSON.stringify(req);

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization' : token
        },
    };

    const res = http.post(url, payload, params);
    const success = check(res, {
        'response code was 201': (res) => res.status == 201,
    });

    if (!success) {
        return null;
    }

    return JSON.parse(res.body)
}

/**
 * 입력:
 *     token: jwt token
 *     couponId: couponId
 *     version: api version 정보
 * 반환:
 *     true, 실패시 null
 *
 */

export function applyForCoupon(token, couponId, version) {
    token = toBearerToken(token)

    const url = `http://localhost:8080/api/${version}/coupon-policies/${couponId}/issue`;

    const params = {
        headers: {
            'Authorization' : token
        },
    };

    const res = http.post(url, null, params);

    if (res.status !== 200) {
        return null;
    }

    return true
}


/**
 * 입력:
 *     token: jwt token
 *     req : 
 *     {
 *         memberItemId,
 *         totalPrice,
 *         quantity,
 *         salesDuration
 *     }
 *     version: api version 정보
 * 반환:
 *     body, 실패시 null
 *
 */
export function createMarketListing(token, req, version) {
    token = toBearerToken(token)

    const url = `http://localhost:8080/api/${version}/market-listings`;

    const payload = JSON.stringify(req);

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization' : token
        },
    };

    const res = http.post(url, payload, params);
    const success = check(res, {
        'created market listings': (res) => res.status == 200,
    });

    if (!success) {
        return null;
    }

    return JSON.parse(res.body)
}

/**
 * 입력:
 *     token: jwt token
 *     marketListingId: 구매할 marketlisting id
 *     version: api version 정보
 * 반환:
 *     body, 실패시 null
 *
 */
export function purchase(token, marketListingId, version) {
    token = toBearerToken(token)

    const url = `http://localhost:8080/api/${version}/market-listings/${marketListingId}`;

    const params = {
        headers: {
            'Authorization' : token
        },
    };

    const res = http.post(url, null, params);
    const success = check(res, {
        'successfully purchased market listing': (res) => res.status == 200,
    });

    if (!success) {
        try {
            const body = JSON.parse(res.body)

            const code = body.code;
            const error = body.error;

            console.error(`FAIL: ${url} - ${code}:${error}`)
        }catch(err) {
        }
    }

    if (!success) {
        return null;
    }

    return JSON.parse(res.body)
}

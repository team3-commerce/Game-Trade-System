import http from 'k6/http';
import { check, fail } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { URL } from 'https://jslib.k6.io/url/1.0.0/index.js';

/**
 * 회원가입을 합니다.
 * 
 * 반환:
 *
 * 실패시 null
 *
 * 성공시
 *
 * {
 *     email
 *     password
 *     nickname
 *     role
 * }
 *
 * 을 반환합니다
 */
export function signup (role = 'USER') {
    const randomEmail = `${randomString(8)}@gmail.com`;
    const randomPassword = randomString(20);
    const randomNickname = randomString(20);

    // define URL and request body
    const url = 'http://localhost:8080/api/v1/auth/signup';

    const payload = JSON.stringify({
        email: randomEmail,
        password: randomPassword,
        nickname: randomNickname,
        role: role
    });

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

    return {
        email : randomEmail,
        password : randomPassword,
        nickname : randomNickname,
        role : role
    }
}

/**
 * 로그인을 합니다
 *
 * 입력:
 * 
 * {
 *     email
 *     password
 * }
 *
 * 반환:
 *
 * 실패시 null, 성공시 jwt token
 */
export function login(req) {
    // define URL and request body
    const url = 'http://localhost:8080/api/v2/auth/login';

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
 * 입력
 * req:
 * {
 *     page : 숫자, null일 수 있음
 *     keyword : 검색어, null일 수 있음,
 *     itemType : EQUIPMENT, CONSUMABLE 둘중 하나, null일 수 있음,
 *     sortCreatedAt : ASC, DSC, null 일 수 있음
 * }
 *
 * version:
 * api version 정보
 * 
 * 출력
 * body, 실패시 null
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

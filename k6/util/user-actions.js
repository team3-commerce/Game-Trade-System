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
 *     success
 *     email
 *     password
 *     nickname
 *     role
 * }
 *
 * 을 반환합니다
 */
export function signup () {
    const randomEmail = `${randomString(8)}@gmail.com`;
    const randomPassword = randomString(20);
    const randomNickname = randomString(20);

    // define URL and request body
    const url = 'http://localhost:8080/api/v1/auth/signup';

    const payload = JSON.stringify({
        email: randomEmail,
        password: randomPassword,
        nickname: randomNickname,
        role: 'USER'
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
        role : 'USER'
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

    if (req.page) {
        url.searchParams.append('page', req.page);
    }
    if (req.keyword) {
        url.searchParams.append('keyword', req.keyword);
    }
    if (req.itemType) {
        url.searchParams.append('itemType', req.itemType);
    }
    if (req.sortCreatedAt) {
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

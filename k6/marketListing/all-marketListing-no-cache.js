import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = 'http://host.docker.internal:8080';

const TARGET = __ENV.SCENARIO || 'all';

const scenarios = {};

if (TARGET === 'rep' || TARGET === 'all') {
    scenarios.representative_case = {
        executor: 'constant-arrival-rate',
        exec: 'representativeCase',
        rate: 70,
        timeUnit: '1s',
        duration: '1m',
        preAllocatedVUs: 100,
        maxVUs: 300,
    };
}

if (TARGET === 'keyword' || TARGET === 'all') {
    scenarios.keyword_search_case = {
        executor: 'constant-arrival-rate',
        exec: 'keywordSearchCase',
        rate: 30,
        timeUnit: '1s',
        duration: '1m',
        preAllocatedVUs: 50,
        maxVUs: 150,
    };
}

export const options = {
  scenarios,
};

export function setup() {
    const tokens = [];

    for (let i = 0; i < 200; i++) {
        const email = `user${i}@test.com`;
        const password = '1234567890';

        const res = http.post(
            'http://host.docker.internal:8080/api/v3/auth/login',
            JSON.stringify({email, password}),
            {
                headers: {'Content-Type': 'application/json'},
            }
        );

        check(res, {
            'login status 200': (r) => r.status === 200,
            'login token exists': (r) => !!r.json('data.accessToken'),
        });

        tokens.push(res.json('data.accessToken'))
    }
    return { tokens };
}

export function representativeCase(data) {

    const token = data.tokens[(__VU - 1) % data.tokens.length];

    const params = {
        headers: {
            'Authorization': `Bearer ${token}`
        },
    };

    const res = http.get(`${BASE_URL}/api/v1/market-listings`, params)

    check(res, {
        'market-listings status 200': (r) => r.status === 200,
    });
}

export function keywordSearchCase(data) {

    const token = data.tokens[(__VU - 1) % data.tokens.length];

    const keywords = ['S', 'A', 'B', 'C', 'D'];
    const keyword = keywords[__ITER % keywords.length];

    const params = {
        headers: {
            'Authorization': `Bearer ${token}`
        },
    };

    const res = http.get(`${BASE_URL}/api/v1/market-listings?keyword=${keyword}`, params)

    check(res, {
        'market-listings status 200': (r) => r.status === 200,
    });
}
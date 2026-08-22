import http from 'k6/http';

const BASE_URL = 'http://host.docker.internal:8080';

export function loginUsers(userCount) {
    const tokens = [];

    for (let i = 0; i < userCount; i++) {
        const email = `user${i}@test.com`;
        const password = '1234567890';

        const res = http.post(
            `${BASE_URL}/api/v3/auth/login`,
            JSON.stringify({email, password}),
            {headers: {'Content-Type': 'application/json'}}
        );

        tokens.push(res.json('data.accessToken'));
    }

    return { tokens };
}

export function authHeaders(token) {
    return {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    };
}

export function getMyMemberItems(token) {
    return http.get(`${BASE_URL}/api/v3/me/items`, authHeaders(token));
}

import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    duration: '30s',
    vus: 50
};

export function setup() {
    const users = [];
    const tokens = [];

    for (let i = 0; i < options.vus; i++) {
        const email = `user${i}@test.com`;
        const password = '1234567890';

        users.push({ email, password });

        const res = http.post(
            'http://localhost:8080/api/v1/auth/login',
            JSON.stringify({email, password}),
            {
                headers: {'Content-Type': 'application/json'},
            }
        );

        tokens.push(res.json('data.accessToken'))
    }
    return {users, tokens};
}


export default function (data) {
    const index = __VU - 1;

    const token = data.tokens[index];

    const params = {
        headers: {
            'Authorization': `Bearer ${token}`
        },
    };

    http.get('http://localhost:8080/api/v1/me/items', params)
}
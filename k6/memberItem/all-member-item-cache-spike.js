import http from 'k6/http';
import {getMyMemberItems, loginUsers} from './common.js';

export const options = {
    scenarios: {
        burst: {
            executor: 'ramping-arrival-rate',
            startRate: 0,
            timeUnit: '1s',
            preAllocatedVUs: 300,
            maxVUs: 500,
            stages: [
                { target: 800, duration: '5s' },
                { target: 800, duration: '20s' },
                { target: 0, duration: '5s' },
            ],
        },
    },
};

export function setup() {
    const { tokens } = loginUsers(500);

    for (const token of tokens) {
        getMyMemberItems(token);
    }

    return { tokens };
}

export default function (data) {
    const token = data.tokens[(__VU - 1) % data.tokens.length];
    getMyMemberItems(token);
}
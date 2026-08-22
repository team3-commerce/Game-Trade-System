import http from 'k6/http';
import { sleep } from 'k6';
import {getMyMemberItems, loginUsers} from './common.js';

export const options = {
    scenarios: {
        baseline: {
            executor: 'constant-arrival-rate',
            rate: 100,
            timeUnit: '1s',
            duration: '2m',
            preAllocatedVUs: 30,
            maxVUs: 100,
        },
    },
};

export function setup() {
    const { tokens } = loginUsers(100);

    for (const token of tokens) {
        getMyMemberItems(token);
    }

    return { tokens };
}

export default function (data) {
    const token = data.tokens[(__VU - 1) % data.tokens.length];
    getMyMemberItems(token);
}
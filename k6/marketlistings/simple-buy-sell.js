import exec from 'k6/execution';
import { login, getAllMemberItem, createMarketListing, purchase } from "../util/user-actions.js";

// 유저 1000명이 있습니다.
// 한명은 자신의 아이템을 등록하고
// 다른 한명은 그 아이템을 삽니다.
// 
// 이 거를 많~~~이 반복합니다.

export const options = {
    setupTimeout: '10m',

    scenarios: {
        buy_sell : {
            executor: 'ramping-arrival-rate',
            startRate: 50,
            timeUnit: '1s',
            preAllocatedVUs: 60,
            maxVUs: 60,
            stages: [
                { target: 50, duration: '30s' },
                { target: 60, duration: '30s' },
                { target: 60, duration: '30s' },
                { target: 50, duration: '30s' },
                { target: 0, duration: '30s' },
            ],
        },
    },
};

const UNIQUE_ACCOUNTS = 1000

export function setup() {
    let tokenAndMemberItems = []

    for (let i=0; i<UNIQUE_ACCOUNTS; i++) {
        const jwt = login({
            email : `user${i}@test.com`,
            password : '1234567890'
        }, 'v3')

        if (jwt == null) {
            fail(`사용자 ${i} 로그인 실패`);
        }

        // jwtTokens.push(jwt)

        if (i % 100 === 0) {
            console.log(`${i}/${UNIQUE_ACCOUNTS} logged in`)
        }

        const memberItemId = getAllMemberItem(jwt, 0, 'v2').data.content[0].memberItemId;

        // console.log(jwt, memberItemId)
        tokenAndMemberItems.push({
            jwt: jwt,
            memberItemId : memberItemId
        })
    }

    return tokenAndMemberItems;
}

export default function(datas) {
    const sellerData = datas[(exec.scenario.iterationInTest + 0) % datas.length]
    const buyerData = datas[(exec.scenario.iterationInTest + 1) % datas.length]

    const sellerToken = sellerData.jwt;
    const sellerMemberItemId = sellerData.memberItemId;

    const res = createMarketListing(
        sellerToken, 
        {
            memberItemId : sellerMemberItemId,
            totalPrice : 1000,
            quantity : 1,
            salesDuration : 'HOURS_12'
        },
        'v5'
    )

    const marketListingId = res.data.marketListingId

    const buyerToken = buyerData.jwt

    purchase(buyerToken, marketListingId, 'v3')
}

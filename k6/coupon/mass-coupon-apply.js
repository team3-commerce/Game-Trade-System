import { fail, sleep } from 'k6';
import exec from 'k6/execution';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { signup, login, submitCoupon, applyForCoupon } from "../util/user-actions.js"
import { Counter } from 'k6/metrics';

/*
이 테스트는 

설정

dummy:
  enabled: true
  mode: member_only
  member-count: 신청자 수

에 의존합니다
*/

const TARGET_RPS = 5000
const COUPON_COUNT = 100
const UNIQUE_ACCOUNTS = 1000

const couponsClaimed = new Counter('coupons_claimed');

const scenarios = {
    // 유저가 광클릭을 한다
    rate: {
        coupon_stampede: {
            executor: 'constant-arrival-rate',
            duration: '30s',
            rate: TARGET_RPS,
            timeUnit : '1s',

            preAllocatedVUs: parseInt(TARGET_RPS / 2),
            maxVus: parseInt(TARGET_RPS * 2),

            exec: 'stampedePhase',
        },
    },

    fixed: {
         coupon_stampede: {
            executor: 'per-vu-iterations',
            vus: 2000,
            iterations: 10,
            maxDuration: '30s',
            exec: 'stampedePhase',
        },
    }
}

export const options = {
    setupTimeout: '10m',

    scenarios: scenarios['fixed'],

    thresholds: {
        'coupons_claimed': ['count>0'],
    },
};


export function setup () {
    // ===============================
    // 쿠폰 발급
    // ===============================
    let loginInfo = signup({
        role : 'ADMIN'
    }, 'v1')

    if (loginInfo == null) {
        fail("관리자 회원가입 실패");
    }

    let jwt = login(loginInfo, 'v3')
    
    if (jwt == null) {
        fail("관리자 로그인 실패");
    }

    const body = submitCoupon(
        jwt, 
        {
            name : randomString(10),
            moneyAmount : 20000,
            issueType : 'FIRST_COME',
            totalQuantity: COUPON_COUNT
        },
        'v3'
    )

    // ==================================
    // 쿠폰 신청자들을 미리 로그인 시킴
    // ==================================
    let jwtTokens = [];

    for (let i=0; i<UNIQUE_ACCOUNTS; i++) {
        let jwt = login({
            email : `user${i}@test.com`,
            password : '1234567890'
        }, 'v3')

        if (jwt == null) {
            fail(`사용자 ${i} 로그인 실패`);
        }

        jwtTokens.push(jwt)

        if (i % 100 === 0) {
            console.log(`${i}/${UNIQUE_ACCOUNTS} logged in`)
        }
    }

    return {
        couponId : body.data.id,
        jwtTokens : jwtTokens
    }
}

export function stampedePhase (data) {
    const token = data.jwtTokens[exec.scenario.iterationInTest % data.jwtTokens.length]

    if (applyForCoupon(token, data.couponId, 'v3-2')) {
        couponsClaimed.add(1)
    }
}

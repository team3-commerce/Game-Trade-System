// import necessary modules
import http from 'k6/http';
import { fail, sleep } from 'k6';
import { signup, login, getManyItem } from "../util/user-actions.js";
import { SharedArray } from 'k6/data';
import { scenario } from 'k6/execution';

const tokenCount = 100;

export const options = {
  scenarios: {
    browse: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 2000 },
        { duration: '30s', target: 5000 },
        { duration: '30s', target: 2000 },
        { duration: '30s', target: 5000 },
        { duration: '10s', target: 2000 },
        { duration: '10s', target: 0 },
      ],
      gracefulRampDown: '30s',
    },
  },
};

export function setup () {
    let jwtTokens = [];

    for (let i=0; i<tokenCount; i++) {
        let loginInfo = signup();

        if (loginInfo == null) {
            fail("회원가입 실패");
        }

        let jwt = login(loginInfo)

        if (jwt == null) {
            fail("로그인 실패");
        }

        jwtTokens.push(jwt)
    }

    return jwtTokens;
}

// 유저가 로그인을 한다
export default function (data) {
    const token = data[scenario.iterationInTest % tokenCount];

    sleep(0.5) // page에 들어옴
    
    const pages = Math.floor(Math.random() * 3); // 0~2 pages
    for (let i = 0; i < pages; i++) {
        getManyItem(token, { page: i }, 'v1');
        sleep(0.5); // 잠깐 봄
    }

    sleep(Math.random() * 3 + 2) // 실제로 page 읽기
}


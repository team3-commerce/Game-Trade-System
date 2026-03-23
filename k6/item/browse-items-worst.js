// import necessary modules
import http from 'k6/http';
import { fail, sleep } from 'k6';
import { signup, login, getManyItem } from "../util/user-actions.js";
import { SharedArray } from 'k6/data';
import { randomString, randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { scenario } from 'k6/execution';

const tokenCount = 100;

const browseDeeper = true

export const options = {
  scenarios: {
    browse: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 300 },
        { duration: '30s', target: 750 },
        { duration: '30s', target: 300 },
        { duration: '30s', target: 750 },
        { duration: '10s', target: 300 },
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

const keywords = new SharedArray('keywords', function () {
    const keywords = [
        "불꽃", "얼음", "번개", "맹독", "암흑",
        "성스러운", "바람", "대지", "혼돈의", "시간의",
        "공간의", "피의", "영혼", "폭발", "흡혈",
        "마나", "중력", "음파", "환영", "소멸",
    ];

    return keywords;
});



// 유저가 로그인을 한다
export default function (data) {
    const token = data[scenario.iterationInTest % tokenCount];

    sleep(Math.random() * 3 + 2) // page에 들어옴, 검색어 입력
    
    const searchCount = randomIntBetween(1, 2); // 1, 2번 정도
    let lastKeyword = '';

    let totalPageCount = 0;

    for (let i = 0; i < searchCount; i++) {
        const keyword = keywords[randomIntBetween(0, keywords.length - 1)]
        lastKeyword = keyword
        let body = getManyItem(token, { page: 0 , keyword : keyword }, 'v3');
        sleep(0.5); // 잠깐 봄
        totalPageCount = body.data.totalPages
    }

    sleep(Math.random() * 3 + 2) // 실제로 page 읽기
    
    if (browseDeeper) {
        const pageCount = randomIntBetween(1, 3); // 랜덤 1 - 3 페이지

        for (let i = 0; i < pageCount; i++) {
            let page = totalPageCount - 1 - i

            page = Math.max(page, 0)
            page = Math.min(page, totalPageCount - 1)

            getManyItem(token, { page: page, keyword : lastKeyword}, 'v3');
            sleep(Math.random() * 3 + 2) // 실제로 page 읽기
        }
    } else {
        const pageCount = randomIntBetween(2, 4); // 2 - 4 page 까지

        for (let i = 1; i < pageCount; i++) {
            getManyItem(token, { page: i , keyword : lastKeyword}, 'v3');
            sleep(Math.random() * 3 + 2) // 실제로 page 읽기
        }
    }
}


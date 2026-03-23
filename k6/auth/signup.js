// import necessary modules
import http from 'k6/http';
import { signup } from "../util/user-actions.js";

export const options = {
  scenarios: {

    // 한시간 동안 1000명, 정도 회원가입이 잘 되는지
    signup: {
      executor: 'constant-arrival-rate',

      duration: '2m',

      rate: 1000,

      timeUnit: '1h',

      preAllocatedVUs: 5,
    },
  },
};

// 유저가 로그인을 한다

export default function () {
  signup();
}


package com.example.tradedemo.common.initializer;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.example.tradedemo.domain.coupon.service.CouponService;
import com.example.tradedemo.domain.coupon.enums.IssueType;
import com.example.tradedemo.domain.coupon.dto.CreateCouponPolicyRequest;

/**
 * 초기 item들을 생성해주는 class입니다.
 * <p>
 * 만약 설정 파일에
 * <pre>
 * {@code
 * app:
 *   add-test-items: true
 * }
 * </pre>
 *
 * 로 설정되어 있을 경우 test item들을 추가해 줍니다.
 */
@Component()
@ConditionalOnProperty(name = "app.add-test-signup-coupon", havingValue = "true", matchIfMissing = false)
@Profile("!prod")
@RequiredArgsConstructor
public class CouponInitializer implements ApplicationRunner {
    private final CouponService couponService;

    public void run(ApplicationArguments args) throws Exception {
        couponService.createCouponPolicyV2(CreateCouponPolicyRequest.of(
            "회원가입 웰컴 쿠폰",
            new BigDecimal(20000),
            IssueType.AUTO_SIGNUP,
            null,
            null,
            null
        ));
    }
}

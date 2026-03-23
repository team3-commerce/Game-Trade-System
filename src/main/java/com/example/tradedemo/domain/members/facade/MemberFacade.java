package com.example.tradedemo.domain.members.facade;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.marketlistings.service.MarketListingService;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.service.MemberService;
import com.example.tradedemo.domain.pending.service.PendingAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberService memberService;
    private final MarketListingService marketListingService;
    private final PendingAssetService pendingAssetService;

    /**
     * 회원 탈퇴 V1 (DB 기반)
     */
    @Transactional
    public void withdraw(String email) {
        validateWithdrawal(email);
        memberService.withdraw(email);
    }

    /**
     * 회원 탈퇴 V2 (로컬 캐시 기반)
     */
    @Transactional
    public void withdrawV2(String email) {
        validateWithdrawal(email);
        memberService.withdrawV2(email);
    }

    /**
     * 회원 탈퇴 V3 (Redis 기반)
     */
    @Transactional
    public void withdrawV3(String email) {
        validateWithdrawal(email);
        memberService.withdrawV3(email);
    }

    /**
     * 회원 탈퇴 전 공통 검증 로직
     */
    private void validateWithdrawal(String email) {
        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        // 거래소에 판매 중인 상품이 있는지 확인
        if (marketListingService.hasActiveListings(member.getId())) {
            throw new ServiceException(ErrorEnum.ERR_MEMBER_HAS_ACTIVE_LISTINGS);
        }

        // 수령 대기 중인 자산이 있는지 확인
        if (pendingAssetService.hasUnclaimedAssets(member.getId())) {
            throw new ServiceException(ErrorEnum.ERR_MEMBER_HAS_PENDING_ASSETS);
        }
    }
}

package com.example.tradedemo.domain.pending.controller;

import static com.example.tradedemo.auth.consts.AuthConst.SUCCESS_CODE;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.domain.pending.dto.PendingAssetResponse;
import com.example.tradedemo.domain.pending.facade.PendingAssetFacade;
import com.example.tradedemo.domain.pending.service.PendingAssetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PendingAssetController {

    private final PendingAssetService pendingAssetService;
    private final PendingAssetFacade pendingAssetFacade;

    /**
     * 내 수령 대기 테이블 조회
     */
    @GetMapping("/api/v1/me/pending-assets")
    public ResponseEntity<ApiResponse<List<PendingAssetResponse>>> getPendingAssets(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long memberId = principalDetails.getMember().getId();
        List<PendingAssetResponse> result = pendingAssetService.getPendingAssets(memberId);

        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, result));
    }


    /**
     * 개별 수령하기 V1
     */
    @PostMapping("/api/v1/me/pending-assets/{pendingAssetId}")
    public ResponseEntity<ApiResponse<Void>> settlement(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long pendingAssetId
    ) {
        Long memberId = principalDetails.getMember().getId();
        pendingAssetFacade.claimPendingAsset(memberId, pendingAssetId);

        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    /**
     * 개별 수령하기 V2 (RedisLock)
     */
    @PostMapping("/api/v2/me/pending-assets/{pendingAssetId}")
    public ResponseEntity<ApiResponse<Void>> settlementV2(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long pendingAssetId
    ) {
        Long memberId = principalDetails.getMember().getId();
        pendingAssetFacade.claimPendingAssetV2(memberId, pendingAssetId);

        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    /**
     * 개별 수령하기 V3 (RedissonLock)
     */
    @PostMapping("/api/v3/me/pending-assets/{pendingAssetId}")
    public ResponseEntity<ApiResponse<Void>> settlementV3(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long pendingAssetId
    ) {
        Long memberId = principalDetails.getMember().getId();
        pendingAssetFacade.claimPendingAssetV3(memberId, pendingAssetId);

        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

}

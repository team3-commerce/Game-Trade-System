package com.example.tradedemo.domain.pending.controller;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.domain.pending.dto.PendingAssetResponse;
import com.example.tradedemo.domain.pending.service.PendingAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PendingAssetController {

    private final PendingAssetService pendingAssetService;

    /**
     * 내 수령 대기 테이블 조회
     * @param principalDetails
     * @return
     */
    @GetMapping("/api/v1/me/pending-assets")
    public ResponseEntity<ApiResponse<List<PendingAssetResponse>>> getPendingAssets(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        Long memberId = principalDetails.getMember().getId();

        List<PendingAssetResponse> result =
                pendingAssetService.getPendingAssets(memberId);

        return ResponseEntity.ok(
                ApiResponse.success("200", result)
        );
    }


    /**
     * 개별 수령하기
     * 로그인 한 사용자, 수령 대기
     * @param principalDetails
     * @param pendingAssetId
     * @return
     */
    @PostMapping("/api/v1/me/pending-assets/{pendingAssetId}")
    public ResponseEntity<ApiResponse<Void>> settlement(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long pendingAssetId
    ) {

        Long memberId = principalDetails.getMember().getId();

        pendingAssetService.claimPendingAsset(memberId, pendingAssetId);

        return ResponseEntity.ok(
                ApiResponse.success("200", null)
        );
    }

    @PostMapping("/api/v2/me/pending-assets/{pendingAssetId}")
    public ResponseEntity<ApiResponse<Void>> settlementV2(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long pendingAssetId
    ) {

        Long memberId = principalDetails.getMember().getId();

        pendingAssetService.claimPendingAssetV2(memberId, pendingAssetId);

        return ResponseEntity.ok(
                ApiResponse.success("200", null)
        );
    }

    @PostMapping("/api/v3/me/pending-assets/{pendingAssetId}")
    public ResponseEntity<ApiResponse<Void>> settlementV3(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long pendingAssetId
    ) {

        Long memberId = principalDetails.getMember().getId();

        pendingAssetService.claimPendingAssetV3(memberId, pendingAssetId);

        return ResponseEntity.ok(
                ApiResponse.success("200", null)
        );
    }

}

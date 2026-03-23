package com.example.tradedemo.domain.members.controller;

import static com.example.tradedemo.auth.consts.AuthConst.SUCCESS_CODE;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.members.dto.GetAllMemberItemResponse;
import com.example.tradedemo.domain.members.dto.GetMemberItemResponse;
import com.example.tradedemo.domain.members.service.MemberItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberItemController {

    private final MemberItemService memberItemService;

    /**
     * 내 인벤토리 아이템 전체 조회
     * 아이템 획득일 기준으로 최신순으로 정렬
     */
    @GetMapping("/api/v1/me/items")
    public ResponseEntity<ApiResponse<PageResponse<GetAllMemberItemResponse>>> getAllMemberItem(
            @RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page, 10);
        Long memberId = principalDetails.getMember().getId();

        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_CODE, memberItemService.getAllMemberItem(memberId, pageable)));
    }

    @GetMapping("/api/v2/me/items")
    public ResponseEntity<ApiResponse<PageResponse<GetAllMemberItemResponse>>> getAllMemberItemV2(
            @RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page, 10);
        Long memberId = principalDetails.getMember().getId();

        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_CODE, memberItemService.getAllMemberItemV2(memberId, pageable)));
    }

    @GetMapping("/api/v3/me/items")
    public ResponseEntity<ApiResponse<PageResponse<GetAllMemberItemResponse>>> getAllMemberItemV3(
            @RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page, 10);
        Long memberId = principalDetails.getMember().getId();

        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_CODE, memberItemService.getAllMemberItemV3(memberId, pageable)));
    }

    /**
     * 내 인벤토리 아이템 단건 조회
     */
    @GetMapping("/api/v1/me/items/{memberItemId}")
    public ResponseEntity<ApiResponse<GetMemberItemResponse>> getMemberItem(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long memberItemId) {
        Long memberId = principalDetails.getMember().getId();

        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_CODE, memberItemService.getMemberItem(memberId, memberItemId)));
    }

    @GetMapping("/api/v2/me/items/{memberItemId}")
    public ResponseEntity<ApiResponse<GetMemberItemResponse>> getMemberItemV2(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long memberItemId) {
        Long memberId = principalDetails.getMember().getId();

        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_CODE, memberItemService.getMemberItemV2(memberId, memberItemId)));
    }

    @GetMapping("/api/v3/me/items/{memberItemId}")
    public ResponseEntity<ApiResponse<GetMemberItemResponse>> getMemberItemV3(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long memberItemId) {
        Long memberId = principalDetails.getMember().getId();

        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_CODE, memberItemService.getMemberItemV3(memberId, memberItemId)));
    }
}

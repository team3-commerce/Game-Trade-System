package com.example.tradedemo.domain.members.controller;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.domain.members.dto.GetAllMemberItemResponse;
import com.example.tradedemo.domain.members.dto.GetMemberItemResponse;
import com.example.tradedemo.domain.members.service.MemberItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<Page<GetAllMemberItemResponse>>> getAllMemberItem(
            @RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page, 10);
        Long memberId = principalDetails.getMember().getId();

        return ResponseEntity.ok(ApiResponse.success(
                String.valueOf(HttpStatus.OK), memberItemService.getAllMemberItem(memberId, pageable)));
    }

    /**
     * 내 인벤토리 아이템 단건 조회
     */
    @GetMapping("/api/v1/me/items/{memberItemId}")
    public ResponseEntity<ApiResponse<GetMemberItemResponse>> getMemberItem(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long memberItemId) {
        Long memberId = principalDetails.getMember().getId();

        return ResponseEntity.ok(ApiResponse.success(
                String.valueOf(HttpStatus.OK), memberItemService.getMemberItem(memberId, memberItemId)));
    }
}

package com.example.tradedemo.domain.members.controller;

import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.domain.members.dto.MemberResponse;
import com.example.tradedemo.domain.members.dto.NicknameUpdateRequest;
import com.example.tradedemo.domain.members.dto.PasswordUpdateRequest;
import com.example.tradedemo.domain.members.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 내 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        MemberResponse response = memberService.getMyInfo(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("200", response));
    }

    /**
     * 내 닉네임 수정
     */
    @PatchMapping("/me/nickname")
    public ResponseEntity<ApiResponse<Void>> updateNickname(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid NicknameUpdateRequest request) {

        memberService.updateNickname(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    /**
     * 내 비밀번호 수정
     */
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid PasswordUpdateRequest request) {

        memberService.updatePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMyinfo(@AuthenticationPrincipal UserDetails userDetails) {
        memberService.withdraw(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }
}

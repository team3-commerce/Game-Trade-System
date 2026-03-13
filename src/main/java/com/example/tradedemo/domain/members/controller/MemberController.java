package com.example.tradedemo.domain.members.controller;

import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.domain.members.dto.GetMyInfoResponse;
import com.example.tradedemo.domain.members.dto.SuspendMemberRequest;
import com.example.tradedemo.domain.members.dto.UpdateNicknameRequest;
import com.example.tradedemo.domain.members.dto.UpdatePasswordRequest;
import com.example.tradedemo.domain.members.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 내 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<GetMyInfoResponse>> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        GetMyInfoResponse response = memberService.getMyInfo(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("200", response));
    }

    /**
     * 내 닉네임 수정
     */
    @PatchMapping("/me/nickname")
    public ResponseEntity<ApiResponse<Void>> updateNickname(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid UpdateNicknameRequest request) {

        memberService.updateNickname(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    /**
     * 내 비밀번호 수정
     */
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid UpdatePasswordRequest request) {

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

    /**
     * 관리자 - 회원 계정 정지
     */
    @PatchMapping("/admin/suspend")
    public ResponseEntity<ApiResponse<Void>> suspend(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid SuspendMemberRequest request) {

        memberService.suspendMember(request);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }
}

package com.example.tradedemo.domain.members.controller;

import static com.example.tradedemo.auth.consts.AuthConst.SUCCESS_CODE;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.domain.members.dto.GetMyInfoResponse;
import com.example.tradedemo.domain.members.dto.SuspendMemberRequest;
import com.example.tradedemo.domain.members.dto.UpdateNicknameRequest;
import com.example.tradedemo.domain.members.dto.UpdatePasswordRequest;
import com.example.tradedemo.domain.members.facade.MemberFacade;
import com.example.tradedemo.domain.members.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberFacade memberFacade;

    /**
     * 내 정보 조회
     */
    @GetMapping("/v1/me")
    public ResponseEntity<ApiResponse<GetMyInfoResponse>> getMyInfo(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        GetMyInfoResponse response = memberService.getMyInfo(principalDetails.getEmail());
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, response));
    }

    @GetMapping("/v2/me")
    public ResponseEntity<ApiResponse<GetMyInfoResponse>> getMyInfoV2(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        GetMyInfoResponse response = memberService.getMyInfoV2(principalDetails.getEmail());
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, response));
    }

    @GetMapping("/v3/me")
    public ResponseEntity<ApiResponse<GetMyInfoResponse>> getMyInfoV3(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        GetMyInfoResponse response = memberService.getMyInfoV3(principalDetails.getEmail());
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, response));
    }

    /**
     * 내 닉네임 수정
     */
    @PatchMapping("/v1/me/nickname")
    public ResponseEntity<ApiResponse<Void>> updateNickname(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid UpdateNicknameRequest request) {

        memberService.updateNickname(principalDetails.getEmail(), request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    @PatchMapping("/v2/me/nickname")
    public ResponseEntity<ApiResponse<Void>> updateNicknameV2(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid UpdateNicknameRequest request) {

        memberService.updateNicknameV2(principalDetails.getEmail(), request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    @PatchMapping("/v3/me/nickname")
    public ResponseEntity<ApiResponse<Void>> updateNicknameV3(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid UpdateNicknameRequest request) {

        memberService.updateNicknameV3(principalDetails.getEmail(), request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    /**
     * 내 비밀번호 수정
     */
    @PatchMapping("/v1/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid UpdatePasswordRequest request) {

        memberService.updatePassword(principalDetails.getEmail(), request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    @PatchMapping("/v2/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePasswordV2(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid UpdatePasswordRequest request) {

        memberService.updatePasswordV2(principalDetails.getEmail(), request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    @PatchMapping("/v3/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePasswordV3(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid UpdatePasswordRequest request) {

        memberService.updatePasswordV3(principalDetails.getEmail(), request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/v1/me")
    public ResponseEntity<ApiResponse<Void>> deleteMyinfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        memberFacade.withdraw(principalDetails.getEmail());
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    @DeleteMapping("/v2/me")
    public ResponseEntity<ApiResponse<Void>> deleteMyinfoV2(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        memberFacade.withdrawV2(principalDetails.getEmail()); 
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    @DeleteMapping("/v3/me")
    public ResponseEntity<ApiResponse<Void>> deleteMyinfoV3(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        memberFacade.withdrawV3(principalDetails.getEmail());
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    /**
     * 관리자 - 회원 계정 정지
     */
    @PatchMapping("/v1/admin/suspend")
    public ResponseEntity<ApiResponse<Void>> suspend(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid SuspendMemberRequest request) {

        memberService.suspendMember(request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    @PatchMapping("/v2/admin/suspend")
    public ResponseEntity<ApiResponse<Void>> suspendV2(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid SuspendMemberRequest request) {

        memberService.suspendMemberV2(request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    @PatchMapping("/v3/admin/suspend")
    public ResponseEntity<ApiResponse<Void>> suspendV3(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid SuspendMemberRequest request) {

        memberService.suspendMemberV3(request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }
}

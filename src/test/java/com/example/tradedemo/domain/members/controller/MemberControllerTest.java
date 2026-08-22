package com.example.tradedemo.domain.members.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.domain.members.dto.GetMyInfoResponse;
import com.example.tradedemo.domain.members.dto.SuspendMemberRequest;
import com.example.tradedemo.domain.members.dto.UpdateNicknameRequest;
import com.example.tradedemo.domain.members.dto.UpdatePasswordRequest;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PrincipalDetails principalDetails;

    @BeforeEach
    void setUp() {
        Member member = Member.create("test@example.com", "password", "nickname", MemberRole.USER);
        principalDetails = new PrincipalDetails(member);

        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().isAssignableFrom(PrincipalDetails.class)
                                || parameter.getParameterType().isAssignableFrom(UserDetails.class);
                    }

                    @Override
                    public Object resolveArgument(
                            MethodParameter parameter,
                            ModelAndViewContainer mavContainer,
                            NativeWebRequest webRequest,
                            WebDataBinderFactory binderFactory) {
                        return principalDetails;
                    }
                })
                .build();
    }

    @Test
    @DisplayName("내 정보 조회 V1 API 성공")
    void getMyInfo_v1_api_success() throws Exception {
        GetMyInfoResponse response = GetMyInfoResponse.from(principalDetails.getMember());
        given(memberService.getMyInfo(anyString())).willReturn(response);

        mockMvc.perform(get("/api/v1/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andDo(print());
    }

    @Test
    @DisplayName("내 정보 조회 V2 API 성공")
    void getMyInfo_v2_api_success() throws Exception {
        GetMyInfoResponse response = GetMyInfoResponse.from(principalDetails.getMember());
        given(memberService.getMyInfoV2(anyString())).willReturn(response);

        mockMvc.perform(get("/api/v2/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andDo(print());
    }

    @Test
    @DisplayName("내 정보 조회 V3 API 성공")
    void getMyInfo_v3_api_success() throws Exception {
        GetMyInfoResponse response = GetMyInfoResponse.from(principalDetails.getMember());
        given(memberService.getMyInfoV3(anyString())).willReturn(response);

        mockMvc.perform(get("/api/v3/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 수정 V1 API 성공")
    void updateNickname_v1_api_success() throws Exception {
        UpdateNicknameRequest request = new UpdateNicknameRequest("newNickname");

        mockMvc.perform(patch("/api/v1/me/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 수정 V1 API 실패 - 빈 닉네임")
    void updateNickname_v1_api_fail_empty_nickname() throws Exception {
        UpdateNicknameRequest request = new UpdateNicknameRequest("");

        mockMvc.perform(patch("/api/v1/me/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 수정 V2 API 성공")
    void updateNickname_v2_api_success() throws Exception {
        UpdateNicknameRequest request = new UpdateNicknameRequest("newNicknameV2");

        mockMvc.perform(patch("/api/v2/me/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 수정 V3 API 성공")
    void updateNickname_v3_api_success() throws Exception {
        UpdateNicknameRequest request = new UpdateNicknameRequest("newNicknameV3");

        mockMvc.perform(patch("/api/v3/me/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호 수정 V1 API 성공")
    void updatePassword_v1_api_success() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest("oldPassword", "newPassword");

        mockMvc.perform(patch("/api/v1/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호 수정 V2 API 성공")
    void updatePassword_v2_api_success() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest("oldPassword", "newPasswordV2");

        mockMvc.perform(patch("/api/v2/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호 수정 V3 API 성공")
    void updatePassword_v3_api_success() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest("oldPassword", "newPasswordV3");

        mockMvc.perform(patch("/api/v3/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 탈퇴 V1 API 성공")
    void withdraw_v1_api_success() throws Exception {
        mockMvc.perform(delete("/api/v1/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 탈퇴 V2 API 성공")
    void withdraw_v2_api_success() throws Exception {
        mockMvc.perform(delete("/api/v2/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 탈퇴 V3 API 성공")
    void withdraw_v3_api_success() throws Exception {
        mockMvc.perform(delete("/api/v3/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("관리자 - 회원 계정 정지 V1 API 성공")
    void suspend_v1_api_success() throws Exception {
        SuspendMemberRequest request = new SuspendMemberRequest("target@example.com", "reason");

        mockMvc.perform(patch("/api/v1/admin/suspend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("관리자 - 회원 계정 정지 V2 API 성공")
    void suspend_v2_api_success() throws Exception {
        SuspendMemberRequest request = new SuspendMemberRequest("target2@example.com", "reason2");

        mockMvc.perform(patch("/api/v2/admin/suspend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("관리자 - 회원 계정 정지 V3 API 성공")
    void suspend_v3_api_success() throws Exception {
        SuspendMemberRequest request = new SuspendMemberRequest("target3@example.com", "reason3");

        mockMvc.perform(patch("/api/v3/admin/suspend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }
}

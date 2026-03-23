package com.example.tradedemo.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.tradedemo.auth.dto.*;
import com.example.tradedemo.auth.facade.AuthFacade;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.enums.MemberRole;
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
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthFacade authFacade;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PrincipalDetails principalDetails;

    @BeforeEach
    void setUp() {
        Member member = Member.create("test@example.com", "password", "nickname", MemberRole.USER);
        principalDetails = new PrincipalDetails(member);

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().isAssignableFrom(UserDetails.class);
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
    @DisplayName("회원가입 V1 API 성공")
    void signup_api_success() throws Exception {
        SignupAuthRequest request =
                new SignupAuthRequest("test@example.com", "password123", "nickname", MemberRole.USER);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 V1 API 실패 - 잘못된 이메일 형식")
    void signup_api_fail_invalid_email() throws Exception {
        SignupAuthRequest request =
                new SignupAuthRequest("invalid-email", "password123", "nickname", MemberRole.USER);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 V1 API 성공")
    void login_v1_api_success() throws Exception {
        LoginAuthRequest request = new LoginAuthRequest("test@example.com", "password123");
        TokenAuthResponse response = new TokenAuthResponse("at-v1", "rt-v1");
        given(authFacade.login(any(LoginAuthRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("at-v1"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 V2 API 성공")
    void login_v2_api_success() throws Exception {
        LoginAuthRequest request = new LoginAuthRequest("test@example.com", "password123");
        TokenAuthResponse response = new TokenAuthResponse("at-v2", "rt-v2");
        given(authFacade.loginV2(any(LoginAuthRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/v2/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("at-v2"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 V3 API 성공")
    void login_v3_api_success() throws Exception {
        LoginAuthRequest request = new LoginAuthRequest("test@example.com", "password123");
        TokenAuthResponse response = new TokenAuthResponse("at-v3", "rt-v3");
        given(authFacade.loginV3(any(LoginAuthRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/v3/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("at-v3"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 V1 API 성공")
    void logout_v1_api_success() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 V2 API 성공")
    void logout_v2_api_success() throws Exception {
        mockMvc.perform(post("/api/v2/auth/logout").header("Authorization", "Bearer accessToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 V3 API 성공")
    void logout_v3_api_success() throws Exception {
        mockMvc.perform(post("/api/v3/auth/logout").header("Authorization", "Bearer accessToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("토큰 재발급 V1 API 성공")
    void reissue_v1_api_success() throws Exception {
        TokenReissueRequest request = new TokenReissueRequest("refreshToken");
        TokenAuthResponse response = new TokenAuthResponse("new-at", "new-rt");
        given(authFacade.reissue(anyString())).willReturn(response);

        mockMvc.perform(post("/api/v1/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("new-at"))
                .andDo(print());
    }

    @Test
    @DisplayName("토큰 재발급 V2 API 성공")
    void reissue_v2_api_success() throws Exception {
        TokenReissueRequest request = new TokenReissueRequest("refreshToken");
        TokenAuthResponse response = new TokenAuthResponse("new-at-v2", "new-rt-v2");
        given(authFacade.reissueV2(anyString())).willReturn(response);

        mockMvc.perform(post("/api/v2/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("new-at-v2"))
                .andDo(print());
    }

    @Test
    @DisplayName("토큰 재발급 V3 API 성공")
    void reissue_v3_api_success() throws Exception {
        TokenReissueRequest request = new TokenReissueRequest("refreshToken");
        TokenAuthResponse response = new TokenAuthResponse("new-at-v3", "new-rt-v3");
        given(authFacade.reissueV3(anyString())).willReturn(response);

        mockMvc.perform(post("/api/v3/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("new-at-v3"))
                .andDo(print());
    }

    @Test
    @DisplayName("소셜 로그인 비밀번호 설정 V2 API 성공")
    void set_password_v2_api_success() throws Exception {
        SetPasswordRequest request = new SetPasswordRequest("new-password");

        mockMvc.perform(post("/api/v2/auth/set-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("소셜 로그인 비밀번호 설정 V3 API 성공")
    void set_password_v3_api_success() throws Exception {
        SetPasswordRequest request = new SetPasswordRequest("new-password-v3");

        mockMvc.perform(post("/api/v3/auth/set-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("소셜 연동 해제 V2 API 성공")
    void unlink_social_v2_api_success() throws Exception {
        UnlinkSocialRequest request =
                new UnlinkSocialRequest(com.example.tradedemo.domain.members.enums.SocialProvider.GOOGLE);

        mockMvc.perform(post("/api/v2/auth/unlink-social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("소셜 연동 해제 V3 API 성공")
    void unlink_social_v3_api_success() throws Exception {
        UnlinkSocialRequest request =
                new UnlinkSocialRequest(com.example.tradedemo.domain.members.enums.SocialProvider.GITHUB);

        mockMvc.perform(post("/api/v3/auth/unlink-social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("소셜 로그인 성공 토큰 확인 API 성공")
    void oauth_success_api_success() throws Exception {
        mockMvc.perform(get("/api/auth/oauth-success")
                        .param("accessToken", "at")
                        .param("refreshToken", "rt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("at"))
                .andDo(print());
    }
}

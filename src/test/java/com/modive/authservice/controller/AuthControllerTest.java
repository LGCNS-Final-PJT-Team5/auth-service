package com.modive.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modive.authservice.dto.request.AccessTokenRequest;
import com.modive.authservice.dto.request.KakaoRegisterRequest;
import com.modive.authservice.dto.response.ApiResponse;
import com.modive.authservice.dto.response.SignUpSuccessResponse;
import com.modive.authservice.exception.SignupRequiredException;
import com.modive.authservice.service.KakaoSocialService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Test
    void testRegisterSuccess() throws Exception {
        KakaoRegisterRequest request = new KakaoRegisterRequest();
        request.setAccessToken("validAccessToken");
        request.setNickname("nickname");
        request.setInterest("interest");
        request.setCarNumber("12345");
        request.setDrivingExperience(5L);

        SignUpSuccessResponse response = SignUpSuccessResponse.builder()
                .accessToken("newAccessToken")
                .refreshToken("newRefreshToken")
                .build();

        Mockito.when(kakaoSocialService.createKakaoUser(any(KakaoRegisterRequest.class)))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("newRefreshToken"));
    }

    @Test
    void testRegisterFailure() throws Exception {
        KakaoRegisterRequest request = new KakaoRegisterRequest();
        request.setAccessToken("invalidAccessToken");
        request.setNickname("nickname");
        request.setInterest("interest");
        request.setCarNumber("12345");
        request.setDrivingExperience(5L);

        Mockito.when(kakaoSocialService.createKakaoUser(any(KakaoRegisterRequest.class)))
                .thenThrow(new SignupRequiredException());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KakaoSocialService kakaoSocialService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSignUpSuccess() throws Exception {
        AccessTokenRequest request = new AccessTokenRequest();
        request.setAccessToken("validAccessToken");

        SignUpSuccessResponse response = SignUpSuccessResponse.builder()
                .accessToken("newAccessToken")
                .refreshToken("newRefreshToken")
                .build();

        Mockito.when(kakaoSocialService.kakaoSignUp(any(String.class)))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/kakao-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("newRefreshToken"));
    }

    @Test
    void testSignUpSignupRequired() throws Exception {
        AccessTokenRequest request = new AccessTokenRequest();
        request.setAccessToken("invalidAccessToken");

        Mockito.when(kakaoSocialService.kakaoSignUp(any(String.class)))
                .thenThrow(new SignupRequiredException());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/kakao-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
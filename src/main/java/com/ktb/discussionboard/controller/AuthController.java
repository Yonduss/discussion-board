package com.ktb.discussionboard.controller;

import com.ktb.discussionboard.dto.*;
import com.ktb.discussionboard.response.ApiResponse;
import com.ktb.discussionboard.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/users/signup")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(
            @Valid @RequestBody SignUpRequestDto request) {

        SignUpResponseDto response = authService.signUp(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("Sign up success", response));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto request) {

        LoginResponseDto response = authService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("Login success", response));
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<ApiResponse<TokenReissueResponseDto>> reissue(
            @RequestBody TokenReissueRequestDto request) {
        TokenReissueResponseDto result = authService.reissue(request);

        return ResponseEntity.ok(
                ApiResponse.of("Token reissued successfully", result)
        );
    }
}

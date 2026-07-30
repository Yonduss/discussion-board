package com.ktb.discussionboard.controller;

import com.ktb.discussionboard.dto.ChangePasswordRequestDto;
import com.ktb.discussionboard.dto.UpdateUserProfileRequestDto;
import com.ktb.discussionboard.dto.UserResponseDto;
import com.ktb.discussionboard.response.ApiResponse;
import com.ktb.discussionboard.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(
            Authentication authentication) {

        String email = authentication.getName();

        UserResponseDto result = userService.getUser(email);

        return ResponseEntity.ok(
                ApiResponse.of("User found successfully", result));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateUserProfileRequestDto request) {

        String email = authentication.getName();

        UserResponseDto result = userService.updateUserProfile(email, request);

        return ResponseEntity.ok(
                ApiResponse.of("User profile updated successfully", result));
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequestDto request) {

        String email = authentication.getName();

        userService.changePassword(email, request);

        return ResponseEntity.ok(
                ApiResponse.of("Password changed successfully", null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            Authentication authentication) {

        String email = authentication.getName();

        userService.deleteUser(email);

        return ResponseEntity.ok(
                ApiResponse.of("User deleted successfully", null));
    }
}

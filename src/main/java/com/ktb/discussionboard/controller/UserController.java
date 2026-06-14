package com.ktb.discussionboard.controller;

import com.ktb.discussionboard.dto.ChangePasswordRequestDto;
import com.ktb.discussionboard.dto.UpdateUserProfileRequestDto;
import com.ktb.discussionboard.dto.UserResponseDto;
import com.ktb.discussionboard.response.ApiResponse;
import com.ktb.discussionboard.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(
            @PathVariable Long userId) {

        UserResponseDto response = userService.getUser(userId);

        return ResponseEntity.ok(
                ApiResponse.of("User found successfully", response));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UpdateUserProfileRequestDto request) {

        UserResponseDto response = userService.updateUserProfile(userId, request);

        return ResponseEntity.ok(
                ApiResponse.of("User profile updated successfully", response));
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequestDto request) {

        userService.changePassword(userId, request);

        return ResponseEntity.ok(
                ApiResponse.of("Password changed successfully", null));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long userId) {

        userService.deleteUser(userId);

        return ResponseEntity.ok(
                ApiResponse.of("User deleted successfully", null));
    }
}
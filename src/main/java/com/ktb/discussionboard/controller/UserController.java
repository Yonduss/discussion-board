package com.ktb.discussionboard.controller;

import com.ktb.discussionboard.dto.ChangePasswordRequestDto;
import com.ktb.discussionboard.dto.MyCommentResponseDto;
import com.ktb.discussionboard.dto.MyPostResponseDto;
import com.ktb.discussionboard.dto.PageResponseDto;
import com.ktb.discussionboard.dto.UpdateUserProfileRequestDto;
import com.ktb.discussionboard.dto.UserResponseDto;
import com.ktb.discussionboard.response.ApiResponse;
import com.ktb.discussionboard.service.UserActivityService;
import com.ktb.discussionboard.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserActivityService userActivityService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(
            Authentication authentication) {

        String email = authentication.getName();

        UserResponseDto result = userService.getUser(email);

        return ResponseEntity.ok(
                ApiResponse.of("User found successfully", result));
    }

    @GetMapping("/me/posts")
    public ResponseEntity<ApiResponse<PageResponseDto<MyPostResponseDto>>> getMyPosts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {

        PageResponseDto<MyPostResponseDto> result = userActivityService.getMyPosts(
                authentication.getName(),
                page,
                size
        );

        return ResponseEntity.ok(
                ApiResponse.of("User posts found successfully", result)
        );
    }

    @GetMapping("/me/comments")
    public ResponseEntity<ApiResponse<PageResponseDto<MyCommentResponseDto>>> getMyComments(
            Authentication authentication,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {

        PageResponseDto<MyCommentResponseDto> result = userActivityService.getMyComments(
                authentication.getName(),
                page,
                size
        );

        return ResponseEntity.ok(
                ApiResponse.of("User comments found successfully", result)
        );
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

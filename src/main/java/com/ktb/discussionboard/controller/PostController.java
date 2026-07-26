package com.ktb.discussionboard.controller;

import com.ktb.discussionboard.dto.*;
import com.ktb.discussionboard.response.ApiResponse;
import com.ktb.discussionboard.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(
            Authentication authentication,
            @Valid @RequestBody CreatePostRequestDto request) {

        String email = authentication.getName();

        PostResponseDto result = postService.createPost(email, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("Post created successfully", result));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(
            Authentication authentication,
            @PathVariable Long postId) {

        String email = authentication.getName();

        PostResponseDto response = postService.getPost(email, postId);

        return ResponseEntity.ok(
                ApiResponse.of("Post found successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PostPageResponseDto>> getPosts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String email = authentication.getName();

        PostPageResponseDto response = postService.getPosts(email, page, size);

        return ResponseEntity.ok(
                ApiResponse.of("Posts found successfully", response));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(
            Authentication authentication,
            @PathVariable Long postId,
            @RequestBody UpdatePostRequestDto request) {

        String email = authentication.getName();

        PostResponseDto result = postService.updatePost(email, postId, request);

        return ResponseEntity.ok(
                ApiResponse.of("Post updated successfully", result));
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<Void>> likePost(
            Authentication authentication,
            @PathVariable Long postId) {

        String email = authentication.getName();

        postService.likePost(email, postId);

        return ResponseEntity.ok(
                ApiResponse.of("Post liked successfully", null));
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<Void>> unlikePost(
            Authentication authentication,
            @PathVariable Long postId) {

        String email = authentication.getName();

        postService.unlikePost(email, postId);

        return ResponseEntity.ok(
                ApiResponse.of("Post unliked successfully", null));
    }

    @PostMapping("/{postId}/reports")
    public ResponseEntity<ApiResponse<Void>> reportPost(
            Authentication authentication,
            @PathVariable Long postId,
            @Valid @RequestBody ReportPostRequestDto request) {

        String email = authentication.getName();

        postService.reportPost(email, postId, request.getReason());

        return ResponseEntity.ok(
                ApiResponse.of("Post reported successfully", null));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            Authentication authentication,
            @PathVariable Long postId) {

        String email = authentication.getName();

        postService.deletePost(email, postId);

        return ResponseEntity.ok(
                ApiResponse.of("Post deleted successfully", null));
    }
}
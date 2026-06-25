package com.ktb.discussionboard.controller;

import com.ktb.discussionboard.dto.CreatePostRequestDto;
import com.ktb.discussionboard.dto.PostResponseDto;
import com.ktb.discussionboard.dto.ReportPostRequestDto;
import com.ktb.discussionboard.dto.UpdatePostRequestDto;
import com.ktb.discussionboard.response.ApiResponse;
import com.ktb.discussionboard.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(
            @PathVariable Long userId,
            @Valid @RequestBody CreatePostRequestDto request) {

        PostResponseDto response = postService.createPost(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("Post created successfully", response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(
            @PathVariable Long postId) {

        PostResponseDto response = postService.getPost(postId);

        return ResponseEntity.ok(
                ApiResponse.of("Post found successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponseDto>>> getPosts() {

        List<PostResponseDto> response = postService.getPosts();

        return ResponseEntity.ok(
                ApiResponse.of("Posts found successfully", response));
    }

    @PatchMapping("/{userId}/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(
            @PathVariable Long userId,
            @PathVariable Long postId,
            @RequestBody UpdatePostRequestDto request) {

        PostResponseDto response = postService.updatePost(userId, postId, request);

        return ResponseEntity.ok(
                ApiResponse.of("Post updated successfully", response));
    }

    @PostMapping("/{userId}/{postId}/likes")
    public ResponseEntity<ApiResponse<Void>> likePost(
            @PathVariable Long userId,
            @PathVariable Long postId) {

        postService.likePost(userId, postId);

        return ResponseEntity.ok(
                ApiResponse.of("Post liked successfully", null));
    }

    @PostMapping("/{userId}/{postId}/reports")
    public ResponseEntity<ApiResponse<Void>> reportPost(
            @PathVariable Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody ReportPostRequestDto request) {

        postService.reportPost(userId, postId, request.getReason());

        return ResponseEntity.ok(
                ApiResponse.of("Post reported successfully", null));
    }

    @DeleteMapping("/{userId}/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long userId,
            @PathVariable Long postId) {

        postService.deletePost(userId, postId);

        return ResponseEntity.ok(
                ApiResponse.of("Post deleted successfully", null));
    }
}
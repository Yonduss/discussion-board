package com.ktb.discussionboard.controller;

import com.ktb.discussionboard.dto.CommentListResponseDto;
import com.ktb.discussionboard.dto.CommentResponseDto;
import com.ktb.discussionboard.dto.CreateCommentRequestDto;
import com.ktb.discussionboard.dto.UpdateCommentRequestDto;
import com.ktb.discussionboard.response.ApiResponse;
import com.ktb.discussionboard.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            Authentication authentication,
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequestDto request) {

        String email = authentication.getName();

        CommentResponseDto response = commentService.createComment(email, postId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("Comment created successfully", response));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> getComment(
            @PathVariable Long commentId) {

        CommentResponseDto response = commentService.getComment(commentId);

        return ResponseEntity.ok(
                ApiResponse.of("Comment found successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CommentListResponseDto>> getComments(
            @PathVariable Long postId) {

        CommentListResponseDto response = commentService.getComments(postId);

        return ResponseEntity.ok(
                ApiResponse.of("Comments found successfully", response));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            Authentication authentication,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequestDto request) {

        String email = authentication.getName();

        CommentResponseDto response = commentService.updateComment(email, commentId, request);

        return ResponseEntity.ok(
                ApiResponse.of("Comment updated successfully", response));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            Authentication authentication,
            @PathVariable Long commentId) {

        String email = authentication.getName();

        commentService.deleteComment(email, commentId);

        return ResponseEntity.ok(
                ApiResponse.of("Comment deleted successfully", null));
    }
}
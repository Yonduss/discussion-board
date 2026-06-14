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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @PathVariable Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequestDto request) {

        CommentResponseDto response = commentService.createComment(userId, postId, request);

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

    @PatchMapping("/{userId}/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequestDto request) {

        CommentResponseDto response = commentService.updateComment(userId, commentId, request);

        return ResponseEntity.ok(
                ApiResponse.of("Comment updated successfully", response));
    }

    @DeleteMapping("/{userId}/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long userId,
            @PathVariable Long commentId) {

        commentService.deleteComment(userId, commentId);

        return ResponseEntity.ok(
                ApiResponse.of("Comment deleted successfully", null));
    }
}
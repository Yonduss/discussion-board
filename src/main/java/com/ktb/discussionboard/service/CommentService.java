package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.Comment;
import com.ktb.discussionboard.dto.CommentListResponseDto;
import com.ktb.discussionboard.dto.CommentResponseDto;
import com.ktb.discussionboard.dto.CreateCommentRequestDto;
import com.ktb.discussionboard.dto.UpdateCommentRequestDto;
import com.ktb.discussionboard.exception.BusinessException;
import com.ktb.discussionboard.exception.ErrorCode;
import com.ktb.discussionboard.repository.CommentMemoryRepository;
import com.ktb.discussionboard.repository.PostMemoryRepository;
import com.ktb.discussionboard.repository.UserMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMemoryRepository commentMemoryRepository;
    private final UserMemoryRepository userMemoryRepository;
    private final PostMemoryRepository postMemoryRepository;

    public CommentResponseDto createComment(
            Long userId,
            Long postId,
            CreateCommentRequestDto request) {
        userMemoryRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        postMemoryRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (request.getParentCommentId() != null) {
            Comment parentComment = commentMemoryRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

            if (!parentComment.getPostId().equals(postId)) {
                throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
            }
        }

        Comment comment = new Comment(
                null,
                postId,
                userId,
                request.getParentCommentId(),
                request.getContent(),
                false,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Comment savedComment = commentMemoryRepository.save(comment);

        return toCommentResponseDto(savedComment);
    }

    public CommentResponseDto getComment(Long commentId) {
        Comment comment = commentMemoryRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        return toCommentResponseDto(comment);
    }

    public CommentListResponseDto getComments(Long postId) {
        postMemoryRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        List<CommentResponseDto> comments = commentMemoryRepository.findAllByPostId(postId)
                .stream()
                .map(this::toCommentResponseDto)
                .toList();

        return new CommentListResponseDto(comments);
    }

    public CommentResponseDto updateComment(
            Long userId,
            Long commentId,
            UpdateCommentRequestDto request) {
        Comment comment = commentMemoryRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        comment.setContent(request.getContent());
        comment.setEdited(true);
        comment.setUpdatedAt(LocalDateTime.now());

        return toCommentResponseDto(comment);
    }

    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentMemoryRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        comment.setContent("Deleted comment.");
        commentMemoryRepository.delete(comment);
    }

    private CommentResponseDto toCommentResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getPostId(),
                comment.getUserId(),
                comment.getParentCommentId(),
                comment.getContent(),
                comment.isDeleted(),
                comment.isEdited(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
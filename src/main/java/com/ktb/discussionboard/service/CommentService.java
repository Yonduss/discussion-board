package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.Comment;
import com.ktb.discussionboard.dto.CommentListResponseDto;
import com.ktb.discussionboard.dto.CommentResponseDto;
import com.ktb.discussionboard.dto.CreateCommentRequestDto;
import com.ktb.discussionboard.dto.UpdateCommentRequestDto;
import com.ktb.discussionboard.exception.BusinessException;
import com.ktb.discussionboard.exception.ErrorCode;
import com.ktb.discussionboard.repository.CommentRepository;
import com.ktb.discussionboard.repository.PostRepository;
import com.ktb.discussionboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponseDto createComment(
            Long userId,
            Long postId,
            CreateCommentRequestDto request) {
        userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (request.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findByIdAndDeletedFalse(request.getParentCommentId())
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

        Comment savedComment = commentRepository.save(comment);

        return toCommentResponseDto(savedComment);
    }

    @Transactional(readOnly = true)
    public CommentResponseDto getComment(Long commentId) {
        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        return toCommentResponseDto(comment);
    }

    @Transactional(readOnly = true)
    public CommentListResponseDto getComments(Long postId) {
        postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        List<CommentResponseDto> comments = commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::toCommentResponseDto)
                .toList();

        return new CommentListResponseDto(comments);
    }

    @Transactional
    public CommentResponseDto updateComment(
            Long userId,
            Long commentId,
            UpdateCommentRequestDto request) {
        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        comment.setContent(request.getContent());
        comment.setEdited(true);
        comment.setUpdatedAt(LocalDateTime.now());

        return toCommentResponseDto(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        comment.setContent("Deleted comment.");
        comment.setDeleted(true);
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
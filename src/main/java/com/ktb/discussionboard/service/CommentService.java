package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.Comment;
import com.ktb.discussionboard.domain.Post;
import com.ktb.discussionboard.domain.User;
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
            String email,
            Long postId,
            CreateCommentRequestDto request) {

        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        Comment parentComment = null;

        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

            if (!parentComment.getPost().getId().equals(postId)) {
                throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
            }

            if (parentComment.getParentComment() != null) {
                throw new BusinessException(ErrorCode.NESTED_REPLY_NOT_ALLOWED);
            }
        }

        Comment comment = new Comment(
                null,
                user,
                post,
                parentComment,
                request.getContent(),
                false,
                false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
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

        List<CommentResponseDto> comments = commentRepository.findAllByPost_IdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::toCommentResponseDto)
                .toList();

        return new CommentListResponseDto(comments);
    }

    @Transactional
    public CommentResponseDto updateComment(
            String email,
            Long commentId,
            UpdateCommentRequestDto request) {

        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        comment.setContent(request.getContent());
        comment.setEdited(true);
        comment.setUpdatedAt(LocalDateTime.now());

        return toCommentResponseDto(comment);
    }

    @Transactional
    public void deleteComment(String email, Long commentId) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        comment.setContent("Deleted comment.");
        comment.setDeleted(true);
        comment.setDeletedAt(LocalDateTime.now());
    }

    private CommentResponseDto toCommentResponseDto(Comment comment) {
        User user = comment.getUser();

        Long parentCommentId = comment.getParentComment() == null
                ? null : comment.getParentComment().getId();

        return new CommentResponseDto(
                comment.getId(),
                comment.getPost().getId(),
                user.getId(),
                user.getNickname(),
                user.resolveDisplayProfileImageUrl(),
                parentCommentId,
                comment.getContent(),
                comment.isDeleted(),
                comment.isEdited(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}

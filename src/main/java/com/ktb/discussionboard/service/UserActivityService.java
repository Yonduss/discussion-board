package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.Comment;
import com.ktb.discussionboard.domain.Post;
import com.ktb.discussionboard.domain.User;
import com.ktb.discussionboard.dto.MyCommentResponseDto;
import com.ktb.discussionboard.dto.MyPostResponseDto;
import com.ktb.discussionboard.dto.PageResponseDto;
import com.ktb.discussionboard.exception.BusinessException;
import com.ktb.discussionboard.exception.ErrorCode;
import com.ktb.discussionboard.repository.CommentRepository;
import com.ktb.discussionboard.repository.PostRepository;
import com.ktb.discussionboard.repository.UserRepository;
import com.ktb.discussionboard.repository.projection.PostCommentCountProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public PageResponseDto<MyPostResponseDto> getMyPosts(
            String email,
            int page,
            int size
    ) {
        User user = getActiveUser(email);
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> postPage = postRepository
                .findAllByUser_IdAndDeletedFalseAndHiddenFalseOrderByCreatedAtDesc(
                        user.getId(),
                        pageable
                );

        List<Post> posts = postPage.getContent();
        Map<Long, Integer> commentCountsByPostId = getCommentCounts(posts);

        List<MyPostResponseDto> content = posts.stream()
                .map(post -> new MyPostResponseDto(
                        post.getId(),
                        post.getTitle(),
                        post.getLikeCount(),
                        post.getViewCount(),
                        commentCountsByPostId.getOrDefault(post.getId(), 0),
                        post.isEdited(),
                        post.getCreatedAt()
                ))
                .toList();

        return toPageResponse(postPage, content);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<MyCommentResponseDto> getMyComments(
            String email,
            int page,
            int size
    ) {
        User user = getActiveUser(email);
        Pageable pageable = PageRequest.of(page, size);

        Page<Comment> commentPage = commentRepository
                .findAllByUser_IdAndDeletedFalseAndPost_DeletedFalseAndPost_HiddenFalseOrderByCreatedAtDesc(
                        user.getId(),
                        pageable
                );

        List<MyCommentResponseDto> content = commentPage.getContent()
                .stream()
                .map(comment -> new MyCommentResponseDto(
                        comment.getId(),
                        comment.getPost().getId(),
                        comment.getPost().getTitle(),
                        comment.getContent(),
                        comment.isEdited(),
                        comment.getCreatedAt()
                ))
                .toList();

        return toPageResponse(commentPage, content);
    }

    private User getActiveUser(String email) {
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Map<Long, Integer> getCommentCounts(List<Post> posts) {
        if (posts.isEmpty()) {
            return Map.of();
        }

        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();

        return commentRepository.countActiveCommentsByPostIds(postIds)
                .stream()
                .collect(Collectors.toMap(
                        PostCommentCountProjection::getPostId,
                        count -> Math.toIntExact(count.getCommentCount())
                ));
    }

    private <T> PageResponseDto<T> toPageResponse(
            Page<?> page,
            List<T> content
    ) {
        return new PageResponseDto<>(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                content
        );
    }
}

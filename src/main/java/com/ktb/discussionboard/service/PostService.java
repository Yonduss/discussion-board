package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.Post;
import com.ktb.discussionboard.dto.CreatePostRequestDto;
import com.ktb.discussionboard.dto.PostResponseDto;
import com.ktb.discussionboard.dto.UpdatePostRequestDto;
import com.ktb.discussionboard.exception.BusinessException;
import com.ktb.discussionboard.exception.ErrorCode;
import com.ktb.discussionboard.repository.PostRepository;
import com.ktb.discussionboard.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponseDto createPost(Long userId, CreatePostRequestDto request) {
        userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = new Post(
                null,
                userId,
                request.getTitle(),
                request.getContent(),
                request.getPostImageUrl(),
                0,
                0,
                0,
                false,
                false,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Post savedPost = postRepository.save(post);

        return toPostResponseDto(savedPost);
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        post.setViewCount(post.getViewCount() + 1);

        return toPostResponseDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts() {
        return postRepository.findAllByDeletedFalseAndHiddenFalseOrderByCreatedAtDesc().stream()
                .map(this::toPostResponseDto)
                .toList();
    }

    @Transactional
    public PostResponseDto updatePost(Long userId, Long postId, UpdatePostRequestDto request) {
        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }

        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }

        if (request.getPostImageUrl() != null) {
            post.setPostImageUrl(request.getPostImageUrl());
        }

        post.setEdited(true);
        post.setUpdatedAt(LocalDateTime.now());

        return toPostResponseDto(post);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        post.setDeleted(true);
    }

    @Transactional
    public void likePost(Long userId, Long postId) {
        userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        post.setLikeCount(post.getLikeCount() + 1);
    }

    @Transactional
    public void reportPost(Long userId, Long postId) {
        userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (post.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        post.setReportedCount(post.getReportedCount() + 1);

        if (post.getReportedCount() >= 5) {
            post.setHidden(true);
        }
    }

    private PostResponseDto toPostResponseDto(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getUserId(),
                post.getTitle(),
                post.getContent(),
                post.getPostImageUrl(),
                post.getLikeCount(),
                post.getViewCount(),
                post.isEdited(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
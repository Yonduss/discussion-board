package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.Post;
import com.ktb.discussionboard.domain.PostImage;
import com.ktb.discussionboard.domain.PostLike;
import com.ktb.discussionboard.domain.PostReport;
import com.ktb.discussionboard.dto.CreatePostRequestDto;
import com.ktb.discussionboard.dto.PostResponseDto;
import com.ktb.discussionboard.dto.UpdatePostRequestDto;
import com.ktb.discussionboard.exception.BusinessException;
import com.ktb.discussionboard.exception.ErrorCode;
import com.ktb.discussionboard.repository.*;
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
    private final PostLikeRepository postLikeRepository;
    private final PostReportRepository postReportRepository;
    private final PostImageRepository postImageRepository;

    @Transactional
    public PostResponseDto createPost(Long userId, CreatePostRequestDto request) {
        userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = new Post(
                null,
                userId,
                request.getTitle(),
                request.getContent(),
                0,
                0,
                0,
                false,
                false,
                false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null
        );

        Post savedPost = postRepository.save(post);

        if (request.getPostImageUrls() != null) {
            for (int i = 0; i < request.getPostImageUrls().size(); i++) {
                PostImage postImage = new PostImage(
                        null,
                        savedPost.getId(),
                        request.getPostImageUrls().get(i),
                        i
                );
                postImageRepository.save(postImage);
            }
        }

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

        if (request.getPostImageUrls() != null) {
            postImageRepository.deleteAllByPostId(postId);

            for (int i = 0; i < request.getPostImageUrls().size(); i++) {
                PostImage postImage = new PostImage(
                        null,
                        postId,
                        request.getPostImageUrls().get(i),
                        i
                );

                postImageRepository.save(postImage);
            }
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
        post.setDeletedAt(LocalDateTime.now());
    }

    @Transactional
    public void likePost(Long userId, Long postId) {
        userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new BusinessException(ErrorCode.POST_ALREADY_LIKED);
        }

        PostLike postLike = new PostLike(
                null,
                userId,
                postId,
                LocalDateTime.now()
        );

        postLikeRepository.save(postLike);

        post.setLikeCount(post.getLikeCount() + 1);
    }

    @Transactional
    public void reportPost(Long userId, Long postId, String reason) {
        userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (postReportRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new BusinessException(ErrorCode.POST_ALREADY_REPORTED);
        }

        PostReport postReport = new PostReport(
                null,
                userId,
                postId,
                reason,
                LocalDateTime.now()
        );

        postReportRepository.save(postReport);

        post.setReportedCount(post.getReportedCount() + 1);

        if (post.getReportedCount() >= 5) {
            post.setHidden(true);
            post.setHiddenAt(LocalDateTime.now());
        }
    }

    private PostResponseDto toPostResponseDto(Post post) {
        List<String> postImageUrls = postImageRepository
                .findAllByPostIdOrderBySortOrderAsc(post.getId())
                .stream()
                .map(PostImage::getImageUrl)
                .toList();

        return new PostResponseDto(
                post.getId(),
                post.getUserId(),
                post.getTitle(),
                post.getContent(),
                postImageUrls,
                post.getLikeCount(),
                post.getViewCount(),
                post.isEdited(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
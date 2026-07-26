package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.*;
import com.ktb.discussionboard.dto.CreatePostRequestDto;
import com.ktb.discussionboard.dto.PostPageResponseDto;
import com.ktb.discussionboard.dto.PostResponseDto;
import com.ktb.discussionboard.dto.UpdatePostRequestDto;
import com.ktb.discussionboard.exception.BusinessException;
import com.ktb.discussionboard.exception.ErrorCode;
import com.ktb.discussionboard.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostReportRepository postReportRepository;
    private final PostImageRepository postImageRepository;

    @Transactional
    public PostResponseDto createPost(String email, CreatePostRequestDto request) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = new Post(
                null,
                user,
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
                        savedPost,
                        request.getPostImageUrls().get(i),
                        i
                );
                postImageRepository.save(postImage);
            }
        }

        return toPostResponseDto(savedPost);
    }

    @Transactional
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        post.setViewCount(post.getViewCount() + 1);

        return toPostResponseDto(post);
    }

    @Transactional(readOnly = true)
    public PostPageResponseDto getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> postPage = postRepository
                .findAllByDeletedFalseAndHiddenFalseOrderByCreatedAtDesc(pageable);

        List<PostResponseDto> posts = postPage.getContent().stream()
                .map(this::toPostResponseDto)
                .toList();

        return new PostPageResponseDto(
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                postPage.hasNext(),
                posts
        );
    }

    @Transactional
    public PostResponseDto updatePost(String email, Long postId, UpdatePostRequestDto request) {
        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }

        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }

        if (request.getPostImageUrls() != null) {
            postImageRepository.deleteAllByPost_Id(postId);

            for (int i = 0; i < request.getPostImageUrls().size(); i++) {
                PostImage postImage = new PostImage(
                        null,
                        post,
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
    public void deletePost(String email, Long postId) {
        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        post.setDeleted(true);
        post.setDeletedAt(LocalDateTime.now());

        List<Comment> comments = commentRepository.findAllByPost_IdOrderByCreatedAtAsc(postId);

        for (Comment comment : comments) {
            comment.setDeleted(true);
            comment.setDeletedAt(LocalDateTime.now());
        }
    }

    @Transactional
    public void likePost(String email, Long postId) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (postLikeRepository.existsByUser_IdAndPost_Id(user.getId(), postId)) {
            throw new BusinessException(ErrorCode.POST_ALREADY_LIKED);
        }

        PostLike postLike = new PostLike(
                null,
                user,
                post,
                LocalDateTime.now()
        );

        postLikeRepository.save(postLike);

        post.setLikeCount(post.getLikeCount() + 1);
    }

    @Transactional
    public void reportPost(String email, Long postId, String reason) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (postReportRepository.existsByUser_IdAndPost_Id(user.getId(), postId)) {
            throw new BusinessException(ErrorCode.POST_ALREADY_REPORTED);
        }

        PostReport postReport = new PostReport(
                null,
                user,
                post,
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
                .findAllByPost_IdOrderBySortOrderAsc(post.getId())
                .stream()
                .map(PostImage::getImageUrl)
                .toList();

        User user = post.getUser();

        int commentCount = commentRepository.countByPost_IdAndDeletedFalse(post.getId());

        return new PostResponseDto(
                post.getId(),
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                post.getTitle(),
                post.getContent(),
                postImageUrls,
                post.getLikeCount(),
                post.getViewCount(),
                commentCount,
                post.isEdited(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
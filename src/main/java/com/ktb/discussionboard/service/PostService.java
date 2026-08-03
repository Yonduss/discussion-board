package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.*;
import com.ktb.discussionboard.dto.*;
import com.ktb.discussionboard.exception.BusinessException;
import com.ktb.discussionboard.exception.ErrorCode;
import com.ktb.discussionboard.repository.*;
import com.ktb.discussionboard.repository.projection.PostCommentCountProjection;
import com.ktb.discussionboard.repository.projection.PostImageProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

        return toPostResponseDto(savedPost, user);
    }

    @Transactional
    public PostResponseDto getPost(String email, Long postId) {
        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        User currentUser = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        post.setViewCount(post.getViewCount() + 1);

        return toPostResponseDto(post, currentUser);
    }

    @Transactional(readOnly = true)
    public PostPageResponseDto getPosts(String email, int page, int size, String keyword) {
        User currentUser = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);

        Page<Post> postPage;

        if (keyword == null || keyword.isBlank()) {
            postPage = postRepository.findAllByDeletedFalseAndHiddenFalseOrderByCreatedAtDesc(pageable);
        } else {
            String escapedKeyword = escapeLikeWildcards(keyword.trim());
            postPage = postRepository.searchPosts(escapedKeyword, pageable);
        }

        List<Post> pagePosts = postPage.getContent();
        List<PostResponseDto> posts = toPostResponseDtos(pagePosts, currentUser);

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

        return toPostResponseDto(post, user);
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
    public PostLikeResponseDto likePost(String email, Long postId) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if(postLikeRepository.existsByUser_IdAndPost_Id(user.getId(), postId)) {
            throw new BusinessException(ErrorCode.POST_ALREADY_LIKED);
        }

        PostLike postLike = new PostLike(
                        null,
                        user,
                        post,
                        LocalDateTime.now());

        postLikeRepository.save(postLike);

        int updatedLikeCount = post.getLikeCount() + 1;

        postRepository.increaseLikeCount(postId);

        return new PostLikeResponseDto(
                updatedLikeCount,
                true
        );
    }

    @Transactional
    public PostLikeResponseDto unlikePost(String email, Long postId){
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        PostLike postLike = postLikeRepository.findByUser_IdAndPost_Id(user.getId(), postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_LIKED));

        postLikeRepository.delete(postLike);

        int updatedLikeCount = Math.max(post.getLikeCount() - 1, 0);

        postRepository.decreaseLikeCount(postId);

        return new PostLikeResponseDto(
                updatedLikeCount,
                false
        );
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

    private PostResponseDto toPostResponseDto(Post post, User currentUser) {
        List<String> postImageUrls = postImageRepository
                        .findAllByPost_IdOrderBySortOrderAsc(post.getId())
                        .stream()
                        .map(PostImage::getImageUrl)
                        .toList();

        int commentCount = commentRepository.countByPost_IdAndDeletedFalse(post.getId());

        boolean liked = postLikeRepository.existsByUser_IdAndPost_Id(currentUser.getId(), post.getId());

        return toPostResponseDto(post, postImageUrls, commentCount, liked);
    }

    private List<PostResponseDto> toPostResponseDtos(
            List<Post> posts, User currentUser) {
        if (posts.isEmpty()) {
            return List.of();
        }

        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();

        Map<Long, List<String>> imageUrlsByPostId = postImageRepository
                .findAllProjectedByPostIds(postIds)
                .stream()
                .collect(Collectors.groupingBy(
                        PostImageProjection::getPostId,
                        Collectors.mapping(
                                PostImageProjection::getImageUrl,
                                Collectors.toList()
                        )
                ));

        Map<Long, Integer> commentCountsByPostId = commentRepository
                .countActiveCommentsByPostIds(postIds)
                .stream()
                .collect(Collectors.toMap(
                        PostCommentCountProjection::getPostId,
                        count -> Math.toIntExact(count.getCommentCount())
                ));

        Set<Long> likedPostIds = postLikeRepository.findLikedPostIds(
                currentUser.getId(),
                postIds
        );

        return posts.stream()
                .map(post -> toPostResponseDto(
                        post,
                        imageUrlsByPostId.getOrDefault(post.getId(), List.of()),
                        commentCountsByPostId.getOrDefault(post.getId(), 0),
                        likedPostIds.contains(post.getId())
                ))
                .toList();
    }

    private PostResponseDto toPostResponseDto(
            Post post,
            List<String> postImageUrls,
            int commentCount,
            boolean liked
    ) {
        User user = post.getUser();

        return new PostResponseDto(
                post.getId(),
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                post.getTitle(),
                post.getContent(),
                postImageUrls,
                post.getLikeCount(),
                liked,
                post.getViewCount(),
                commentCount,
                post.isEdited(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    private String escapeLikeWildcards(String keyword) {
        return keyword
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
    }
}

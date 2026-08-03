package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.Post;
import com.ktb.discussionboard.domain.User;
import com.ktb.discussionboard.dto.PostPageResponseDto;
import com.ktb.discussionboard.dto.PostResponseDto;
import com.ktb.discussionboard.repository.CommentRepository;
import com.ktb.discussionboard.repository.PostImageRepository;
import com.ktb.discussionboard.repository.PostLikeRepository;
import com.ktb.discussionboard.repository.PostReportRepository;
import com.ktb.discussionboard.repository.PostRepository;
import com.ktb.discussionboard.repository.UserRepository;
import com.ktb.discussionboard.repository.projection.PostCommentCountProjection;
import com.ktb.discussionboard.repository.projection.PostImageProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostReportRepository postReportRepository;

    @Mock
    private PostImageRepository postImageRepository;

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostService(
                postRepository,
                userRepository,
                commentRepository,
                postLikeRepository,
                postReportRepository,
                postImageRepository
        );
    }

    @Test
    @DisplayName("Get posts loads list metadata in bulk")
    void getPosts_loadsRelatedDataInBulk() {
        // given
        String email = "viewer@test.com";
        User currentUser = createUser(99L, email, "viewer");
        User firstAuthor = createUser(10L, "first@test.com", "first");
        User secondAuthor = createUser(20L, "second@test.com", "second");

        Post firstPost = createPost(1L, firstAuthor, "First post", 2);
        Post secondPost = createPost(2L, secondAuthor, "Second post", 5);

        PageRequest pageable = PageRequest.of(0, 10);
        given(userRepository.findByEmailAndDeletedFalse(email))
                .willReturn(Optional.of(currentUser));
        given(postRepository.findAllByDeletedFalseAndHiddenFalseOrderByCreatedAtDesc(pageable))
                .willReturn(new PageImpl<>(List.of(firstPost, secondPost), pageable, 2));

        PostImageProjection firstImage = imageProjection(1L, "first-1.jpg");
        PostImageProjection secondImage = imageProjection(1L, "first-2.jpg");
        given(postImageRepository.findAllProjectedByPostIds(List.of(1L, 2L)))
                .willReturn(List.of(firstImage, secondImage));

        PostCommentCountProjection firstCommentCount = commentCountProjection(1L, 3L);
        given(commentRepository.countActiveCommentsByPostIds(List.of(1L, 2L)))
                .willReturn(List.of(firstCommentCount));

        given(postLikeRepository.findLikedPostIds(99L, List.of(1L, 2L)))
                .willReturn(Set.of(2L));

        // when
        PostPageResponseDto result = postService.getPosts(email, 0, 10, null);

        // then
        assertThat(result.getPosts()).hasSize(2);

        PostResponseDto firstResult = result.getPosts().get(0);
        assertThat(firstResult.getPostImageUrls())
                .containsExactly("first-1.jpg", "first-2.jpg");
        assertThat(firstResult.getCommentCount()).isEqualTo(3);
        assertThat(firstResult.isLiked()).isFalse();

        PostResponseDto secondResult = result.getPosts().get(1);
        assertThat(secondResult.getPostImageUrls()).isEmpty();
        assertThat(secondResult.getCommentCount()).isZero();
        assertThat(secondResult.isLiked()).isTrue();

        then(postImageRepository).should()
                .findAllProjectedByPostIds(List.of(1L, 2L));
        then(commentRepository).should()
                .countActiveCommentsByPostIds(List.of(1L, 2L));
        then(postLikeRepository).should()
                .findLikedPostIds(99L, List.of(1L, 2L));

        then(postImageRepository).should(never())
                .findAllByPost_IdOrderBySortOrderAsc(1L);
        then(commentRepository).should(never())
                .countByPost_IdAndDeletedFalse(1L);
        then(postLikeRepository).should(never())
                .existsByUser_IdAndPost_Id(99L, 1L);
    }

    @Test
    @DisplayName("Get empty posts page skips bulk metadata queries")
    void getPosts_withEmptyPage_skipsRelatedDataQueries() {
        // given
        String email = "viewer@test.com";
        User currentUser = createUser(99L, email, "viewer");
        PageRequest pageable = PageRequest.of(0, 10);

        given(userRepository.findByEmailAndDeletedFalse(email))
                .willReturn(Optional.of(currentUser));
        given(postRepository.findAllByDeletedFalseAndHiddenFalseOrderByCreatedAtDesc(pageable))
                .willReturn(new PageImpl<>(List.of(), pageable, 0));

        // when
        PostPageResponseDto result = postService.getPosts(email, 0, 10, null);

        // then
        assertThat(result.getPosts()).isEmpty();
        then(postImageRepository).shouldHaveNoInteractions();
        then(commentRepository).shouldHaveNoInteractions();
        then(postLikeRepository).shouldHaveNoInteractions();
    }

    private User createUser(Long id, String email, String nickname) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setNickname(nickname);
        user.setProfileImageUrl(nickname + ".jpg");
        return user;
    }

    private Post createPost(Long id, User author, String title, int likeCount) {
        LocalDateTime now = LocalDateTime.now();

        return new Post(
                id,
                author,
                title,
                title + " content",
                likeCount,
                10,
                0,
                false,
                false,
                false,
                now,
                now,
                null,
                null
        );
    }

    private PostImageProjection imageProjection(Long postId, String imageUrl) {
        PostImageProjection projection = mock(PostImageProjection.class);
        given(projection.getPostId()).willReturn(postId);
        given(projection.getImageUrl()).willReturn(imageUrl);
        return projection;
    }

    private PostCommentCountProjection commentCountProjection(
            Long postId,
            long commentCount
    ) {
        PostCommentCountProjection projection = mock(PostCommentCountProjection.class);
        given(projection.getPostId()).willReturn(postId);
        given(projection.getCommentCount()).willReturn(commentCount);
        return projection;
    }
}

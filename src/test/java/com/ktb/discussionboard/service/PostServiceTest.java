package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.MlbTeam;
import com.ktb.discussionboard.domain.Post;
import com.ktb.discussionboard.domain.PostLike;
import com.ktb.discussionboard.domain.User;
import com.ktb.discussionboard.dto.PostPageResponseDto;
import com.ktb.discussionboard.dto.PostLikeResponseDto;
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
        firstAuthor.updateFavoriteTeam(MlbTeam.TEX, true);

        Post firstPost = createPost(1L, firstAuthor, "First post", 2);
        Post secondPost = createPost(2L, secondAuthor, "Second post", 5);

        PageRequest pageable = PageRequest.of(0, 10);
        given(userRepository.findByEmailAndDeletedFalse(email))
                .willReturn(Optional.of(currentUser));
        given(postRepository.findAllByDeletedFalseAndHiddenFalseOrderByCreatedAtDesc(pageable))
                .willReturn(new PageImpl<>(List.of(firstPost, secondPost), pageable, 2));

        PostImageProjection firstImage = mock(PostImageProjection.class);
        given(firstImage.getPostId()).willReturn(1L);
        given(firstImage.getImageUrl()).willReturn("first-1.jpg");

        PostImageProjection secondImage = mock(PostImageProjection.class);
        given(secondImage.getPostId()).willReturn(1L);
        given(secondImage.getImageUrl()).willReturn("first-2.jpg");

        given(postImageRepository.findAllProjectedByPostIds(List.of(1L, 2L)))
                .willReturn(List.of(firstImage, secondImage));

        PostCommentCountProjection firstCommentCount = mock(
                PostCommentCountProjection.class
        );
        given(firstCommentCount.getPostId()).willReturn(1L);
        given(firstCommentCount.getCommentCount()).willReturn(3L);

        given(commentRepository.countActiveCommentsByPostIds(List.of(1L, 2L)))
                .willReturn(List.of(firstCommentCount));

        given(postLikeRepository.findLikedPostIds(99L, List.of(1L, 2L)))
                .willReturn(Set.of(2L));

        // when
        PostPageResponseDto result = postService.getPosts(email, 0, 10, null);

        // then
        assertThat(result.getPosts()).hasSize(2);

        PostResponseDto firstResult = result.getPosts().getFirst();
        assertThat(firstResult.getProfileImageUrl())
                .isEqualTo("/team-logos/TEX_logo.svg");
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

    @Test
    @DisplayName("Search posts escapes LIKE wildcard characters")
    void getPosts_withLikeWildcards_escapesSearchKeyword() {
        // given
        String email = "viewer@test.com";
        User currentUser = createUser(99L, email, "viewer");
        PageRequest pageable = PageRequest.of(0, 10);

        given(userRepository.findByEmailAndDeletedFalse(email))
                .willReturn(Optional.of(currentUser));
        given(postRepository.searchPosts("100!%!_match!!", pageable))
                .willReturn(new PageImpl<>(List.of(), pageable, 0));

        // when
        PostPageResponseDto result = postService.getPosts(
                email,
                0,
                10,
                " 100%_match! "
        );

        // then
        assertThat(result.getPosts()).isEmpty();
        then(postRepository).should()
                .searchPosts("100!%!_match!!", pageable);
    }

    @Test
    @DisplayName("Like post returns the persisted like count")
    void likePost_returnsCountFromLikeRepository() {
        // given
        String email = "viewer@test.com";
        Long postId = 1L;
        User currentUser = createUser(99L, email, "viewer");
        User author = createUser(10L, "author@test.com", "author");
        Post post = createPost(postId, author, "Post", 0);

        given(userRepository.findByEmailAndDeletedFalse(email))
                .willReturn(Optional.of(currentUser));
        given(postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId))
                .willReturn(Optional.of(post));
        given(postLikeRepository.existsByUser_IdAndPost_Id(99L, postId))
                .willReturn(false);
        given(postLikeRepository.countByPost_Id(postId)).willReturn(2L);

        // when
        PostLikeResponseDto result = postService.likePost(email, postId);

        // then
        assertThat(result.getLikeCount()).isEqualTo(2);
        assertThat(result.isLiked()).isTrue();
        then(postRepository).should().increaseLikeCount(postId);
        then(postLikeRepository).should().countByPost_Id(postId);
    }

    @Test
    @DisplayName("Unlike post returns the persisted like count")
    void unlikePost_returnsCountFromLikeRepository() {
        // given
        String email = "viewer@test.com";
        Long postId = 1L;
        User currentUser = createUser(99L, email, "viewer");
        User author = createUser(10L, "author@test.com", "author");
        Post post = createPost(postId, author, "Post", 10);
        PostLike postLike = new PostLike();

        given(userRepository.findByEmailAndDeletedFalse(email))
                .willReturn(Optional.of(currentUser));
        given(postRepository.findByIdAndDeletedFalseAndHiddenFalse(postId))
                .willReturn(Optional.of(post));
        given(postLikeRepository.findByUser_IdAndPost_Id(99L, postId))
                .willReturn(Optional.of(postLike));
        given(postLikeRepository.countByPost_Id(postId)).willReturn(4L);

        // when
        PostLikeResponseDto result = postService.unlikePost(email, postId);

        // then
        assertThat(result.getLikeCount()).isEqualTo(4);
        assertThat(result.isLiked()).isFalse();
        then(postLikeRepository).should().delete(postLike);
        then(postRepository).should().decreaseLikeCount(postId);
        then(postLikeRepository).should().countByPost_Id(postId);
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

}

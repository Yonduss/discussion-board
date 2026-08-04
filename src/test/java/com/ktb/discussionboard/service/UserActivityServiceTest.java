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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserActivityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    private UserActivityService userActivityService;

    @BeforeEach
    void setUp() {
        userActivityService = new UserActivityService(
                userRepository,
                postRepository,
                commentRepository
        );
    }

    @Test
    @DisplayName("Get my posts returns paged post summaries with bulk comment counts")
    void getMyPosts_success() {
        // given
        String email = "test@test.com";
        User user = createUser(1L, email);
        Post post = createPost(10L, user, "My post");
        PageRequest pageable = PageRequest.of(0, 10);

        given(userRepository.findByEmailAndDeletedFalse(email))
                .willReturn(Optional.of(user));
        given(postRepository
                .findAllByUser_IdAndDeletedFalseAndHiddenFalseOrderByCreatedAtDesc(
                        1L,
                        pageable
                ))
                .willReturn(new PageImpl<>(List.of(post), pageable, 1));

        PostCommentCountProjection countProjection = mock(
                PostCommentCountProjection.class
        );
        given(countProjection.getPostId()).willReturn(10L);
        given(countProjection.getCommentCount()).willReturn(3L);
        given(commentRepository.countActiveCommentsByPostIds(List.of(10L)))
                .willReturn(List.of(countProjection));

        // when
        PageResponseDto<MyPostResponseDto> result = userActivityService
                .getMyPosts(email, 0, 10);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(10L);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("My post");
        assertThat(result.getContent().getFirst().getCommentCount()).isEqualTo(3);
        then(commentRepository).should()
                .countActiveCommentsByPostIds(List.of(10L));
    }

    @Test
    @DisplayName("Get empty my posts skips the comment count query")
    void getMyPosts_emptyPage_skipsCommentCountQuery() {
        // given
        String email = "test@test.com";
        User user = createUser(1L, email);
        PageRequest pageable = PageRequest.of(0, 10);

        given(userRepository.findByEmailAndDeletedFalse(email))
                .willReturn(Optional.of(user));
        given(postRepository
                .findAllByUser_IdAndDeletedFalseAndHiddenFalseOrderByCreatedAtDesc(
                        1L,
                        pageable
                ))
                .willReturn(new PageImpl<>(List.of(), pageable, 0));

        // when
        PageResponseDto<MyPostResponseDto> result = userActivityService
                .getMyPosts(email, 0, 10);

        // then
        assertThat(result.getContent()).isEmpty();
        then(commentRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Get my comments returns post navigation information")
    void getMyComments_success() {
        // given
        String email = "test@test.com";
        User user = createUser(1L, email);
        Post post = createPost(10L, user, "Linked post");
        Comment comment = createComment(20L, user, post, "My comment");
        PageRequest pageable = PageRequest.of(0, 10);

        given(userRepository.findByEmailAndDeletedFalse(email))
                .willReturn(Optional.of(user));
        given(commentRepository
                .findAllByUser_IdAndDeletedFalseAndPost_DeletedFalseAndPost_HiddenFalseOrderByCreatedAtDesc(
                        1L,
                        pageable
                ))
                .willReturn(new PageImpl<>(List.of(comment), pageable, 1));

        // when
        PageResponseDto<MyCommentResponseDto> result = userActivityService
                .getMyComments(email, 0, 10);

        // then
        assertThat(result.getContent()).hasSize(1);
        MyCommentResponseDto response = result.getContent().getFirst();
        assertThat(response.getId()).isEqualTo(20L);
        assertThat(response.getPostId()).isEqualTo(10L);
        assertThat(response.getPostTitle()).isEqualTo("Linked post");
        assertThat(response.getContent()).isEqualTo("My comment");
    }

    @Test
    @DisplayName("Get user activity fails when the user does not exist")
    void getMyPosts_userNotFound() {
        // given
        String email = "missing@test.com";
        given(userRepository.findByEmailAndDeletedFalse(email))
                .willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userActivityService.getMyPosts(email, 0, 10)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        then(postRepository).shouldHaveNoInteractions();
        then(commentRepository).shouldHaveNoInteractions();
    }

    private User createUser(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }

    private Post createPost(Long id, User user, String title) {
        LocalDateTime now = LocalDateTime.now();

        return new Post(
                id,
                user,
                title,
                title + " content",
                2,
                5,
                0,
                false,
                true,
                false,
                now,
                now,
                null,
                null
        );
    }

    private Comment createComment(
            Long id,
            User user,
            Post post,
            String content
    ) {
        LocalDateTime now = LocalDateTime.now();

        return new Comment(
                id,
                user,
                post,
                null,
                content,
                false,
                false,
                now,
                now,
                null
        );
    }
}

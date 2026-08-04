package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.Comment;
import com.ktb.discussionboard.domain.Post;
import com.ktb.discussionboard.domain.ProfileImageSource;
import com.ktb.discussionboard.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserActivityRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("My posts contain only the requested user's visible active posts")
    void findMyPosts_excludesOtherUsersAndUnavailablePosts() {
        User userA = persistUser("user-a@test.com", "userA");
        User userB = persistUser("user-b@test.com", "userB");

        persistPost(userA, "A visible post", false, false);
        persistPost(userA, "A deleted post", true, false);
        persistPost(userA, "A hidden post", false, true);
        persistPost(userB, "B visible post", false, false);

        flushAndClear();

        Page<Post> result = postRepository
                .findAllByUser_IdAndDeletedFalseAndHiddenFalseOrderByCreatedAtDesc(
                        userA.getId(),
                        PageRequest.of(0, 10)
                );

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(Post::getTitle)
                .containsExactly("A visible post");
    }

    @Test
    @DisplayName("My comments contain only the requested user's available comments")
    void findMyComments_excludesOtherUsersAndUnavailableComments() {
        User userA = persistUser("user-a@test.com", "userA");
        User userB = persistUser("user-b@test.com", "userB");

        Post visiblePost = persistPost(userA, "Visible post", false, false);
        Post deletedPost = persistPost(userA, "Deleted post", true, false);
        Post hiddenPost = persistPost(userA, "Hidden post", false, true);

        persistComment(userA, visiblePost, "A visible comment", false);
        persistComment(userA, visiblePost, "A deleted comment", true);
        persistComment(userA, deletedPost, "A comment on deleted post", false);
        persistComment(userA, hiddenPost, "A comment on hidden post", false);
        persistComment(userB, visiblePost, "B visible comment", false);

        flushAndClear();

        Page<Comment> result = commentRepository
                .findAllByUser_IdAndDeletedFalseAndPost_DeletedFalseAndPost_HiddenFalseOrderByCreatedAtDesc(
                        userA.getId(),
                        PageRequest.of(0, 10)
                );

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(Comment::getContent)
                .containsExactly("A visible comment");

        PersistenceUnitUtil persistenceUnitUtil = entityManager
                .getEntityManagerFactory()
                .getPersistenceUnitUtil();

        assertThat(persistenceUnitUtil.isLoaded(
                result.getContent().getFirst().getPost()
        )).isTrue();
    }

    private User persistUser(String email, String nickname) {
        LocalDateTime now = LocalDateTime.now();

        User user = new User(
                null,
                email,
                "password",
                nickname,
                null,
                null,
                ProfileImageSource.PERSONAL,
                false,
                now,
                now,
                now,
                null
        );

        entityManager.persist(user);
        return user;
    }

    private Post persistPost(
            User user,
            String title,
            boolean deleted,
            boolean hidden
    ) {
        LocalDateTime now = LocalDateTime.now();

        Post post = new Post(
                null,
                user,
                title,
                title + " content",
                0,
                0,
                0,
                deleted,
                false,
                hidden,
                now,
                now,
                deleted ? now : null,
                hidden ? now : null
        );

        entityManager.persist(post);
        return post;
    }

    private void persistComment(
            User user,
            Post post,
            String content,
            boolean deleted
    ) {
        LocalDateTime now = LocalDateTime.now();

        Comment comment = new Comment(
                null,
                user,
                post,
                null,
                content,
                deleted,
                false,
                now,
                now,
                deleted ? now : null
        );

        entityManager.persist(comment);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}

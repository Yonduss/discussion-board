package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.Post;
import com.ktb.discussionboard.domain.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        User author = persistUser();

        persistPost(author, "100% baseball", "Literal percent sign");
        persistPost(author, "1000 baseball", "No percent sign");
        persistPost(author, "wild_card", "Literal underscore");
        persistPost(author, "wildXcard", "No underscore");
        persistPost(author, "bang! mark", "Literal escape character");

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Search treats percent, underscore, and escape characters literally")
    void searchPosts_withEscapedWildcards_matchesLiteralCharacters() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Post> percentResult = postRepository.searchPosts("100!%", pageable);
        Page<Post> underscoreResult = postRepository.searchPosts("wild!_card", pageable);
        Page<Post> escapeCharacterResult = postRepository.searchPosts("bang!!", pageable);

        assertThat(percentResult.getContent())
                .extracting(Post::getTitle)
                .containsExactly("100% baseball");
        assertThat(underscoreResult.getContent())
                .extracting(Post::getTitle)
                .containsExactly("wild_card");
        assertThat(escapeCharacterResult.getContent())
                .extracting(Post::getTitle)
                .containsExactly("bang! mark");
    }

    private User persistUser() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User(
                null,
                "author@test.com",
                "password",
                "author",
                null,
                false,
                now,
                now,
                now,
                null
        );

        entityManager.persist(user);
        return user;
    }

    private void persistPost(User author, String title, String content) {
        LocalDateTime now = LocalDateTime.now();

        Post post = new Post(
                null,
                author,
                title,
                content,
                0,
                0,
                0,
                false,
                false,
                false,
                now,
                now,
                null,
                null
        );

        entityManager.persist(post);
    }
}

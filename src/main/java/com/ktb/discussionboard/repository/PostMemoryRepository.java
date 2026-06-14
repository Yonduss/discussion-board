package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.Post;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PostMemoryRepository {

    private final Map<Long, Post> posts = new HashMap<>();
    private Long sequence = 1L;

    public PostMemoryRepository() {
        Post post1 = new Post(
                sequence++,
                1L,
                "I'm chainsaw man",
                "Do you know where the Futamichi Cafe is?",
                null,
                0,
                0,
                0,
                false,
                false,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        posts.put(post1.getId(), post1);
    }

    public Post save(Post post) {
        if (post.getId() == null) {
            post.setId(sequence++);
        }

        posts.put(post.getId(), post);

        return post;
    }

    public Optional<Post> findById(Long id) {
        Post post = posts.get(id);

        if (post == null || post.isDeleted() || post.isHidden()) {
            return Optional.empty();
        }

        return Optional.of(post);
    }

    public List<Post> findAll() {

        return posts.values().stream()
                .filter(post -> !post.isDeleted())
                .filter(post -> !post.isHidden())
                .toList();
    }

    public void delete(Post post) {
        post.setDeleted(true);
    }
}
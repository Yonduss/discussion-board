package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.Comment;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@Repository
public class CommentMemoryRepository {

    private final Map<Long, Comment> comments = new HashMap<>();
    private Long sequence = 1L;

    public CommentMemoryRepository() {
        Comment comment1 = new Comment(
                sequence++,
                1L,
                2L,
                null,
                "Do you mean the cafe close to  phone booth?",
                false,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        comments.put(comment1.getId(), comment1);

        Comment comment2 = new Comment(
                sequence++,
                1L,
                1L,
                comment1.getId(),
                "Yes, that's exactly the one I'm looking for!",
                false,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        comments.put(comment2.getId(), comment2);
    }

    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(sequence++);
        }

        comments.put(comment.getId(), comment);

        return comment;
    }

    public Optional<Comment> findById(Long commentId) {
        Comment comment = comments.get(commentId);

        if (comment == null || comment.isDeleted()) {
            return Optional.empty();
        }

        return Optional.of(comment);
    }

    public List<Comment> findAllByPostId(Long postId) {

        return comments.values().stream()
                .filter(comment -> comment.getPostId().equals(postId))
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .toList();
    }

    public void delete(Comment comment) {
        comment.setDeleted(true);
    }
}
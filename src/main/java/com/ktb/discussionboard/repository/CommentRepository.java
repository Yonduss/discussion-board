package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndDeletedFalse(Long id);

    List<Comment> findAllByPostIdOrderByCreatedAtAsc(Long postId);
}
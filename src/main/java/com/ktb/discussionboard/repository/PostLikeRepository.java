package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUser_IdAndPost_Id(Long userId, Long postId);

    Optional<PostLike> findByUser_IdAndPost_Id(Long userId, Long postId);
}

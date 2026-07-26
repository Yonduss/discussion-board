package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUser_IdAndPost_Id(Long userId, Long postId);
}

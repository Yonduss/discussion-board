package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUser_IdAndPost_Id(Long userId, Long postId);

    Optional<PostLike> findByUser_IdAndPost_Id(Long userId, Long postId);

    @Query("""
        select pl.post.id
        from PostLike pl
        where pl.user.id = :userId
          and pl.post.id in :postIds
    """)
    Set<Long> findLikedPostIds(
            @Param("userId") Long userId,
            @Param("postIds") List<Long> postIds
    );
}

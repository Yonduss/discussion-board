package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.Comment;
import com.ktb.discussionboard.repository.projection.PostCommentCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndDeletedFalse(Long id);

    List<Comment> findAllByPost_IdOrderByCreatedAtAsc(Long postId);

    int countByPost_IdAndDeletedFalse(Long postId);

    @Query("""
        select c.post.id as postId,
               count(c.id) as commentCount
        from Comment c
        where c.post.id in :postIds
          and c.deleted = false
        group by c.post.id
    """)
    List<PostCommentCountProjection> countActiveCommentsByPostIds(
            @Param("postIds") List<Long> postIds
    );
}

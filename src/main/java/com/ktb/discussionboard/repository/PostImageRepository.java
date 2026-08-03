package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.PostImage;
import com.ktb.discussionboard.repository.projection.PostImageProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findAllByPost_IdOrderBySortOrderAsc(Long postId);

    @Query("""
        select pi.post.id as postId,
               pi.imageUrl as imageUrl
        from PostImage pi
        where pi.post.id in :postIds
        order by pi.post.id asc, pi.sortOrder asc
    """)
    List<PostImageProjection> findAllProjectedByPostIds(
            @Param("postIds") List<Long> postIds
    );

    void deleteAllByPost_Id(Long postId);
}

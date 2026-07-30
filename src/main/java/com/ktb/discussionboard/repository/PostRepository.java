package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndDeletedFalseAndHiddenFalse(Long id);

    @EntityGraph(attributePaths = "user")
    Page<Post> findAllByDeletedFalseAndHiddenFalseOrderByCreatedAtDesc(Pageable pageable);

    @Modifying
    @Query("""
        update Post p
        set p.likeCount = p.likeCount + 1
        where p.id = :postId
    """)
    void increaseLikeCount(
            @Param("postId") Long postId
    );


    @Modifying
    @Query("""
        update Post p
        set p.likeCount = p.likeCount - 1
        where p.id = :postId
          and p.likeCount > 0
    """)
    void decreaseLikeCount(
            @Param("postId") Long postId
    );

    @Query("""
    select p
    from Post p
    where p.deleted = false
      and p.hidden = false
      and (
          lower(p.title) like lower(concat('%', :keyword, '%'))
          or lower(p.content) like lower(concat('%', :keyword, '%'))
      )
    order by p.createdAt desc
    """)
    @EntityGraph(attributePaths = "user")
    Page<Post> searchPosts(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}

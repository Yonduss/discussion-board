package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findAllByPostIdOrderBySortOrderAsc(Long postId);

    void deleteAllByPostId(Long postId);
}
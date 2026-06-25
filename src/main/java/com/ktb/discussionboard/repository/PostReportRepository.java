package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
}
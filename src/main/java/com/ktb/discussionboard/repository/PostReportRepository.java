package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    boolean existsByUser_IdAndPost_Id(Long userId, Long postId);
}
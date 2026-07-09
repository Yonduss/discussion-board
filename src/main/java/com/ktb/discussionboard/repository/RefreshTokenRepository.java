package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser_Id(Long userId);

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser_Id(Long userId);
}
package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.User;
import com.ktb.discussionboard.dto.*;
import com.ktb.discussionboard.exception.BusinessException;
import com.ktb.discussionboard.exception.ErrorCode;
import com.ktb.discussionboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public SignUpResponseDto signUp(SignUpRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }

        if (userRepository.existsByNicknameAndDeletedFalse(request.getNickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_EXISTS);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        User user = new User(
                null,
                request.getEmail(),
                request.getPassword(),
                request.getNickname(),
                request.getProfileImageUrl(),
                false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        User savedUser = userRepository.save(user);

        return new SignUpResponseDto(
                savedUser.getId(),
                savedUser.getEmail()
        );
    }

    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_EMAIL_OR_PASSWORD));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_OR_PASSWORD);
        }

        return new LoginResponseDto(
                user.getId(),
                user.getEmail()
        );
    }
}
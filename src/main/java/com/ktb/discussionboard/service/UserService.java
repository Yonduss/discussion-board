package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.User;
import com.ktb.discussionboard.dto.ChangePasswordRequestDto;
import com.ktb.discussionboard.dto.UpdateUserProfileRequestDto;
import com.ktb.discussionboard.dto.UserResponseDto;
import com.ktb.discussionboard.exception.BusinessException;
import com.ktb.discussionboard.exception.ErrorCode;
import com.ktb.discussionboard.repository.RefreshTokenRepository;
import com.ktb.discussionboard.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponseDto getUser(String email) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return toUserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateUserProfile(String email, UpdateUserProfileRequestDto request) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (request.getNickname() != null) {
            if (!request.getNickname().equals(user.getNickname())
                    && userRepository.existsByNicknameAndDeletedFalse(request.getNickname())) {
                throw new BusinessException(ErrorCode.NICKNAME_EXISTS);
            }

            user.setNickname(request.getNickname());
            user.setProfileUpdatedAt(LocalDateTime.now());
        }

        user.setProfileImageUrl(request.getProfileImageUrl());
        user.setProfileUpdatedAt(LocalDateTime.now());

        return toUserResponseDto(user);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequestDto request) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.CURRENT_PASSWORD_MISMATCH);
        }

        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.SAME_AS_OLD_PASSWORD);
        }

        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

        user.setPassword(encodedNewPassword);
        user.setPasswordUpdatedAt(LocalDateTime.now());

        refreshTokenRepository.deleteByUser_Id(user.getId());
    }

    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        refreshTokenRepository.deleteByUser_Id(user.getId());

        user.setDeleted(true);
        user.setEmail("deleted-user-" + user.getId() + "@deleted.email");
        user.setNickname("Unknown user " + user.getId());
        user.setProfileImageUrl(null);
        user.setDeletedAt(LocalDateTime.now());
    }

    private UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl()
        );
    }
}

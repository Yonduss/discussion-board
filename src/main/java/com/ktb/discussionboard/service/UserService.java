package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.User;
import com.ktb.discussionboard.dto.ChangePasswordRequestDto;
import com.ktb.discussionboard.dto.UpdateUserProfileRequestDto;
import com.ktb.discussionboard.dto.UserResponseDto;
import com.ktb.discussionboard.exception.BusinessException;
import com.ktb.discussionboard.exception.ErrorCode;
import com.ktb.discussionboard.repository.UserMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMemoryRepository userMemoryRepository;

    public UserResponseDto getUser(Long userId) {
        User user = userMemoryRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return toUserResponseDto(user);
    }

    public UserResponseDto updateUserProfile(Long userId, UpdateUserProfileRequestDto request) {
        User user = userMemoryRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (request.getNickname() != null) {
            if (!request.getNickname().equals(user.getNickname())
                    && userMemoryRepository.existsByNickname(request.getNickname())) {
                throw new BusinessException(ErrorCode.NICKNAME_EXISTS);
            }

            user.setNickname(request.getNickname());
        }

        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        return toUserResponseDto(user);
    }

    public void changePassword(Long userId, ChangePasswordRequestDto request) {
        User user = userMemoryRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.getPassword().equals(request.getCurrentPassword())) {
            throw new BusinessException(ErrorCode.CURRENT_PASSWORD_MISMATCH);
        }

        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        if (user.getPassword().equals(request.getNewPassword())) {
            throw new BusinessException(ErrorCode.SAME_AS_OLD_PASSWORD);
        }

        user.setPassword(request.getNewPassword());
    }

    public void deleteUser(Long userId) {
        User user = userMemoryRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.setNickname("Unknown user");
        user.setProfileImageUrl(null);
        userMemoryRepository.delete(user);
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

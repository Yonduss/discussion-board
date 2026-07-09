package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.RefreshToken;
import com.ktb.discussionboard.domain.User;
import com.ktb.discussionboard.dto.*;
import com.ktb.discussionboard.exception.BusinessException;
import com.ktb.discussionboard.exception.ErrorCode;
import com.ktb.discussionboard.repository.RefreshTokenRepository;
import com.ktb.discussionboard.repository.UserRepository;
import com.ktb.discussionboard.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.time.LocalDateTime.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public SignUpResponseDto signUp(SignUpRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }

        if (userRepository.existsByNicknameAndDeletedFalse(request.getNickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_EXISTS);
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                null,
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                request.getProfileImageUrl(),
                false,
                now(),
                now(),
                now(),
                null
        );

        User savedUser = userRepository.save(user);

        return new SignUpResponseDto(
                savedUser.getId(),
                savedUser.getEmail()
        );
    }

    public LoginResponseDto login(LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_OR_PASSWORD);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_EMAIL_OR_PASSWORD));

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = refreshTokenRepository
                .findByUser_Id(user.getId())
                .orElseGet(() -> new RefreshToken(
                        null,
                        user,
                        refreshTokenValue,
                        now().plusDays(14)
                ));

        refreshToken.updateToken(
                refreshTokenValue,
                now().plusDays(14)
        );

        refreshTokenRepository.save(refreshToken);

        return new LoginResponseDto(
                user.getId(),
                user.getEmail(),
                accessToken,
                refreshTokenValue
        );
    }

    public TokenReissueResponseDto reissue(TokenReissueRequestDto request) {
        String refreshTokenValue = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken savedRefreshToken = refreshTokenRepository
                .findByToken(refreshTokenValue)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        String email = jwtTokenProvider.getEmailFromToken(refreshTokenValue);

        if (!savedRefreshToken.getUser().getEmail().equals(email)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(email);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

        savedRefreshToken.updateToken(
                newRefreshToken,
                now().plusDays(14)
        );

        refreshTokenRepository.save(savedRefreshToken);

        return new TokenReissueResponseDto(
                newAccessToken,
                newRefreshToken
        );
    }
}
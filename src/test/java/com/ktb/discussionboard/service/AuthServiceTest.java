package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.User;
import com.ktb.discussionboard.domain.RefreshToken;
import com.ktb.discussionboard.dto.*;
import com.ktb.discussionboard.exception.BusinessException;
import com.ktb.discussionboard.exception.ErrorCode;

import com.ktb.discussionboard.repository.RefreshTokenRepository;
import com.ktb.discussionboard.repository.UserRepository;
import com.ktb.discussionboard.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import static org.mockito.Mockito.never;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                authenticationManager,
                jwtTokenProvider,
                refreshTokenRepository
        );
    }

    @Test
    @DisplayName("Sign up user success")
    void signUp_withValidInfo_success() {
        // given
        SignUpRequestDto request = org.mockito.Mockito.mock(SignUpRequestDto.class);

        given(request.getEmail()).willReturn("test@test.com");
        given(request.getPassword()).willReturn("12345678");
        given(request.getPasswordConfirm()).willReturn("12345678");
        given(request.getNickname()).willReturn("Test");
        given(request.getProfileImageUrl()).willReturn("https://cdn.pixabay.com/photo/2026/05/12/03/06/03-06-14-37_1280.jpg");

        given(userRepository.existsByEmail("test@test.com")).willReturn(false);
        given(userRepository.existsByNicknameAndDeletedFalse("Test")).willReturn(false);

        given(passwordEncoder.encode("12345678")).willReturn("encodedPassword");

        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        // when
        SignUpResponseDto response = authService.signUp(request);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        then(userRepository).should().save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@test.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getPassword()).isNotEqualTo("12345678");

        then(passwordEncoder).should().encode("12345678");
    }

    @Test
    @DisplayName("Sign up user failed due to duplicated email")
    void signUp_duplicateEmail() {
        // given
        SignUpRequestDto request = org.mockito.Mockito.mock(SignUpRequestDto.class);
        given(request.getEmail()).willReturn("test@test.com");

        given(userRepository.existsByEmail("test@test.com")).willReturn(true);

        // when & then
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> authService.signUp(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_EXISTS);
    }

    @Test
    @DisplayName("Sign up user failed due to duplicated nickname")
    void signUp_duplicateNickname() {
        // given
        SignUpRequestDto request = org.mockito.Mockito.mock(SignUpRequestDto.class);

        given(request.getEmail()).willReturn("test@test.com");
        given(request.getNickname()).willReturn("Test");

        given(userRepository.existsByEmail("test@test.com")).willReturn(false);
        given(userRepository.existsByNicknameAndDeletedFalse("Test")).willReturn(true);

        // when & then
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> authService.signUp(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NICKNAME_EXISTS);
    }

    @Test
    @DisplayName("Sign up user failed due to mismatched passwords")
    void signUp_passwordMismatch() {
        // given
        SignUpRequestDto request = org.mockito.Mockito.mock(SignUpRequestDto.class);

        given(request.getEmail()).willReturn("test@test.com");
        given(request.getPassword()).willReturn("12345678");
        given(request.getPasswordConfirm()).willReturn("87654321");
        given(request.getNickname()).willReturn("Test");

        given(userRepository.existsByEmail("test@test.com")).willReturn(false);
        given(userRepository.existsByNicknameAndDeletedFalse("Test")).willReturn(false);

        // when & then
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> authService.signUp(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_MISMATCH);
    }

    @Test
    @DisplayName("Login user success")
    void login_success() {
        // given
        LoginRequestDto request = org.mockito.Mockito.mock(LoginRequestDto.class);
        given(request.getEmail()).willReturn("test@test.com");
        given(request.getPassword()).willReturn("12345678");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");

        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);

        given(userRepository.findByEmailAndDeletedFalse("test@test.com")).willReturn(Optional.of(user));

        given(jwtTokenProvider.generateAccessToken("test@test.com")).willReturn("access-token");
        given(jwtTokenProvider.generateRefreshToken("test@test.com")).willReturn("refresh-token");

        given(refreshTokenRepository.findByUser_Id(1L)).willReturn(Optional.empty());
        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        LoginResponseDto response = authService.login(request);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@test.com");
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    @DisplayName("Login user failed due to invalid email or password")
    void login_invalidEmailOrPassword() {
        // given
        LoginRequestDto request = org.mockito.Mockito.mock(LoginRequestDto.class);

        given(request.getEmail()).willReturn("wrong@test.com");
        given(request.getPassword()).willReturn("wrongPassword");

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new AuthenticationException("Invalid email or password") {});

        // when & then
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> authService.login(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_EMAIL_OR_PASSWORD);
    }

    @Test
    @DisplayName("Login user success and saves refresh token")
    void login_success_savesRefreshToken() {
        // given
        LoginRequestDto request = Mockito.mock(LoginRequestDto.class);

        given(request.getEmail()).willReturn("test@test.com");
        given(request.getPassword()).willReturn("12345678");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");

        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);

        given(userRepository.findByEmailAndDeletedFalse("test@test.com")).willReturn(Optional.of(user));

        given(jwtTokenProvider.generateAccessToken("test@test.com")).willReturn("access-token");
        given(jwtTokenProvider.generateRefreshToken("test@test.com")).willReturn("refresh-token");

        given(refreshTokenRepository.findByUser_Id(1L)).willReturn(Optional.empty());
        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        authService.login(request);

        // then
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

        then(refreshTokenRepository).should().save(captor.capture());

        RefreshToken savedRefreshToken = captor.getValue();

        assertThat(savedRefreshToken.getUser()).isEqualTo(user);
        assertThat(savedRefreshToken.getToken()).isEqualTo("refresh-token");
        assertThat(savedRefreshToken.getExpiresAt()).isNotNull();
    }

    @Test
    @DisplayName("Reissue token success")
    void reissue_success() {
        // given
        TokenReissueRequestDto request = org.mockito.Mockito.mock(TokenReissueRequestDto.class);

        given(request.getRefreshToken()).willReturn("old-refresh-token");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        RefreshToken savedRefreshToken = new RefreshToken(
                1L,
                user,
                "old-refresh-token",
                java.time.LocalDateTime.now().plusDays(14)
        );

        given(refreshTokenRepository.findByToken("old-refresh-token")).willReturn(Optional.of(savedRefreshToken));

        given(jwtTokenProvider.validateToken("old-refresh-token")).willReturn(true);
        given(jwtTokenProvider.getEmailFromToken("old-refresh-token")).willReturn("test@test.com");
        given(jwtTokenProvider.generateAccessToken("test@test.com")).willReturn("new-access-token");
        given(jwtTokenProvider.generateRefreshToken("test@test.com")).willReturn("new-refresh-token");

        // when
        TokenReissueResponseDto response = authService.reissue(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        assertThat(savedRefreshToken.getToken()).isEqualTo("new-refresh-token");
        assertThat(savedRefreshToken.getExpiresAt()).isNotNull();
    }

    @Test
    @DisplayName("Reissue token failed due to invalid refresh token")
    void reissue_invalidRefreshToken() {
        // given
        TokenReissueRequestDto request = org.mockito.Mockito.mock(TokenReissueRequestDto.class);
        given(request.getRefreshToken()).willReturn("invalid-refresh-token");

        given(jwtTokenProvider.validateToken("invalid-refresh-token")).willReturn(false);

        // when & then
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> authService.reissue(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);

        then(refreshTokenRepository).should(never()).findByToken("invalid-refresh-token");
    }
}
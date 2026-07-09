package com.ktb.discussionboard.service;

import com.ktb.discussionboard.domain.User;
import com.ktb.discussionboard.dto.ChangePasswordRequestDto;
import com.ktb.discussionboard.dto.UpdateUserProfileRequestDto;
import com.ktb.discussionboard.dto.UserResponseDto;
import com.ktb.discussionboard.exception.BusinessException;
import com.ktb.discussionboard.exception.ErrorCode;
import com.ktb.discussionboard.repository.RefreshTokenRepository;
import com.ktb.discussionboard.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository,
                refreshTokenRepository,
                passwordEncoder
        );
    }

    @Test
    @DisplayName("Get user profile success")
    void getUserProfile_withValidInfo_success() {
        // given
        String email = "test@test.com";

        User user = Mockito.mock(User.class);

        given(userRepository.findByEmailAndDeletedFalse(email)).willReturn(Optional.of(user));

        given(user.getId()).willReturn(1L);
        given(user.getEmail()).willReturn(email);
        given(user.getNickname()).willReturn("Test");
        given(user.getProfileImageUrl())
                .willReturn("https://cdn.pixabay.com/photo/2026/05/12/03/06/03-06-14-37_1280.jpg");

        // when
        UserResponseDto result = userService.getUser(email);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getNickname()).isEqualTo("Test");
        assertThat(result.getProfileImageUrl())
                .isEqualTo("https://cdn.pixabay.com/photo/2026/05/12/03/06/03-06-14-37_1280.jpg");
    }

    @Test
    @DisplayName("Get user profile failed due to user not found")
    void getUser_userNotFound() {
        // given
        String email = "test@test.com";

        given(userRepository.findByEmailAndDeletedFalse(email)).willReturn(Optional.empty());

        // when & then
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> userService.getUser(email));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("Update user profile success")
    void updateUserProfile_withValidInfo_success() {
        // given
        String email = "test@test.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setNickname("Old nickname");
        user.setProfileImageUrl("old-profile-image-url");

        UpdateUserProfileRequestDto request = Mockito.mock(UpdateUserProfileRequestDto.class);

        given(request.getNickname()).willReturn("New nickname");
        given(request.getProfileImageUrl()).willReturn("New profile image url");

        given(userRepository.findByEmailAndDeletedFalse(email)).willReturn(Optional.of(user));
        given(userRepository.existsByNicknameAndDeletedFalse("New nickname")).willReturn(false);

        // when
        UserResponseDto response = userService.updateUserProfile(email, request);

        // then
        assertThat(response.getNickname()).isEqualTo("New nickname");
        assertThat(response.getProfileImageUrl()).isEqualTo("New profile image url");
        assertThat(user.getNickname()).isEqualTo("New nickname");
        assertThat(user.getProfileImageUrl()).isEqualTo("New profile image url");
    }

    @Test
    @DisplayName("Update user profile failed due to duplicated nickname")
    void updateUserProfile_duplicateNickname() {
        // given
        String email = "test@test.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setNickname("Old nickname");

        UpdateUserProfileRequestDto request = Mockito.mock(UpdateUserProfileRequestDto.class);

        given(request.getNickname()).willReturn("Duplicated nickname");

        given(userRepository.findByEmailAndDeletedFalse(email)).willReturn(Optional.of(user));
        given(userRepository.existsByNicknameAndDeletedFalse("Duplicated nickname")).willReturn(true);

        // when & then
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> userService.updateUserProfile(email, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NICKNAME_EXISTS);
    }

    @Test
    @DisplayName("Change password success")
    void changePassword_withValidInfo_success() {
        // given
        String email = "test@test.com";

        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedOldPassword");

        ChangePasswordRequestDto request = Mockito.mock(ChangePasswordRequestDto.class);

        given(request.getCurrentPassword()).willReturn("oldPassword");
        given(request.getNewPassword()).willReturn("newPassword");
        given(request.getNewPasswordConfirm()).willReturn("newPassword");

        given(userRepository.findByEmailAndDeletedFalse(email)).willReturn(Optional.of(user));

        given(passwordEncoder.matches("oldPassword", "encodedOldPassword")).willReturn(true);
        given(passwordEncoder.matches("newPassword", "encodedOldPassword")).willReturn(false);
        given(passwordEncoder.encode("newPassword")).willReturn("encodedNewPassword");

        // when
        userService.changePassword(email, request);

        // then
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
        assertThat(user.getPasswordUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Change password failed due to mismatched old and new passwords")
    void changePassword_currentPasswordMismatch() {
        // given
        String email = "test@test.com";

        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedOldPassword");

        ChangePasswordRequestDto request = Mockito.mock(ChangePasswordRequestDto.class);

        given(request.getCurrentPassword()).willReturn("wrongPassword");

        given(userRepository.findByEmailAndDeletedFalse(email)).willReturn(Optional.of(user));

        given(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).willReturn(false);

        // when & then
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> userService.changePassword(email, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CURRENT_PASSWORD_MISMATCH);
        assertThat(user.getPassword()).isEqualTo("encodedOldPassword");
    }

    @Test
    @DisplayName("Change password failed due to mismatched new passwords")
    void changePassword_newPasswordMismatch() {
        // given
        String email = "test@test.com";

        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedOldPassword");

        ChangePasswordRequestDto request = Mockito.mock(ChangePasswordRequestDto.class);

        given(request.getCurrentPassword()).willReturn("oldPassword");
        given(request.getNewPassword()).willReturn("newPassword");
        given(request.getNewPasswordConfirm()).willReturn("wrongNewPasswordConfirm");

        given(userRepository.findByEmailAndDeletedFalse(email)).willReturn(Optional.of(user));

        given(passwordEncoder.matches("oldPassword", "encodedOldPassword")).willReturn(true);

        // when & then
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> userService.changePassword(email, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_MISMATCH);
        assertThat(user.getPassword()).isEqualTo("encodedOldPassword");
    }

    @Test
    @DisplayName("Change password failed due to same passwords")
    void changePassword_sameAsOldPassword() {
        // given
        String email = "test@test.com";

        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedOldPassword");

        ChangePasswordRequestDto request = Mockito.mock(ChangePasswordRequestDto.class);

        given(request.getCurrentPassword()).willReturn("oldPassword");
        given(request.getNewPassword()).willReturn("oldPassword");
        given(request.getNewPasswordConfirm()).willReturn("oldPassword");

        given(userRepository.findByEmailAndDeletedFalse(email)).willReturn(Optional.of(user));

        given(passwordEncoder.matches("oldPassword", "encodedOldPassword")).willReturn(true);

        // when & then
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> userService.changePassword(email, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SAME_AS_OLD_PASSWORD);
        assertThat(user.getPassword()).isEqualTo("encodedOldPassword");
    }

    @Test
    @DisplayName("Delete user success")
    void deleteUser_success() {
        // given
        String email = "test@test.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setNickname("Test");
        user.setProfileImageUrl("https://cdn.pixabay.com/photo/2026/05/12/03/06/03-06-14-37_1280.jpg");

        given(userRepository.findByEmailAndDeletedFalse(email)).willReturn(Optional.of(user));

        // when
        userService.deleteUser(email);

        // then
        then(refreshTokenRepository).should().deleteByUser_Id(user.getId());

        assertThat(user.isDeleted()).isTrue();
        assertThat(user.getNickname()).isEqualTo("Unknown user");
        assertThat(user.getProfileImageUrl()).isNull();
        assertThat(user.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Delete user failed due to user not found")
    void deleteUser_userNotFound() {
        // given
        String email = "test@test.com";

        given(userRepository.findByEmailAndDeletedFalse(email)).willReturn(Optional.empty());

        // when & then
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> userService.deleteUser(email));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

        then(refreshTokenRepository).should(never()).deleteByUser_Id(anyLong());
    }
}
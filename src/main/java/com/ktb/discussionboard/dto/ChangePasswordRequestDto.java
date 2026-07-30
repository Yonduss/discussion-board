package com.ktb.discussionboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangePasswordRequestDto {

    @NotBlank(message = "Current password is required to change password")
    @Size(min = 8, max = 72, message = "Current password must be between 8 and 72 characters")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 72, message = "New password must be between 8 and 72 characters")
    private String newPassword;

    @NotBlank(message = "Enter new password again")
    @Size(min = 8, max = 72, message = "Password confirmation must be between 8 and 72 characters")
    private String newPasswordConfirm;
}

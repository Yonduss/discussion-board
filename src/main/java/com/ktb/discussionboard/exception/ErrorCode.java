package com.ktb.discussionboard.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EMAIL_EXISTS("Email is already existed", HttpStatus.CONFLICT),

    NICKNAME_EXISTS("Nickname is already existed", HttpStatus.CONFLICT),

    POST_ALREADY_LIKED("Post is already liked", HttpStatus.CONFLICT),

    POST_ALREADY_REPORTED("Post is already reported", HttpStatus.CONFLICT),

    PASSWORD_MISMATCH("Passwords do not match", HttpStatus.BAD_REQUEST),

    CURRENT_PASSWORD_MISMATCH("Current password does not match", HttpStatus.BAD_REQUEST),

    SAME_AS_OLD_PASSWORD("Password is same as old password", HttpStatus.BAD_REQUEST),

    VALIDATION_FAILED("Validation is failed", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED("Unauthorized access", HttpStatus.UNAUTHORIZED),

    INVALID_EMAIL_OR_PASSWORD("Invalid email or password", HttpStatus.UNAUTHORIZED),

    FORBIDDEN("Forbidden access", HttpStatus.FORBIDDEN),

    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),

    POST_NOT_FOUND("Post not found", HttpStatus.NOT_FOUND),

    COMMENT_NOT_FOUND("Comment not found", HttpStatus.NOT_FOUND),

    INTERNAL_SERVER_ERROR("Server error",  HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final HttpStatus status;
}
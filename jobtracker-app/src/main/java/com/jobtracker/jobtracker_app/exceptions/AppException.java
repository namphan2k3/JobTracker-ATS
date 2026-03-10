package com.jobtracker.jobtracker_app.exceptions;

import lombok.*;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // QUAN TRỌNG
        this.errorCode = errorCode;
    }
}

package com.jobtracker.jobtracker_app.validator.file.impl;

import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.validator.file.FileValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PdfFileValidator implements FileValidator {
    @Value("${file.max-pdf-size}")
    long maxSize;

    @Override
    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        if (file.getSize() > maxSize) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.contains("pdf")) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
    }
}

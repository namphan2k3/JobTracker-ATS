package com.jobtracker.jobtracker_app.dto.responses.attachment;

import com.jobtracker.jobtracker_app.enums.AttachmentType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttachmentCreationResponse {
    String id;
    String publicId;
    String applicationId;
    String companyId;
    String userId;
    String filename;
    String originalFilename;
    String filePath;
    Long fileSize;
    String fileType;
    AttachmentType attachmentType;
    String description;
    Boolean isPublic;
    LocalDateTime uploadedAt;
    LocalDateTime createdAt;
}

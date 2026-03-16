package com.jobtracker.jobtracker_app.dto.responses.attachment;

import com.jobtracker.jobtracker_app.enums.AttachmentType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttachmentResponse {
    String id;
    String publicId;
    String filename;
    AttachmentType attachmentType;
    Long fileSize;
    LocalDateTime uploadedAt;
}

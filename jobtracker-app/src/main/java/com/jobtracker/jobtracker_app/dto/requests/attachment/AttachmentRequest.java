package com.jobtracker.jobtracker_app.dto.requests.attachment;

import com.jobtracker.jobtracker_app.enums.AttachmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttachmentRequest {

    @NotBlank(message = "{attachment.company_id.not_blank}")
    String companyId;

    String applicationId;

    @NotBlank(message = "{attachment.user_id.not_blank}")
    String userId;

    @NotBlank(message = "{attachment.filename.not_blank}")
    @Size(max = 255, message = "{attachment.filename.size}")
    String filename;

    @NotBlank(message = "{attachment.original_filename.not_blank}")
    @Size(max = 255, message = "{attachment.original_filename.size}")
    String originalFilename;

    @NotBlank(message = "{attachment.file_path.not_blank}")
    @Size(max = 500, message = "{attachment.file_path.size}")
    String filePath;

    @NotNull(message = "{attachment.file_size.not_null}")
    Long fileSize;

    @NotBlank(message = "{attachment.file_type.not_blank}")
    @Size(max = 100, message = "{attachment.file_type.size}")
    String fileType;

    @NotNull(message = "{attachment.attachment_type.not_null}")
    AttachmentType attachmentType;

    String description;

    Boolean isPublic;

    LocalDateTime uploadedAt;
}

package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;
import com.jobtracker.jobtracker_app.enums.AttachmentType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Attachment extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "application_id")
    Application application;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    Company company;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column(nullable = false, length = 255)
    String filename;

    @Column(name = "original_filename", nullable = false, length = 255)
    String originalFilename;

    @Column(name = "file_path", nullable = false, length = 500)
    String filePath;

    @Column(name = "public_id", length = 255)
    String publicId;

    @Column(name = "file_size", nullable = false)
    Long fileSize;

    @Column(name = "file_type", nullable = false, length = 100)
    String fileType;

    @Column(name = "attachment_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    AttachmentType attachmentType;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "is_public", nullable = false)
    Boolean isPublic = false;

    @Column(name = "uploaded_at")
    LocalDateTime uploadedAt;

}





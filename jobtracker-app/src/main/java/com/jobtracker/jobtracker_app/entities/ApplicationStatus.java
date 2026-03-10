package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;
import com.jobtracker.jobtracker_app.enums.StatusType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "application_statuses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationStatus extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    Company company;

    @Column(nullable = false, length = 50)
    String name;

    @Column(name = "display_name", nullable = false, length = 100)
    String displayName;

    @Column(length = 255)
    String description;

    @Column(length = 7)
    String color = "#6B7280";

    @Enumerated(EnumType.STRING)
    @Column(name = "status_type", nullable = false, length = 30)
    StatusType statusType;

    @Column(name = "sort_order", nullable = false)
    Integer sortOrder = 0;

    @Column(name = "auto_send_email")
    Boolean autoSendEmail = false;

    @Column(name = "ask_before_send")
    Boolean askBeforeSend = false;

    @Column(name = "is_terminal")
    Boolean isTerminal = false;

    @Column(name = "is_default")
    Boolean isDefault = false;

    @Column(name = "is_active", nullable = false)
    Boolean isActive = true;
}


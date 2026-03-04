package com.jobtracker.jobtracker_app.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    Company company;

    @Column(nullable = false, unique = true, length = 255)
    String email;

    String password;

    @Column(name = "first_name", nullable = false, length = 100)
    String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    String lastName;

    @Column(length = 20)
    String phone;

    @Column(name = "avatar_url", length = 500)
    String avatarUrl;

    @Column(name = "avatar_public_id", length = 255)
    String avatarPublicId;

    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;

    @Column(name = "is_active")
    Boolean isActive = true;

    @Column(name = "email_verified")
    Boolean emailVerified = false;

    @Column(name = "is_billable")
    Boolean isBillable = true;

    @Column(name = "last_login_at")
    LocalDateTime lastLoginAt;
}

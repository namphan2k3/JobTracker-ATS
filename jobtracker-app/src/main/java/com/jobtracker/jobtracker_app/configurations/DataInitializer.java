package com.jobtracker.jobtracker_app.configurations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.entities.ApplicationStatus;
import com.jobtracker.jobtracker_app.entities.EmailTemplate;
import com.jobtracker.jobtracker_app.enums.EmailType;
import com.jobtracker.jobtracker_app.enums.SubscriptionStatus;
import com.jobtracker.jobtracker_app.enums.SystemRole;
import com.jobtracker.jobtracker_app.entities.Company;
import com.jobtracker.jobtracker_app.entities.Permission;
import com.jobtracker.jobtracker_app.entities.Role;
import com.jobtracker.jobtracker_app.entities.RolePermission;
import com.jobtracker.jobtracker_app.entities.SubscriptionPlan;
import com.jobtracker.jobtracker_app.entities.CompanySubscription;
import com.jobtracker.jobtracker_app.entities.Skill;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.enums.StatusType;
import com.jobtracker.jobtracker_app.repositories.ApplicationStatusRepository;
import com.jobtracker.jobtracker_app.repositories.CompanyRepository;
import com.jobtracker.jobtracker_app.repositories.PermissionRepository;
import com.jobtracker.jobtracker_app.repositories.RolePermissionRepository;
import com.jobtracker.jobtracker_app.repositories.RoleRepository;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.repositories.EmailTemplateRepository;
import com.jobtracker.jobtracker_app.repositories.SubscriptionPlanRepository;
import com.jobtracker.jobtracker_app.repositories.CompanySubscriptionRepository;
import com.jobtracker.jobtracker_app.repositories.SkillRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataInitializer implements CommandLineRunner {
    UserRepository userRepository;
    CompanyRepository companyRepository;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RolePermissionRepository rolePermissionRepository;
    ApplicationStatusRepository applicationStatusRepository;
    EmailTemplateRepository emailTemplateRepository;
    SubscriptionPlanRepository subscriptionPlanRepository;
    CompanySubscriptionRepository companySubscriptionRepository;
    SkillRepository skillRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Seeding initial data...");
            seedApplicationStatuses();
            seedSubscriptionPlans();
            seedSkills();
            seedEmailTemplates();
            seedAdminUser();
            log.info("✅ Default admin user created: admin@gmail.com / 123456789");
        } else {
            log.info("Database already initialized. Skipping seeding.");
        }
    }

    @Transactional
    public void seedAdminUser() {
        // System level - Global
        Role systemAdminRole = new Role();
        systemAdminRole.setName("SYSTEM_ADMIN");
        systemAdminRole.setDescription("System administrator (global)");
        systemAdminRole.setIsActive(true);
        roleRepository.save(systemAdminRole);

        // Company level - Per company
        Role adminCompanyRole = new Role();
        adminCompanyRole.setName(SystemRole.ADMIN_COMPANY.name());
        adminCompanyRole.setDescription("Company administrator (owner, self-signup)");
        adminCompanyRole.setIsActive(true);
        roleRepository.save(adminCompanyRole);

        Role recruiterRole = new Role();
        recruiterRole.setName(SystemRole.RECRUITER.name());
        recruiterRole.setDescription("Recruiter (per company)");
        recruiterRole.setIsActive(true);
        roleRepository.save(recruiterRole);

        User admin = new User();
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("123456789"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(systemAdminRole);
        admin.setEmailVerified(true);
        userRepository.save(admin);

        systemAdminRole.setCreatedBy(admin.getEmail());
        roleRepository.save(systemAdminRole);
        adminCompanyRole.setCreatedBy(admin.getEmail());
        roleRepository.save(adminCompanyRole);
        recruiterRole.setCreatedBy(admin.getEmail());
        roleRepository.save(recruiterRole);

        String adminEmail = admin.getEmail();
        List<Permission> permissions = List.of(
                createPermission("USER_READ", "user", "read", "Read user", adminEmail),
                createPermission("USER_CREATE", "user", "create", "Create user", adminEmail),
                createPermission("USER_UPDATE", "user", "update", "Update user", adminEmail),
                createPermission("USER_DELETE", "user", "delete", "Delete user", adminEmail),
                createPermission("PERMISSION_CREATE", "permission", "create", "Create permission", adminEmail),
                createPermission("PERMISSION_READ", "permission", "read", "Read permission", adminEmail),
                createPermission("PERMISSION_UPDATE", "permission", "update", "Update permission", adminEmail),
                createPermission("PERMISSION_DELETE", "permission", "delete", "Delete permission", adminEmail),
                createPermission("ROLE_CREATE", "role", "create", "Create role", adminEmail),
                createPermission("ROLE_READ", "role", "read", "Read role", adminEmail),
                createPermission("ROLE_UPDATE", "role", "update", "Update role", adminEmail),
                createPermission("ROLE_DELETE", "role", "delete", "Delete role", adminEmail),
                createPermission("JOB_READ", "job", "read", "Read job", adminEmail),
                createPermission("JOB_CREATE", "job", "create", "Create job", adminEmail),
                createPermission("JOB_UPDATE", "job", "update", "Update job", adminEmail),
                createPermission("JOB_DELETE", "job", "delete", "Delete job", adminEmail),
                createPermission("APPLICATION_READ", "application", "read", "Read application", adminEmail),
                createPermission("APPLICATION_CREATE", "application", "create", "Create application", adminEmail),
                createPermission("APPLICATION_UPDATE", "application", "update", "Update application", adminEmail),
                createPermission("APPLICATION_DELETE", "application", "delete", "Delete application", adminEmail),
                createPermission("INTERVIEW_READ", "interview", "read", "Read interview", adminEmail),
                createPermission("INTERVIEW_CREATE", "interview", "create", "Create interview", adminEmail),
                createPermission("INTERVIEW_UPDATE", "interview", "update", "Update interview", adminEmail),
                createPermission("INTERVIEW_DELETE", "interview", "delete", "Delete interview", adminEmail),
                createPermission("COMMENT_READ", "comment", "read", "Read comment", adminEmail),
                createPermission("COMMENT_CREATE", "comment", "create", "Create comment", adminEmail),
                createPermission("COMMENT_UPDATE", "comment", "update", "Update comment", adminEmail),
                createPermission("COMMENT_DELETE", "comment", "delete", "Delete comment", adminEmail),
                createPermission("ATTACHMENT_READ", "attachment", "read", "Read attachment", adminEmail),
                createPermission("ATTACHMENT_CREATE", "attachment", "create", "Create attachment", adminEmail),
                createPermission("ATTACHMENT_DELETE", "attachment", "delete", "Delete attachment", adminEmail),
                createPermission("SKILL_READ", "skill", "read", "Read skill", adminEmail),
                createPermission("SKILL_CREATE", "skill", "create", "Create skill", adminEmail),
                createPermission("SKILL_UPDATE", "skill", "update", "Update skill", adminEmail),
                createPermission("SKILL_DELETE", "skill", "delete", "Delete skill", adminEmail),
                createPermission("COMPANY_READ", "company", "read", "Read company", adminEmail),
                createPermission("COMPANY_UPDATE", "company", "update", "Update company", adminEmail),
                createPermission("COMPANY_VERIFY", "company", "verify", "Verify company", adminEmail),
                createPermission("SUBSCRIPTION_READ", "subscription", "read", "Read subscription", adminEmail),
                createPermission("SUBSCRIPTION_CREATE", "subscription", "create", "Create subscription", adminEmail),
                createPermission("PAYMENT_READ", "payment", "read", "Read payment", adminEmail),
                createPermission("PAYMENT_CREATE", "payment", "create", "Create payment", adminEmail),
                createPermission("APPLICATION_STATUS_READ", "application_status", "read", "Read application status", adminEmail),
                createPermission("APPLICATION_STATUS_CREATE", "application_status", "create", "Create application status", adminEmail),
                createPermission("APPLICATION_STATUS_UPDATE", "application_status", "update", "Update application status", adminEmail),
                createPermission("APPLICATION_STATUS_DELETE", "application_status", "delete", "Delete application status", adminEmail),
                createPermission("EMAIL_TEMPLATE_READ", "email_template", "read", "Read email template", adminEmail),
                createPermission("EMAIL_TEMPLATE_CREATE", "email_template", "create", "Create email template", adminEmail),
                createPermission("EMAIL_TEMPLATE_UPDATE", "email_template", "update", "Update email template", adminEmail),
                createPermission("EMAIL_TEMPLATE_DELETE", "email_template", "delete", "Delete email template", adminEmail),
                createPermission("EMAIL_HISTORY_READ", "email_history", "read", "Read email history", adminEmail),
                createPermission("NOTIFICATION_READ", "notification", "read", "Read notification", adminEmail),
                createPermission("NOTIFICATION_UPDATE", "notification", "update", "Update notification", adminEmail),
                createPermission("NOTIFICATION_DELETE", "notification", "delete", "Delete notification", adminEmail),
                createPermission("AUDIT_LOG_READ", "audit_log", "read", "Read audit log", adminEmail));

        permissionRepository.saveAll(permissions);

        // SYSTEM_ADMIN: full permissions (global)
        List<RolePermission> systemAdminPermissions = permissions.stream()
                .map(permission -> RolePermission.builder()
                        .role(systemAdminRole)
                        .permission(permission)
                        .build())
                .toList();
        rolePermissionRepository.saveAll(systemAdminPermissions);

        // ADMIN_COMPANY: company-level permissions
        List<String> adminCompanyPermNames = List.of(
                "USER_READ", "USER_CREATE", "USER_UPDATE", "USER_DELETE", "ROLE_READ",
                "JOB_READ", "JOB_CREATE", "JOB_UPDATE", "JOB_DELETE",
                "APPLICATION_READ", "APPLICATION_CREATE", "APPLICATION_UPDATE", "APPLICATION_DELETE",
                "INTERVIEW_READ", "INTERVIEW_CREATE", "INTERVIEW_UPDATE", "INTERVIEW_DELETE",
                "COMMENT_READ", "COMMENT_CREATE", "COMMENT_UPDATE", "COMMENT_DELETE",
                "ATTACHMENT_READ", "ATTACHMENT_CREATE", "ATTACHMENT_DELETE",
                "SKILL_READ", "SKILL_CREATE", "SKILL_UPDATE", "SKILL_DELETE",
                "COMPANY_READ", "COMPANY_UPDATE",
                "SUBSCRIPTION_READ", "SUBSCRIPTION_CREATE", "PAYMENT_READ", "PAYMENT_CREATE",
                "APPLICATION_STATUS_READ", "APPLICATION_STATUS_CREATE", "APPLICATION_STATUS_UPDATE", "APPLICATION_STATUS_DELETE",
                "EMAIL_TEMPLATE_READ", "EMAIL_TEMPLATE_CREATE", "EMAIL_TEMPLATE_UPDATE", "EMAIL_TEMPLATE_DELETE",
                "EMAIL_HISTORY_READ", "NOTIFICATION_READ", "NOTIFICATION_UPDATE", "NOTIFICATION_DELETE", "AUDIT_LOG_READ");
        List<RolePermission> adminCompanyPermissions = permissions.stream()
                .filter(p -> adminCompanyPermNames.contains(p.getName()))
                .map(p -> RolePermission.builder().role(adminCompanyRole).permission(p).build())
                .toList();
        rolePermissionRepository.saveAll(adminCompanyPermissions);

        // RECRUITER: recruiter permissions
        List<String> recruiterPermNames = List.of(
                "USER_READ", "JOB_READ", "APPLICATION_READ", "APPLICATION_UPDATE",
                "INTERVIEW_READ", "INTERVIEW_CREATE", "INTERVIEW_UPDATE", "INTERVIEW_DELETE",
                "COMMENT_READ", "COMMENT_CREATE", "COMMENT_UPDATE", "COMMENT_DELETE",
                "ATTACHMENT_READ", "ATTACHMENT_CREATE", "ATTACHMENT_DELETE",
                "SKILL_READ", "NOTIFICATION_READ", "NOTIFICATION_UPDATE", "NOTIFICATION_DELETE");
        List<RolePermission> recruiterPermissions = permissions.stream()
                .filter(p -> recruiterPermNames.contains(p.getName()))
                .map(p -> RolePermission.builder().role(recruiterRole).permission(p).build())
                .toList();
        rolePermissionRepository.saveAll(recruiterPermissions);

        log.info("✅ Admin user created successfully: {}", admin.getEmail());

        // Seed unlimited subscription for system admin company if applicable
        seedSystemAdminSubscription(admin);
    }

    @Transactional
    public void seedApplicationStatuses() {
        if (applicationStatusRepository.count() == 0) {
            log.info("Seeding application statuses...");
            String systemUser = "system";

            List<ApplicationStatus> statuses = List.of(
                    createApplicationStatus("NEW", "Mới", "Ứng viên vừa nộp đơn", "#6B7280", 1,
                            StatusType.APPLIED, false, true, systemUser),
                    createApplicationStatus("SCREENING", "Sàng lọc", "Đang sàng lọc hồ sơ", "#3B82F6", 2,
                            StatusType.SCREENING, false, false, systemUser),
                    createApplicationStatus("INTERVIEWING", "Phỏng vấn", "Đang trong quá trình phỏng vấn", "#F59E0B", 3,
                            StatusType.INTERVIEW, false, false, systemUser),
                    createApplicationStatus("OFFERED", "Đã đề xuất", "Đã gửi offer cho ứng viên", "#8B5CF6", 4,
                            StatusType.OFFER, false, false, systemUser),
                    createApplicationStatus("HIRED", "Đã tuyển", "Ứng viên đã được tuyển", "#10B981", 5,
                            StatusType.HIRED, true, false, systemUser),
                    createApplicationStatus("REJECTED", "Từ chối", "Ứng viên bị từ chối", "#EF4444", 6,
                            StatusType.REJECTED, true, false, systemUser)
            );
            
            applicationStatusRepository.saveAll(statuses);
            log.info("✅ Application statuses seeded: {} statuses", statuses.size());
        }
    }

    private ApplicationStatus createApplicationStatus(
            String name,
            String displayName,
            String description,
            String color,
            Integer sortOrder,
            StatusType statusType,
            boolean isTerminal,
            boolean isDefault,
            String createdBy) {
        ApplicationStatus status = ApplicationStatus.builder()
                .name(name)
                .displayName(displayName)
                .description(description)
                .color(color)
                .sortOrder(sortOrder)
                .statusType(statusType)
                .isTerminal(isTerminal)
                .isDefault(isDefault)
                .isActive(true)
                .build();
        status.setCreatedBy(createdBy);
        return status;
    }

    @Transactional
    public void seedEmailTemplates() {
        log.info("Seeding default global email templates (bilingual EN + VI) if missing...");

        // Layout template for candidate workflow emails
        createTemplateIfNotExists(
                EmailType.CANDIDATE_WORKFLOW_LAYOUT,
                "Candidate workflow layout",
                "{{company_name}} - Candidate workflow email",
                """
                        <div style="font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; max-width: 640px; margin: 0 auto;">
                            <div style="padding: 24px; border-bottom: 1px solid #e5e7eb;">
                                <h1 style="font-size: 20px; margin: 0; color: #111827;">{{company_name}}</h1>
                            </div>
                            <div style="padding: 24px 24px 16px 24px;">
                                {{content}}
                            </div>
                            <div style="padding: 16px 24px 24px 24px; font-size: 12px; color: #6b7280; border-top: 1px solid #e5e7eb;">
                                <p style="margin: 0 0 8px 0;">
                                    You can check your application status here /
                                    Bạn có thể xem trạng thái hồ sơ tại đây:
                                </p>
                                <p style="margin: 0 0 8px 0;">
                                    <a href="{{application_link}}" style="color: #2563eb;">{{application_link}}</a>
                                </p>
                                <p style="margin: 0;">
                                    This email was sent by {{company_name}} via JobTracker ATS.
                                </p>
                            </div>
                        </div>
                        """
        );

        // User & Auth emails
        createTemplateIfNotExists(
                EmailType.USER_INVITE,
                "User invite (EN/VI)",
                "[{{company_name}}] You are invited to join JobTracker / Lời mời tham gia JobTracker của {{company_name}}",
                """
                        <p>Hi {{user_first_name}} {{user_last_name}},</p>
                        <p>You have been invited to join <strong>{{company_name}}</strong> on JobTracker.</p>
                        <p>Please click the link below to accept the invitation and set your password:</p>
                        <p><a href="{{invite_link}}">{{invite_link}}</a></p>
                        <p>If you did not expect this email, you can safely ignore it.</p>
                        <p>Best regards,<br/>{{company_name}}</p>
                        <hr/>
                        <p>Chào {{user_first_name}} {{user_last_name}},</p>
                        <p>Bạn được mời tham gia <strong>{{company_name}}</strong> trên hệ thống JobTracker.</p>
                        <p>Vui lòng nhấn vào đường dẫn dưới đây để chấp nhận lời mời và tạo mật khẩu đăng nhập:</p>
                        <p><a href="{{invite_link}}">{{invite_link}}</a></p>
                        <p>Nếu bạn không mong đợi email này, bạn có thể bỏ qua.</p>
                        <p>Trân trọng,<br/>{{company_name}}</p>
                        """
        );

        createTemplateIfNotExists(
                EmailType.USER_INVITE_RESEND,
                "User invite resend (EN/VI)",
                "[{{company_name}}] Reminder: invitation to join JobTracker / Nhắc lại lời mời tham gia JobTracker của {{company_name}}",
                """
                        <p>Hi {{user_first_name}} {{user_last_name}},</p>
                        <p>This is a reminder that you have been invited to join <strong>{{company_name}}</strong> on JobTracker.</p>
                        <p>You can accept the invitation and set your password using the link below:</p>
                        <p><a href="{{invite_link}}">{{invite_link}}</a></p>
                        <p>Best regards,<br/>{{company_name}}</p>
                        <hr/>
                        <p>Chào {{user_first_name}} {{user_last_name}},</p>
                        <p>Đây là email nhắc lại rằng bạn đã được mời tham gia <strong>{{company_name}}</strong> trên JobTracker.</p>
                        <p>Bạn có thể chấp nhận lời mời và tạo mật khẩu đăng nhập qua đường dẫn sau:</p>
                        <p><a href="{{invite_link}}">{{invite_link}}</a></p>
                        <p>Trân trọng,<br/>{{company_name}}</p>
                        """
        );

        createTemplateIfNotExists(
                EmailType.EMAIL_VERIFICATION,
                "Email verification (EN/VI)",
                "[{{company_name}}] Verify your email address / Xác thực địa chỉ email của bạn",
                """
                        <p>Hi {{user_first_name}} {{user_last_name}},</p>
                        <p>Thank you for registering with <strong>{{company_name}}</strong> on JobTracker.</p>
                        <p>Please click the link below to verify your email address:</p>
                        <p><a href="{{verification_link}}">{{verification_link}}</a></p>
                        <p>If you did not create this account, you can safely ignore this email.</p>
                        <p>Best regards,<br/>{{company_name}}</p>
                        <hr/>
                        <p>Chào {{user_first_name}} {{user_last_name}},</p>
                        <p>Cảm ơn bạn đã đăng ký tài khoản cho <strong>{{company_name}}</strong> trên JobTracker.</p>
                        <p>Vui lòng nhấn vào đường dẫn dưới đây để xác thực địa chỉ email của bạn:</p>
                        <p><a href="{{verification_link}}">{{verification_link}}</a></p>
                        <p>Nếu bạn không tạo tài khoản này, bạn có thể bỏ qua email.</p>
                        <p>Trân trọng,<br/>{{company_name}}</p>
                        """
        );

        createTemplateIfNotExists(
                EmailType.EMAIL_VERIFICATION_RESEND,
                "Email verification resend (EN/VI)",
                "[{{company_name}}] Reminder: verify your email / Nhắc lại xác thực email của bạn",
                """
                        <p>Hi {{user_first_name}} {{user_last_name}},</p>
                        <p>This is a reminder to verify your email address for your <strong>{{company_name}}</strong> account on JobTracker.</p>
                        <p>Please use the link below to complete verification:</p>
                        <p><a href="{{verification_link}}">{{verification_link}}</a></p>
                        <p>Best regards,<br/>{{company_name}}</p>
                        <hr/>
                        <p>Chào {{user_first_name}} {{user_last_name}},</p>
                        <p>Đây là email nhắc lại để bạn hoàn tất việc xác thực địa chỉ email cho tài khoản <strong>{{company_name}}</strong> trên JobTracker.</p>
                        <p>Vui lòng sử dụng đường dẫn sau để xác thực:</p>
                        <p><a href="{{verification_link}}">{{verification_link}}</a></p>
                        <p>Trân trọng,<br/>{{company_name}}</p>
                        """
        );

        createTemplateIfNotExists(
                EmailType.PASSWORD_RESET,
                "Password reset (EN/VI)",
                "[{{company_name}}] Reset your password / Đặt lại mật khẩu JobTracker",
                """
                        <p>Hi {{user_first_name}} {{user_last_name}},</p>
                        <p>We received a request to reset the password for your account at <strong>{{company_name}}</strong> on JobTracker.</p>
                        <p>If you made this request, please click the link below to set a new password:</p>
                        <p><a href="{{reset_link}}">{{reset_link}}</a></p>
                        <p>If you did not request a password reset, you can safely ignore this email.</p>
                        <p>Best regards,<br/>{{company_name}}</p>
                        <hr/>
                        <p>Chào {{user_first_name}} {{user_last_name}},</p>
                        <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn tại <strong>{{company_name}}</strong> trên JobTracker.</p>
                        <p>Nếu đây là yêu cầu từ bạn, vui lòng nhấn vào đường dẫn dưới đây để đặt mật khẩu mới:</p>
                        <p><a href="{{reset_link}}">{{reset_link}}</a></p>
                        <p>Nếu bạn không yêu cầu đặt lại mật khẩu, bạn có thể bỏ qua email này.</p>
                        <p>Trân trọng,<br/>{{company_name}}</p>
                        """
        );

        // Application workflow emails
        createTemplateIfNotExists(
                EmailType.APPLICATION_CONFIRMATION,
                "Application confirmation (EN/VI)",
                "[{{company_name}}] Application received for {{job_title}} / Xác nhận nhận hồ sơ cho vị trí {{job_title}}",
                """
                        <p>Hi {{candidate_name}},</p>
                        <p>Thank you for applying for the position <strong>{{job_title}}</strong> at <strong>{{company_name}}</strong>. We have received your application and our team will review your profile soon.</p>
                        <p>You can track the status of your application at any time using the following link:</p>
                        <p><a href="{{application_link}}">{{application_link}}</a></p>
                        <p>Best regards,<br/>{{company_name}}</p>
                        <hr/>
                        <p>Chào {{candidate_name}},</p>
                        <p>Cảm ơn bạn đã ứng tuyển vị trí <strong>{{job_title}}</strong> tại <strong>{{company_name}}</strong>. Chúng tôi đã nhận được hồ sơ của bạn và đội ngũ tuyển dụng sẽ sớm xem xét.</p>
                        <p>Bạn có thể theo dõi trạng thái hồ sơ bất cứ lúc nào qua đường dẫn sau:</p>
                        <p><a href="{{application_link}}">{{application_link}}</a></p>
                        <p>Trân trọng,<br/>{{company_name}}</p>
                        """
        );

        createTemplateIfNotExists(
                EmailType.INTERVIEW_SCHEDULED,
                "Interview scheduled (EN/VI)",
                "[{{company_name}}] Interview scheduled for {{job_title}} / Thư mời phỏng vấn vị trí {{job_title}}",
                """
                        <p>Hi {{candidate_name}},</p>
                        <p>We are pleased to invite you to an interview for the position <strong>{{job_title}}</strong> at <strong>{{company_name}}</strong>.</p>
                        <p><strong>Time:</strong> {{interview_time}}<br/>
                        <strong>Location:</strong> {{interview_location}}<br/>
                        <strong>Meeting link:</strong> <a href="{{meeting_link}}">{{meeting_link}}</a></p>
                        <p>{{custom_message}}</p>
                        <p>Best regards,<br/>{{hr_name}} - {{company_name}}</p>
                        <hr/>
                        <p>Chào {{candidate_name}},</p>
                        <p>Chúng tôi trân trọng mời bạn tham gia buổi phỏng vấn cho vị trí <strong>{{job_title}}</strong> tại <strong>{{company_name}}</strong>.</p>
                        <p><strong>Thời gian:</strong> {{interview_time}}<br/>
                        <strong>Địa điểm:</strong> {{interview_location}}<br/>
                        <strong>Link họp:</strong> <a href="{{meeting_link}}">{{meeting_link}}</a></p>
                        <p>{{custom_message}}</p>
                        <p>Trân trọng,<br/>{{hr_name}} - {{company_name}}</p>
                        """
        );

        createTemplateIfNotExists(
                EmailType.INTERVIEW_RESCHEDULED,
                "Interview rescheduled (EN/VI)",
                "[{{company_name}}] Interview rescheduled for {{job_title}} / Đổi lịch phỏng vấn vị trí {{job_title}}",
                """
                        <p>Hi {{candidate_name}},</p>
                        <p>We would like to inform you that your interview for the position <strong>{{job_title}}</strong> at <strong>{{company_name}}</strong> has been rescheduled.</p>
                        <p><strong>New time:</strong> {{interview_time}}<br/>
                        <strong>Location:</strong> {{interview_location}}<br/>
                        <strong>Meeting link:</strong> <a href="{{meeting_link}}">{{meeting_link}}</a></p>
                        <p>{{custom_message}}</p>
                        <p>Best regards,<br/>{{hr_name}} - {{company_name}}</p>
                        <hr/>
                        <p>Chào {{candidate_name}},</p>
                        <p>Chúng tôi xin thông báo buổi phỏng vấn cho vị trí <strong>{{job_title}}</strong> tại <strong>{{company_name}}</strong> đã được <strong>đổi lịch</strong>.</p>
                        <p><strong>Thời gian mới:</strong> {{interview_time}}<br/>
                        <strong>Địa điểm:</strong> {{interview_location}}<br/>
                        <strong>Link họp:</strong> <a href="{{meeting_link}}">{{meeting_link}}</a></p>
                        <p>{{custom_message}}</p>
                        <p>Trân trọng,<br/>{{hr_name}} - {{company_name}}</p>
                        """
        );

        createTemplateIfNotExists(
                EmailType.CANDIDATE_REJECTED,
                "Candidate rejected (EN/VI)",
                "[{{company_name}}] Application update for {{job_title}} / Cập nhật kết quả ứng tuyển vị trí {{job_title}}",
                """
                        <p>Hi {{candidate_name}},</p>
                        <p>Thank you for your interest in the position <strong>{{job_title}}</strong> at <strong>{{company_name}}</strong>.</p>
                        <p>After careful consideration, we regret to inform you that we will not be moving forward with your application at this time.</p>
                        <p>{{custom_message}}</p>
                        <p>We truly appreciate the time you invested and wish you all the best in your job search.</p>
                        <p>Best regards,<br/>{{hr_name}} - {{company_name}}</p>
                        <hr/>
                        <p>Chào {{candidate_name}},</p>
                        <p>Cảm ơn bạn đã quan tâm và ứng tuyển vị trí <strong>{{job_title}}</strong> tại <strong>{{company_name}}</strong>.</p>
                        <p>Sau khi xem xét kỹ lưỡng, rất tiếc chúng tôi chưa thể tiếp tục hồ sơ của bạn cho vị trí này.</p>
                        <p>{{custom_message}}</p>
                        <p>Chúng tôi trân trọng thời gian bạn đã dành cho quy trình tuyển dụng và chúc bạn sớm tìm được cơ hội phù hợp.</p>
                        <p>Trân trọng,<br/>{{hr_name}} - {{company_name}}</p>
                        """
        );

        createTemplateIfNotExists(
                EmailType.CANDIDATE_HIRED,
                "Candidate hired (EN/VI)",
                "[{{company_name}}] Congratulations on your new role: {{job_title}} / Chúc mừng trúng tuyển vị trí {{job_title}}",
                """
                        <p>Hi {{candidate_name}},</p>
                        <p>Congratulations! We are delighted to offer you the position of <strong>{{job_title}}</strong> at <strong>{{company_name}}</strong>.</p>
                        <p>{{custom_message}}</p>
                        <p>We look forward to working with you.</p>
                        <p>Best regards,<br/>{{hr_name}} - {{company_name}}</p>
                        <hr/>
                        <p>Chào {{candidate_name}},</p>
                        <p>Chúc mừng bạn! Chúng tôi rất vui được thông báo bạn đã <strong>trúng tuyển</strong> vị trí <strong>{{job_title}}</strong> tại <strong>{{company_name}}</strong>.</p>
                        <p>{{custom_message}}</p>
                        <p>Chúng tôi mong sớm được đồng hành cùng bạn trong thời gian tới.</p>
                        <p>Trân trọng,<br/>{{hr_name}} - {{company_name}}</p>
                        """
        );

        // Auto-offer email (system-generated)
        createTemplateIfNotExists(
                EmailType.OFFER_CREATED,
                "Offer created (EN/VI)",
                "[{{company_name}}] Offer created for {{job_title}} / Đã tạo offer cho vị trí {{job_title}}",
                """
                        <p>Hi {{candidate_name}},</p>
                        <p>We have created an offer for the position <strong>{{job_title}}</strong> at <strong>{{company_name}}</strong>.</p>
                        <p>Our team will reach out to you shortly with the full details.</p>
                        <p>Best regards,<br/>{{hr_name}} - {{company_name}}</p>
                        <hr/>
                        <p>Chào {{candidate_name}},</p>
                        <p>Chúng tôi đã tạo offer cho vị trí <strong>{{job_title}}</strong> tại <strong>{{company_name}}</strong>.</p>
                        <p>Đội ngũ tuyển dụng sẽ sớm liên hệ lại với bạn với đầy đủ thông tin chi tiết.</p>
                        <p>Trân trọng,<br/>{{hr_name}} - {{company_name}}</p>
                        """
        );

        // Manual offer email
        createTemplateIfNotExists(
                EmailType.MANUAL_OFFER,
                "Job offer (EN/VI)",
                "[{{company_name}}] Job offer for {{job_title}} / Thư mời làm việc vị trí {{job_title}}",
                """
                        <p>Hi {{candidate_name}},</p>
                        <p>We are pleased to extend to you an offer for the position <strong>{{job_title}}</strong> at <strong>{{company_name}}</strong>.</p>
                        <p><strong>Offer salary:</strong> {{offer_salary}}<br/>
                        <strong>Start date:</strong> {{offer_start_date}}<br/>
                        <strong>Offer valid until:</strong> {{offer_expire_date}}</p>
                        <p>{{custom_message}}</p>
                        <p>Please let us know if you have any questions regarding this offer.</p>
                        <p>Best regards,<br/>{{hr_name}} - {{company_name}}</p>
                        <hr/>
                        <p>Chào {{candidate_name}},</p>
                        <p>Chúng tôi trân trọng gửi đến bạn <strong>thư mời làm việc</strong> cho vị trí <strong>{{job_title}}</strong> tại <strong>{{company_name}}</strong>.</p>
                        <p><strong>Mức lương đề xuất:</strong> {{offer_salary}}<br/>
                        <strong>Ngày bắt đầu dự kiến:</strong> {{offer_start_date}}<br/>
                        <strong>Thời hạn phản hồi offer:</strong> {{offer_expire_date}}</p>
                        <p>{{custom_message}}</p>
                        <p>Nếu bạn có bất kỳ câu hỏi nào về offer, vui lòng liên hệ lại với chúng tôi.</p>
                        <p>Trân trọng,<br/>{{hr_name}} - {{company_name}}</p>
                        """
        );

        log.info("✅ Default global email templates ensured for all system EmailType values");
    }

    private void createTemplateIfNotExists(EmailType type, String name, String subject, String htmlContent) {
        boolean exists = emailTemplateRepository
                .findByCodeAndCompanyIsNullAndDeletedAtIsNull(type.name())
                .isPresent();
        if (exists) {
            return;
        }

        EmailTemplate template = EmailTemplate.builder()
                .code(type.name())
                .name(name)
                .subject(subject)
                .htmlContent(htmlContent)
                .isActive(true)
                .build();

        emailTemplateRepository.save(template);
    }

    private Permission createPermission(String name, String resource, String action, String description, String createdBy) {
        Permission permission = new Permission(null, name, resource, action, description, true, new ArrayList());
        permission.setCreatedBy(createdBy);
        return permission;
    }

    @Transactional
    public void seedSubscriptionPlans() {
        if (subscriptionPlanRepository.count() > 0) {
            log.info("Subscription plans already exist. Skipping seeding.");
            return;
        }

        log.info("Seeding default subscription plans...");

        SubscriptionPlan freePlan = SubscriptionPlan.builder()
                .code("FREE")
                .name("Miễn phí")
                .price(new java.math.BigDecimal("0.00"))
                .durationDays(30)
                .maxJobs(3)
                .maxUsers(1)
                .maxApplications(200)
                .isActive(true)
                .build();

        SubscriptionPlan proPlan = SubscriptionPlan.builder()
                .code("PRO")
                .name("Pro")
                .price(new java.math.BigDecimal("299000.00"))
                .durationDays(30)
                .maxJobs(29)
                .maxUsers(10)
                .maxApplications(5000)
                .isActive(true)
                .build();

        SubscriptionPlan enterprisePlan = SubscriptionPlan.builder()
                .code("ENTERPRISE")
                .name("Enterprise")
                .price(new java.math.BigDecimal("599000.00"))
                .durationDays(30)
                .maxJobs(null) // Unlimited for this plan
                .maxUsers(null)
                .maxApplications(null)
                .isActive(true)
                .build();

        subscriptionPlanRepository.saveAll(List.of(freePlan, proPlan, enterprisePlan));
        log.info("✅ Seeded {} subscription plans", 3);
    }

    @Transactional
    public void seedSystemAdminSubscription(User systemAdmin) {
        Company company = companyRepository.findAll().stream().findFirst().orElse(null);
        if (company == null) {
            return;
        }

        boolean hasActive = companySubscriptionRepository
                .findLatestSubscription(company.getId(), SubscriptionStatus.ACTIVE)
                .isPresent();
        if (hasActive) {
            return;
        }

        SubscriptionPlan enterprisePlan = subscriptionPlanRepository
                .findByCodeIgnoreCase("ENTERPRISE")
                .orElse(null);
        if (enterprisePlan == null) {
            return;
        }

        CompanySubscription subscription = CompanySubscription.builder()
                .company(company)
                .plan(enterprisePlan)
                .startDate(java.time.LocalDateTime.now())
                .endDate(null) // Unlimited
                .status(SubscriptionStatus.ACTIVE)
                .build();

        companySubscriptionRepository.save(subscription);
        log.info("✅ Seeded unlimited ENTERPRISE subscription for system admin company: {}", company.getId());
    }

    @Transactional
    public void seedSkills() {
        log.info("Seeding default skills if missing...");

        // Helper to add a batch of skills for a category
        addSkillsIfMissing("PROGRAMMING", List.of(
                "Java", "Python", "JavaScript", "TypeScript", "C", "C++", "C#", "Go", "Rust", "Kotlin", "Swift",
                "PHP", "Ruby", "Dart", "Scala", "R", "MATLAB", "Bash", "Shell Scripting", "Groovy"
        ));

        addSkillsIfMissing("FRAMEWORK", List.of(
                "Spring", "Spring Boot", "Spring MVC", "Spring Security", "Hibernate", "JPA",
                "Django", "Flask", "FastAPI",
                "Laravel", "Symfony", "Ruby on Rails",
                "Express.js", "NestJS",
                "React", "Angular", "Vue.js", "Next.js", "Nuxt.js", "Flutter",
                "ASP.NET", "ASP.NET Core"
        ));

        addSkillsIfMissing("DATABASE", List.of(
                "MySQL", "PostgreSQL", "MongoDB", "Redis", "SQLite", "MariaDB",
                "Oracle Database", "Microsoft SQL Server", "Cassandra", "DynamoDB",
                "Firebase Realtime Database", "Firestore", "Neo4j", "ElasticSearch"
        ));

        addSkillsIfMissing("DEVOPS / CLOUD", List.of(
                "Docker", "Kubernetes",
                "AWS", "Google Cloud Platform", "Microsoft Azure",
                "Terraform", "Ansible",
                "Jenkins", "GitHub Actions", "GitLab CI", "CircleCI", "ArgoCD",
                "Prometheus", "Grafana",
                "Nginx", "Apache", "Linux",
                "CI/CD"
        ));

        addSkillsIfMissing("TOOL", List.of(
                "Git", "GitHub", "GitLab", "Bitbucket",
                "Postman", "Swagger",
                "Jira", "Confluence", "Notion", "Trello", "Slack",
                "Figma", "Adobe Photoshop", "Adobe Illustrator",
                "VS Code", "IntelliJ IDEA", "Eclipse", "Android Studio", "Xcode"
        ));

        addSkillsIfMissing("DATA / AI", List.of(
                "Machine Learning", "Deep Learning",
                "Data Analysis", "Data Visualization", "Data Mining",
                "Natural Language Processing", "Computer Vision",
                "TensorFlow", "PyTorch",
                "Pandas", "NumPy", "Scikit-learn",
                "Power BI", "Tableau",
                "Apache Spark", "Hadoop", "Kafka", "Airflow"
        ));

        addSkillsIfMissing("MARKETING / BUSINESS", List.of(
                "Digital Marketing", "SEO", "Content Marketing", "Email Marketing", "Social Media Marketing",
                "Google Analytics", "Google Ads", "Facebook Ads",
                "Market Research", "Brand Management", "Product Management",
                "Business Analysis", "Sales Strategy", "Customer Relationship Management"
        ));

        addSkillsIfMissing("LANGUAGE", List.of(
                "English", "Vietnamese", "Japanese", "Chinese", "Korean",
                "French", "German", "Spanish"
        ));

        addSkillsIfMissing("SOFT_SKILL", List.of(
                "Communication", "Teamwork", "Problem Solving", "Critical Thinking", "Time Management",
                "Leadership", "Adaptability", "Creativity", "Conflict Resolution", "Decision Making",
                "Negotiation", "Public Speaking", "Emotional Intelligence"
        ));

        log.info("✅ Default skills seeding completed");
    }

    private void addSkillsIfMissing(String category, List<String> names) {
        for (String rawName : names) {
            String name = rawName.trim();
            if (name.isEmpty()) {
                continue;
            }
            boolean exists = skillRepository.findByNameIgnoreCase(name).isPresent();
            if (exists) {
                continue;
            }

            Skill skill = Skill.builder()
                    .name(name)
                    .category(category)
                    .description(null)
                    .isActive(true)
                    .build();

            skillRepository.save(skill);
        }
    }
}

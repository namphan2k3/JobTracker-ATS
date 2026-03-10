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
        // System company
        Company systemCompany = Company.builder()
                .name("SYSTEM")
                .website("https://system.local")
                .industry("SYSTEM")
                .size("ENTERPRISE")
                .location("System")
                .description("System internal company")
                .isVerified(true)
                .isActive(true)
                .build();

        companyRepository.save(systemCompany);

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
        admin.setCompany(systemCompany);
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

        // Seed unlimited subscription for system admin
        seedSystemAdminSubscription(admin);
    }

    @Transactional
    public void seedApplicationStatuses() {
        if (applicationStatusRepository.count() == 0) {
            log.info("Seeding application statuses...");
            String systemUser = "system";

            List<ApplicationStatus> statuses = List.of(
                    // NEW: không auto gửi email; email xác nhận apply dùng workflow riêng APPLICATION_CONFIRMATION
                    createApplicationStatus("NEW", "Mới", "Ứng viên vừa nộp đơn", "#6B7280", 1,
                            StatusType.APPLIED, false, false, true, true, systemUser),
                    // SCREENING / INTERVIEWING: chuyển động nội bộ, mặc định không auto gửi và không hỏi
                    createApplicationStatus("SCREENING", "Sàng lọc", "Đang sàng lọc hồ sơ", "#3B82F6", 2,
                            StatusType.SCREENING, false, false, false, false, systemUser),
                    createApplicationStatus("INTERVIEWING", "Phỏng vấn", "Đang trong quá trình phỏng vấn", "#F59E0B", 3,
                            StatusType.INTERVIEW, false, false, false, false, systemUser),
                    // OFFERED: mặc định hỏi trước khi gửi email (askBeforeSend = true), autoSendEmail = false
                    createApplicationStatus("OFFERED", "Đã đề xuất", "Đã gửi offer cho ứng viên", "#8B5CF6", 4,
                            StatusType.OFFER, false, true, false, false, systemUser),
                    // HIRED: thường đã deal xong qua kênh khác, mặc định hỏi trước khi gửi
                    createApplicationStatus("HIRED", "Đã tuyển", "Ứng viên đã được tuyển", "#10B981", 5,
                            StatusType.HIRED, false, true, false, false, systemUser),
                    // REJECTED: quan trọng phải thông báo, mặc định hỏi trước khi gửi
                    createApplicationStatus("REJECTED", "Từ chối", "Ứng viên bị từ chối", "#EF4444", 6,
                            StatusType.REJECTED, false, true, false, false, systemUser)
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
            boolean autoSendEmail,
            boolean askBeforeSend,
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
                .autoSendEmail(autoSendEmail)
                .askBeforeSend(askBeforeSend)
                .isTerminal(isTerminal)
                .isDefault(isDefault)
                .isActive(true)
                .build();
        status.setCreatedBy(createdBy);
        return status;
    }

    @Transactional
    public void seedEmailTemplates() {
        log.info("Seeding default global email templates (Vietnamese only) if missing...");

        // Layout template for candidate workflow emails
        // {{{content}}} = unescaped HTML (Mustache triple-stash) - content phải render HTML, không escape
        // Không dùng header h1 company_name to đùng - chỉ content + footer
        createTemplateIfNotExists(
                EmailType.CANDIDATE_WORKFLOW_LAYOUT,
                "Candidate workflow layout",
                "{{company_name}} - Candidate workflow email",
                """
                        <div style="font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; margin: 0 auto;">
                            <div style="font-size: 16px;">
                                {{{content}}}
                            </div>
                            <div style="font-size: 14px; color: #6b7280; border-top: 1px solid #e5e7eb;">
                                <p style="margin: 0 0 8px 0;">
                                    Bạn có thể xem trạng thái hồ sơ tại đây:
                                </p>
                                <p style="margin: 0 0 8px 0;">
                                    <a href="{{application_link}}" style="color: #2563eb;">{{application_link}}</a>
                                </p>
                                <p style="margin: 0;">
                                    Email này được gửi bởi {{company_name}} qua JobTracker ATS.
                                </p>
                            </div>
                        </div>
                        """
        );

        // User & Auth emails (tiếng Việt)
        createTemplateIfNotExists(
                EmailType.USER_INVITE,
                "User invite (VI)",
                "[{{company_name}}] Lời mời tham gia JobTracker",
                """
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
                "User invite resend (VI)",
                "[{{company_name}}] Nhắc lại lời mời tham gia JobTracker",
                """
                        <p>Chào {{user_first_name}} {{user_last_name}},</p>
                        <p>Đây là email nhắc lại rằng bạn đã được mời tham gia <strong>{{company_name}}</strong> trên JobTracker.</p>
                        <p>Bạn có thể chấp nhận lời mời và tạo mật khẩu đăng nhập qua đường dẫn sau:</p>
                        <p><a href="{{invite_link}}">{{invite_link}}</a></p>
                        <p>Trân trọng,<br/>{{company_name}}</p>
                        """
        );

        createTemplateIfNotExists(
                EmailType.EMAIL_VERIFICATION,
                "Email verification (VI)",
                "[{{company_name}}] Xác thực địa chỉ email của bạn",
                """
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
                "Email verification resend (VI)",
                "[{{company_name}}] Nhắc lại xác thực email của bạn",
                """
                        <p>Chào {{user_first_name}} {{user_last_name}},</p>
                        <p>Đây là email nhắc lại để bạn hoàn tất việc xác thực địa chỉ email cho tài khoản <strong>{{company_name}}</strong> trên JobTracker.</p>
                        <p>Vui lòng sử dụng đường dẫn sau để xác thực:</p>
                        <p><a href="{{verification_link}}">{{verification_link}}</a></p>
                        <p>Trân trọng,<br/>{{company_name}}</p>
                        """
        );

        createTemplateIfNotExists(
                EmailType.PASSWORD_RESET,
                "Password reset (VI)",
                "[{{company_name}}] Đặt lại mật khẩu JobTracker",
                """
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
                "Application confirmation (VI)",
                "[{{company_name}}] Xác nhận nhận hồ sơ cho vị trí {{job_title}}",
                """
                        <p>Chào {{candidate_name}},</p>
                        <p>Cảm ơn bạn đã ứng tuyển vị trí <strong>{{job_title}}</strong> tại <strong>{{company_name}}</strong>. Chúng tôi đã nhận được hồ sơ của bạn và đội ngũ tuyển dụng sẽ sớm xem xét.</p>
                        <p>Bạn có thể theo dõi trạng thái hồ sơ bất cứ lúc nào qua đường dẫn sau:</p>
                        <p><a href="{{application_link}}">{{application_link}}</a></p>
                        <p>Trân trọng,<br/>{{company_name}}</p>
                        """
        );

        createTemplateIfNotExists(
                EmailType.INTERVIEW_SCHEDULED,
                "Interview scheduled (VI)",
                "[{{company_name}}] Thư mời phỏng vấn vị trí {{job_title}}",
                """
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
                "Interview rescheduled (VI)",
                "[{{company_name}}] Đổi lịch phỏng vấn vị trí {{job_title}}",
                """
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
                "Candidate rejected (VI)",
                "[{{company_name}}] Cập nhật kết quả ứng tuyển vị trí {{job_title}}",
                """
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
                "Candidate hired (VI)",
                "[{{company_name}}] Chúc mừng trúng tuyển vị trí {{job_title}}",
                """
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
                "Offer created (VI)",
                "[{{company_name}}] Đã tạo offer cho vị trí {{job_title}}",
                """
                        <p>Chào {{candidate_name}},</p>
                        <p>Chúng tôi đã tạo offer cho vị trí <strong>{{job_title}}</strong> tại <strong>{{company_name}}</strong>.</p>
                        <p>Đội ngũ tuyển dụng sẽ sớm liên hệ lại với bạn với đầy đủ thông tin chi tiết.</p>
                        <p>Trân trọng,<br/>{{hr_name}} - {{company_name}}</p>
                        """
        );

        // Manual offer email
        createTemplateIfNotExists(
                EmailType.MANUAL_OFFER,
                "Job offer (VI)",
                "[{{company_name}}] Thư mời làm việc vị trí {{job_title}}",
                """
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
                .maxJobs(null) // Unlimited plan
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

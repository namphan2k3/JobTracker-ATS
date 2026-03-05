package com.jobtracker.jobtracker_app.utils;

public class MessageKeys {
    // GENERAL
    public static final String UNCATEGORIZED_ERROR = "uncategorized_error";
    public static final String INVALID_INPUT = "invalid_input";

    // USER
    public static final String USER_NOT_EXISTED = "user.not_existed";
    public static final String USER_EXISTED = "user.existed";
    public static final String UNAUTHENTICATED = "unauthenticated";
    public static final String FORBIDDEN = "forbidden";
    public static final String INVALID_TOKEN = "invalid_token";
    public static final String INCORRECT_CURRENT_PASSWORD = "incorrect_current_password";

    // ROLE
    public static final String ROLE_NOT_EXISTED = "role_not_existed";
    public static final String ROLE_CREATE_SUCCESS = "role.create_success";
    public static final String ROLE_UPDATE_SUCCESS = "role.update_success";
    public static final String ROLE_DELETE_SUCCESS = "role.delete_success";
    public static final String ROLE_LIST_SUCCESS = "role.list_success";
    public static final String ROLE_DETAIL_SUCCESS = "role.detail_success";
    public static final String ROLE_NOT_ACTIVE = "role.not_active";


    // PERMISSION
    public static final String PERMISSION_NOT_EXISTED = "permission_not_existed";
    public static final String PERMISSION_CREATE_SUCCESS = "permission.create_success";
    public static final String PERMISSION_UPDATE_SUCCESS = "permission.update_success";
    public static final String PERMISSION_DELETE_SUCCESS = "permission.delete_success";
    public static final String PERMISSION_LIST_SUCCESS = "permission.list_success";
    public static final String PERMISSION_DETAIL_SUCCESS = "permission.detail_success";
    public static final String PERMISSION_NOT_ACTIVE = "permission.not_active";

    // ROLE PERMISSION
    public static final String ROLE_PERMISSION_EXISTED = "roles_permissions.existed";
    public static final String ROLE_PERMISSIONS_RETRIEVED_SUCCESS = "role.permissions_retrieved_success";
    public static final String ROLE_PERMISSIONS_UPDATE_SUCCESS = "role.permissions_update_success";
    public static final String ROLE_PERMISSION_ADD_SUCCESS = "role.permission_add_success";
    public static final String ROLE_PERMISSION_REMOVE_SUCCESS = "role.permission_remove_success";

    // FIELD
    public static final String FIELD_EXISTED = "field_existed";
    public static final String NAME_EXISTED = "name_existed";

    // USER ACTIONS
    public static final String USER_CREATE_SUCCESS = "user.create_success";
    public static final String USER_INVITE_SUCCESS = "user.invite_success";
    public static final String USER_INVITE_RESEND_SUCCESS = "user.invite_resend_success";
    public static final String EMPLOYEE_ADD_SUCCESS = "user.employee_add_success";
    public static final String USER_UPDATE_SUCCESS = "user.update_success";
    public static final String USER_DELETE_SUCCESS = "user.delete_success";
    public static final String USER_RESTORE_SUCCESS = "user.restore_success";
    public static final String USER_REGISTER_SUCCESS = "user.register_success";
    public static final String USER_LOGIN_SUCCESS = "user.login_success";
    public static final String USER_REFRESH_SUCCESS = "user.refresh_success";
    public static final String USER_LOGOUT_SUCCESS = "user.logout_success";
    public static final String USER_LIST_SUCCESS = "user.list_success";
    public static final String USER_DETAIL_SUCCESS = "user.detail_success";
    public static final String USER_PROFILE_SUCCESS = "user.profile_success";

    // PASSWORD
    public static final String PASSWORD_CHANGE_SUCCESS = "password.change_success";
    public static final String INVALID_VERIFICATION_TOKEN = "auth.invalid_verification_token";
    public static final String INVALID_RESET_TOKEN = "auth.invalid_reset_token";
    public static final String INVALID_INVITE_TOKEN = "auth.invalid_invite_token";
    public static final String EMAIL_ALREADY_VERIFIED = "auth.email_already_verified";
    public static final String EMAIL_NOT_VERIFIED = "auth.email_not_verified";
    public static final String EMAIL_VERIFIED_SUCCESS = "auth.email_verified_success";
    public static final String VERIFICATION_EMAIL_SENT = "auth.verification_email_sent";
    public static final String PASSWORD_RESET_EMAIL_SENT = "auth.password_reset_email_sent";
    public static final String PASSWORD_RESET_SUCCESS = "auth.password_reset_success";
    public static final String INVITE_ACCEPTED_SUCCESS = "auth.invite_accepted_success";

    // AVATAR
    public static final String USER_AVATAR_UPLOAD_SUCCESS = "user.avatar_upload_success";

    // JOB STATUS
    public static final String JOB_STATUS_NOT_EXISTED = "job_status.not_existed";
    public static final String JOB_STATUS_CREATE_SUCCESS = "job_status.create_success";
    public static final String JOB_STATUS_UPDATE_SUCCESS = "job_status.update_success";
    public static final String JOB_STATUS_DELETE_SUCCESS = "job_status.delete_success";
    public static final String JOB_STATUS_LIST_SUCCESS = "job_status.list_success";
    public static final String JOB_STATUS_DETAIL_SUCCESS = "job_status.detail_success";

    // JOB TYPE
    public static final String JOB_TYPE_NOT_EXISTED = "job_type.not_existed";
    public static final String JOB_TYPE_CREATE_SUCCESS = "job_type.create_success";
    public static final String JOB_TYPE_UPDATE_SUCCESS = "job_type.update_success";
    public static final String JOB_TYPE_DELETE_SUCCESS = "job_type.delete_success";
    public static final String JOB_TYPE_LIST_SUCCESS = "job_type.list_success";
    public static final String JOB_TYPE_DETAIL_SUCCESS = "job_type.detail_success";

    // PRIORITY
    public static final String PRIORITY_NOT_EXISTED = "priority.not_existed";
    public static final String PRIORITY_CREATE_SUCCESS = "priority.create_success";
    public static final String PRIORITY_UPDATE_SUCCESS = "priority.update_success";
    public static final String PRIORITY_DELETE_SUCCESS = "priority.delete_success";
    public static final String PRIORITY_LIST_SUCCESS = "priority.list_success";
    public static final String PRIORITY_DETAIL_SUCCESS = "priority.detail_success";

    // EXPERIENCE LEVEL
    public static final String EXPERIENCE_LEVEL_NOT_EXISTED = "experience_level.not_existed";
    public static final String EXPERIENCE_LEVEL_CREATE_SUCCESS = "experience_level.create_success";
    public static final String EXPERIENCE_LEVEL_UPDATE_SUCCESS = "experience_level.update_success";
    public static final String EXPERIENCE_LEVEL_DELETE_SUCCESS = "experience_level.delete_success";
    public static final String EXPERIENCE_LEVEL_LIST_SUCCESS = "experience_level.list_success";
    public static final String EXPERIENCE_LEVEL_DETAIL_SUCCESS = "experience_level.detail_success";

    // INTERVIEW TYPE
    public static final String INTERVIEW_TYPE_NOT_EXISTED = "interview_type.not_existed";
    public static final String INTERVIEW_TYPE_CREATE_SUCCESS = "interview_type.create_success";
    public static final String INTERVIEW_TYPE_UPDATE_SUCCESS = "interview_type.update_success";
    public static final String INTERVIEW_TYPE_DELETE_SUCCESS = "interview_type.delete_success";
    public static final String INTERVIEW_TYPE_LIST_SUCCESS = "interview_type.list_success";
    public static final String INTERVIEW_TYPE_DETAIL_SUCCESS = "interview_type.detail_success";

    // INTERVIEW STATUS
    public static final String INTERVIEW_STATUS_NOT_EXISTED = "interview_status.not_existed";
    public static final String INTERVIEW_STATUS_CREATE_SUCCESS = "interview_status.create_success";
    public static final String INTERVIEW_STATUS_UPDATE_SUCCESS = "interview_status.update_success";
    public static final String INTERVIEW_STATUS_DELETE_SUCCESS = "interview_status.delete_success";
    public static final String INTERVIEW_STATUS_LIST_SUCCESS = "interview_status.list_success";
    public static final String INTERVIEW_STATUS_DETAIL_SUCCESS = "interview_status.detail_success";

    // INTERVIEW RESULT
    public static final String INTERVIEW_RESULT_NOT_EXISTED = "interview_result.not_existed";
    public static final String INTERVIEW_RESULT_CREATE_SUCCESS = "interview_result.create_success";
    public static final String INTERVIEW_RESULT_UPDATE_SUCCESS = "interview_result.update_success";
    public static final String INTERVIEW_RESULT_DELETE_SUCCESS = "interview_result.delete_success";
    public static final String INTERVIEW_RESULT_LIST_SUCCESS = "interview_result.list_success";
    public static final String INTERVIEW_RESULT_DETAIL_SUCCESS = "interview_result.detail_success";

    // NOTIFICATION TYPE
    public static final String NOTIFICATION_TYPE_NOT_EXISTED = "notification_type.not_existed";
    public static final String NOTIFICATION_TYPE_CREATE_SUCCESS = "notification_type.create_success";
    public static final String NOTIFICATION_TYPE_UPDATE_SUCCESS = "notification_type.update_success";
    public static final String NOTIFICATION_TYPE_DELETE_SUCCESS = "notification_type.delete_success";
    public static final String NOTIFICATION_TYPE_LIST_SUCCESS = "notification_type.list_success";
    public static final String NOTIFICATION_TYPE_DETAIL_SUCCESS = "notification_type.detail_success";

    // NOTIFICATION PRIORITY
    public static final String NOTIFICATION_PRIORITY_NOT_EXISTED = "notification_priority.not_existed";
    public static final String NOTIFICATION_PRIORITY_CREATE_SUCCESS = "notification_priority.create_success";
    public static final String NOTIFICATION_PRIORITY_UPDATE_SUCCESS = "notification_priority.update_success";
    public static final String NOTIFICATION_PRIORITY_DELETE_SUCCESS = "notification_priority.delete_success";
    public static final String NOTIFICATION_PRIORITY_LIST_SUCCESS = "notification_priority.list_success";
    public static final String NOTIFICATION_PRIORITY_DETAIL_SUCCESS = "notification_priority.detail_success";

    // COMPANY
    public static final String COMPANY_NOT_EXISTED = "company.not_existed";
    public static final String COMPANY_CREATE_SUCCESS = "company.create_success";
    public static final String COMPANY_UPDATE_SUCCESS = "company.update_success";
    public static final String COMPANY_DELETE_SUCCESS = "company.delete_success";
    public static final String COMPANY_LIST_SUCCESS = "company.list_success";
    public static final String COMPANY_DETAIL_SUCCESS = "company.detail_success";
    public static final String COMPANY_SELF_SIGNUP_SUCCESS = "company.self_signup_success";

    // SKILL
    public static final String SKILL_NOT_EXISTED = "skill.not_existed";
    public static final String SKILL_CREATE_SUCCESS = "skill.create_success";
    public static final String SKILL_UPDATE_SUCCESS = "skill.update_success";
    public static final String SKILL_DELETE_SUCCESS = "skill.delete_success";
    public static final String SKILL_LIST_SUCCESS = "skill.list_success";
    public static final String SKILL_DETAIL_SUCCESS = "skill.detail_success";

    // DASHBOARD
    public static final String DASHBOARD_SUMMARY_SUCCESS = "dashboard.summary_success";

    // JOB
    public static final String JOB_NOT_EXISTED = "job.not_existed";
    public static final String JOB_CREATE_SUCCESS = "job.create_success";
    public static final String JOB_UPDATE_SUCCESS = "job.update_success";
    public static final String JOB_DELETE_SUCCESS = "job.delete_success";
    public static final String JOB_LIST_SUCCESS = "job.list_success";
    public static final String JOB_DETAIL_SUCCESS = "job.detail_success";

    // JOB SKILL
    public static final String JOB_SKILL_NOT_EXISTED = "job_skill.not_existed";
    public static final String JOB_SKILL_EXISTED = "job_skill.existed";
    public static final String JOB_SKILL_CREATE_SUCCESS = "job_skill.create_success";
    public static final String JOB_SKILL_UPDATE_SUCCESS = "job_skill.update_success";
    public static final String JOB_SKILL_DELETE_SUCCESS = "job_skill.delete_success";
    public static final String JOB_SKILL_LIST_SUCCESS = "job_skill.list_success";
    public static final String JOB_SKILL_DETAIL_SUCCESS = "job_skill.detail_success";

    // USER SKILL
    public static final String USER_SKILL_NOT_EXISTED = "user_skill.not_existed";
    public static final String USER_SKILL_CREATE_SUCCESS = "user_skill.create_success";
    public static final String USER_SKILL_UPDATE_SUCCESS = "user_skill.update_success";
    public static final String USER_SKILL_DELETE_SUCCESS = "user_skill.delete_success";
    public static final String USER_SKILL_LIST_SUCCESS = "user_skill.list_success";
    public static final String USER_SKILL_DETAIL_SUCCESS = "user_skill.detail_success";

    // INTERVIEW
    public static final String INTERVIEW_NOT_EXISTED = "interview.not_existed";
    public static final String INTERVIEW_CREATE_SUCCESS = "interview.create_success";
    public static final String INTERVIEW_UPDATE_SUCCESS = "interview.update_success";
    public static final String INTERVIEW_DELETE_SUCCESS = "interview.delete_success";
    public static final String INTERVIEW_LIST_SUCCESS = "interview.list_success";
    public static final String INTERVIEW_DETAIL_SUCCESS = "interview.detail_success";
    public static final String INTERVIEW_CANCEL_SUCCESS = "interview.cancel_success";
    public static final String SCHEDULE_CONFLICT = "interview.schedule_conflict";

    // APPLICATION
    public static final String APPLICATION_NOT_EXISTED = "application.not_existed";
    public static final String APPLICATION_CREATE_SUCCESS = "application.create_success";
    public static final String APPLICATION_UPDATE_SUCCESS = "application.update_success";
    public static final String APPLICATION_DELETE_SUCCESS = "application.delete_success";
    public static final String APPLICATION_LIST_SUCCESS = "application.list_success";
    public static final String APPLICATION_DETAIL_SUCCESS = "application.detail_success";
    public static final String APPLICATION_ASSIGN_SUCCESS = "application.assign_success";
    public static final String APPLICATION_STATUS_HISTORY_SUCCESS = "application.status_history_success";

    // APPLICATION STATUS WORKFLOW
    public static final String DEFAULT_STATUS_NOT_CONFIGURED = "application_status.default_not_configured";

    // COMMENT
    public static final String COMMENT_NOT_EXISTED = "comment.not_existed";
    public static final String COMMENT_LIST_SUCCESS = "comment.list_success";
    public static final String COMMENT_CREATE_SUCCESS = "comment.create_success";
    public static final String COMMENT_UPDATE_SUCCESS = "comment.update_success";
    public static final String COMMENT_DELETE_SUCCESS = "comment.delete_success";

    // APPLICATION STATUS
    public static final String APPLICATION_STATUS_NOT_EXISTED = "application_status.not_existed";
    public static final String APPLICATION_STATUS_IS_TERMINAL = "application_status.is_terminal";
    public static final String APPLICATION_STATUS_SAME = "application_status.same";
    public static final String APPLICATION_STATUS_INVALID_TRANSITION = "application_status.invalid_transition";
    public static final String APPLICATION_STATUS_CREATE_SUCCESS = "application_status.create_success";
    public static final String APPLICATION_STATUS_UPDATE_SUCCESS = "application_status.update_success";
    public static final String APPLICATION_STATUS_DELETE_SUCCESS = "application_status.delete_success";
    public static final String APPLICATION_STATUS_LIST_SUCCESS = "application_status.list_success";
    public static final String APPLICATION_STATUS_DETAIL_SUCCESS = "application_status.detail_success";

    // JOB RESUME
    public static final String JOB_RESUME_NOT_EXISTED = "job_resume.not_existed";
    public static final String JOB_RESUME_CREATE_SUCCESS = "job_resume.create_success";
    public static final String JOB_RESUME_UPDATE_SUCCESS = "job_resume.update_success";
    public static final String JOB_RESUME_DELETE_SUCCESS = "job_resume.delete_success";
    public static final String JOB_RESUME_LIST_SUCCESS = "job_resume.list_success";
    public static final String JOB_RESUME_DETAIL_SUCCESS = "job_resume.detail_success";

    // RESUME
    public static final String RESUME_NOT_EXISTED = "resume.not_existed";
    public static final String RESUME_CREATE_SUCCESS = "resume.create_success";
    public static final String RESUME_UPDATE_SUCCESS = "resume.update_success";
    public static final String RESUME_DELETE_SUCCESS = "resume.delete_success";
    public static final String RESUME_LIST_SUCCESS = "resume.list_success";
    public static final String RESUME_DETAIL_SUCCESS = "resume.detail_success";

    // ATTACHMENT
    public static final String ATTACHMENT_NOT_EXISTED = "attachment.not_existed";
    public static final String ATTACHMENT_CREATE_SUCCESS = "attachment.create_success";
    public static final String ATTACHMENT_UPDATE_SUCCESS = "attachment.update_success";
    public static final String ATTACHMENT_DELETE_SUCCESS = "attachment.delete_success";
    public static final String ATTACHMENT_LIST_SUCCESS = "attachment.list_success";
    public static final String ATTACHMENT_DETAIL_SUCCESS = "attachment.detail_success";

    // NOTIFICATION
    public static final String NOTIFICATION_NOT_EXISTED = "notification.not_existed";
    public static final String NOTIFICATION_CREATE_SUCCESS = "notification.create_success";
    public static final String NOTIFICATION_UPDATE_SUCCESS = "notification.update_success";
    public static final String NOTIFICATION_DELETE_SUCCESS = "notification.delete_success";
    public static final String NOTIFICATION_LIST_SUCCESS = "notification.list_success";
    public static final String NOTIFICATION_DETAIL_SUCCESS = "notification.detail_success";
    public static final String NOTIFICATION_MARK_READ_SUCCESS = "notification.mark_read_success";
    public static final String NOTIFICATION_MARK_ALL_READ_SUCCESS = "notification.mark_all_read_success";

    // USER SESSION
    public static final String USER_SESSION_NOT_EXISTED = "user_session.not_existed";
    public static final String USER_SESSION_CREATE_SUCCESS = "user_session.create_success";
    public static final String USER_SESSION_UPDATE_SUCCESS = "user_session.update_success";
    public static final String USER_SESSION_DELETE_SUCCESS = "user_session.delete_success";
    public static final String USER_SESSION_LIST_SUCCESS = "user_session.list_success";
    public static final String USER_SESSION_DETAIL_SUCCESS = "user_session.detail_success";

    // AUDIT LOG
    public static final String AUDIT_LOG_NOT_EXISTED = "audit_log.not_existed";
    public static final String AUDIT_LOG_CREATE_SUCCESS = "audit_log.create_success";
    public static final String AUDIT_LOG_UPDATE_SUCCESS = "audit_log.update_success";
    public static final String AUDIT_LOG_DELETE_SUCCESS = "audit_log.delete_success";
    public static final String AUDIT_LOG_LIST_SUCCESS = "audit_log.list_success";
    public static final String AUDIT_LOG_DETAIL_SUCCESS = "audit_log.detail_success";

    // SUBSCRIPTION PLAN
    public static final String SUBSCRIPTION_PLAN_NOT_EXISTED = "subscription_plan.not_existed";
    public static final String SUBSCRIPTION_PLAN_CREATE_SUCCESS = "subscription_plan.create_success";
    public static final String SUBSCRIPTION_PLAN_UPDATE_SUCCESS = "subscription_plan.update_success";
    public static final String SUBSCRIPTION_PLAN_DELETE_SUCCESS = "subscription_plan.delete_success";
    public static final String SUBSCRIPTION_PLAN_LIST_SUCCESS = "subscription_plan.list_success";
    public static final String SUBSCRIPTION_PLAN_DETAIL_SUCCESS = "subscription_plan.detail_success";

    // COMPANY SUBSCRIPTION
    public static final String COMPANY_SUBSCRIPTION_NOT_EXISTED = "company_subscription.not_existed";
    public static final String PLAN_LIMIT_APPLICATIONS_EXCEEDED = "plan_limit.applications_exceeded";
    public static final String PLAN_LIMIT_JOBS_EXCEEDED = "plan_limit.jobs_exceeded";
    public static final String PLAN_LIMIT_USERS_EXCEEDED = "plan_limit.users_exceeded";
    public static final String COMPANY_SUBSCRIPTION_CREATE_SUCCESS = "company_subscription.create_success";
    public static final String COMPANY_SUBSCRIPTION_UPDATE_SUCCESS = "company_subscription.update_success";
    public static final String COMPANY_SUBSCRIPTION_LIST_SUCCESS = "company_subscription.list_success";
    public static final String COMPANY_SUBSCRIPTION_DETAIL_SUCCESS = "company_subscription.detail_success";

    // PAYMENT
    public static final String PAYMENT_NOT_EXISTED = "payment.not_existed";
    public static final String PAYMENT_CREATE_SUCCESS = "payment.create_success";
    public static final String PAYMENT_LIST_SUCCESS = "payment.list_success";
    public static final String PAYMENT_DETAIL_SUCCESS = "payment.detail_success";

    // FILE
    public static final String UPLOAD_FILE_FAILED = "file.upload_failed";
    public static final String DELETE_FILE_FAILED = "file.delete_failed";
    public static final String FILE_EMPTY = "file.empty";
    public static final String FILE_TOO_LARGE = "file.too_large";
    public static final String INVALID_FILE_TYPE = "file.invalid_type";
    public static final String UPLOAD_NOT_ALLOWED = "upload.not_allowed";

    // EMAIL
    public static final String CANNOT_SEND_EMAIL = "email.cannot_send";
    public static final String EMAIL_TEMPLATE_UNKNOWN_VARIABLE = "email_template.unknown_variable";
    public static final String EMAIL_TEMPLATE_NOT_FOUND = "email_template.not_found";
    public static final String EMAIL_TEMPLATE_EXISTED = "email_template.existed";
    public static final String EMAIL_OUTBOX_NOT_FOUND = "email_outbox.not_found";
    public static final String EMAIL_CANNOT_RESEND = "email_outbox.cannot_resend";

    // EMAIL TEMPLATE
    public static final String EMAIL_TEMPLATE_CREATE_SUCCESS = "email_template.create_success";
    public static final String EMAIL_TEMPLATE_UPDATE_SUCCESS = "email_template.update_success";
    public static final String EMAIL_TEMPLATE_DELETE_SUCCESS = "email_template.delete_success";
    public static final String EMAIL_TEMPLATE_LIST_SUCCESS = "email_template.list_success";
    public static final String EMAIL_TEMPLATE_DETAIL_SUCCESS = "email_template.detail_success";
    public static final String EMAIL_TEMPLATE_PREVIEW_SUCCESS = "email_template.preview_success";
    public static final String EMAIL_TEMPLATE_SEND_TEST_SUCCESS = "email_template.send_test_success";

    // EMAIL HISTORY
    public static final String EMAIL_HISTORY_LIST_SUCCESS = "email_history.list_success";
    public static final String EMAIL_HISTORY_DETAIL_SUCCESS = "email_history.detail_success";
    public static final String EMAIL_HISTORY_RESEND_SUCCESS = "email_history.resend_success";

}

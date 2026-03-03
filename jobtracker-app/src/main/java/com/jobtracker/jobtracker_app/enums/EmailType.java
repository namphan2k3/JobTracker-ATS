package com.jobtracker.jobtracker_app.enums;

public enum EmailType {
    // User & Auth
    USER_INVITE,
    USER_INVITE_RESEND,
    EMAIL_VERIFICATION,
    EMAIL_VERIFICATION_RESEND,
    PASSWORD_RESET,

    // Application Workflow
    APPLICATION_CONFIRMATION,
    INTERVIEW_SCHEDULED,
    INTERVIEW_RESCHEDULED,
    OFFER_CREATED,
    CANDIDATE_HIRED,
    CANDIDATE_REJECTED,
    MANUAL_OFFER,

    // Layout Application Workflow
    CANDIDATE_WORKFLOW_LAYOUT
}


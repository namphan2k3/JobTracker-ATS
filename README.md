# JobTracker ATS – SME Applicant Tracking System

## Overview

JobTracker ATS is an Applicant Tracking System designed for small and medium enterprises (SMEs). It helps HR and recruiters manage the full hiring lifecycle: job postings, candidate applications, screening, interviews, and hiring.

### Key Goals

- **Job posting management**: Create, publish, and manage job listings
- **Application pipeline**: Track candidates through stages (APPLIED → SCREENING → INTERVIEW → OFFER → HIRED/REJECTED)
- **Interview scheduling**: Schedule and manage multiple interview rounds
- **Team collaboration**: Internal comments and discussions on candidates
- **CV scoring**: Automatic match score (0–100) between CV and job skills
- **Multi-tenant**: Multiple companies on one system with shared infrastructure

### Architecture

- **Pattern**: Monolithic Architecture, Multi-tenant
- **Backend**: Spring Boot 3 + Java 21
- **Database**: MySQL 8.0 with Liquibase migrations
- **Authentication**: JWT (access + refresh tokens)
- **File storage**: Cloudinary API
- **Email**: Brevo API (transactional via email queue)
- **Payments**: VNPay (subscription flow)

---

## Quick Start

### Prerequisites

- Java 21+
- MySQL 8.0+
- Redis
- Cloudinary account (file storage)
- Brevo account (email)

### Installation

```bash
# Clone repository
git clone https://github.com/your-username/jobtracker.git
cd jobtracker

# Configure environment variables
cp .env.example .env
# Edit .env with your credentials

cd jobtracker-app
./mvnw spring-boot:run
```

### Environment Variables

Configure in `application.yaml` or environment variables:

- `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`
- `BREVO_API_KEY`
- `SPRING_DATASOURCE_*` (MySQL)
- `SPRING_DATA_REDIS_*` (Redis for refresh tokens)
- `JWT_SECRET`, `JWT_ACCESS_EXPIRATION`, `JWT_REFRESH_EXPIRATION`

---

## Documentation

- [API Documentation](./docs/API.md) – API endpoints
- [API Security](./docs/API_SECURITY.md) – Public/private endpoints, permissions, roles
- [Database Schema](./docs/DATABASE.md) – Multi-tenant database design
- [Architecture Guide](./docs/ARCHITECTURE.md) – System architecture
- [Business Flows](./docs/BUSINESS_FLOWS.md) – Business logic and flows
- [Technical Specifications](./docs/TECHNICAL_SPECS.md) – Technical details

---

## Tech Stack

### Backend

- **Framework**: Spring Boot 3.2+
- **Language**: Java 21
- **ORM**: Spring Data JPA + Hibernate 6
- **Database**: MySQL 8.0 with Liquibase
- **Security**: Spring Security 6 + JWT (access + refresh tokens)
- **Validation**: Jakarta Validation
- **Email**: Brevo API via EmailOutbox queue + EmailScheduler
- **File storage**: Cloudinary API
- **Payments**: VNPay
- **Scheduling**: Spring @Scheduled (EmailScheduler, PlanLimitScheduler)
- **Cache**: Redis (refresh tokens, permission cache)

---

## Features

### Authentication & Authorization

- **Company self-signup**: Register company + admin user
- **Email verification**: Required before login
- **Login / Logout**: JWT access + refresh tokens
- **Token refresh**: Refresh token rotation via HTTP-only cookie
- **Forgot / Reset password**: Token-based flow
- **Invite-based user creation**: Admin invites HR → user sets password via link
- **Add employee**: Add employees without app access (no billing)
- **Multi-tenant**: Data isolation by `company_id`
- **RBAC**: SYSTEM_ADMIN, ADMIN_COMPANY, RECRUITER
- **User sessions**: List and manage active sessions

### Job Posting Management

- **CRUD jobs**: Create, read, update, delete
- **Job status workflow**: DRAFT → PUBLISHED → PAUSED / CLOSED / FILLED
- **Job skills**: Add/update/remove skills per job (required/optional)
- **Job search**: Filter by status, remote, search text
- **Status transitions**: Publish, pause, close, mark filled

### Application Management (Core ATS)

- **Public apply**: Candidates apply without login (`POST /public/jobs/{jobId}/apply`)
- **CV upload**: PDF required, stored on Cloudinary
- **CV scoring**: Automatic match score (0–100) vs job skills
- **Application pipeline**: APPLIED → SCREENING → INTERVIEW → OFFER → HIRED / REJECTED
- **Status history**: Audit trail for status changes
- **Assign to recruiter**: Assign applications to HR
- **Filter**: By status, job, assignee, match score range
- **HR manual entry**: Create application without CV, upload later
- **Candidate tracking**: Public status check via token (`GET /public/applications/{token}/status`)

### CV Scoring

- **Automatic scoring**: Parse PDF → extract text → match job skills
- **Required vs optional**: 70% required, 30% optional weight
- **Score breakdown**: Matched/missing skills, explainable for HR
- **Filter**: `minMatchScore`, `maxMatchScore`. Sort by match score

### Attachments

- **CV upload**: HR uploads CV for application (triggers re-scoring)
- **Additional files**: Candidate uploads certificates, portfolio (when allowed)
- **Attachment types**: RESUME, CERTIFICATE, PORTFOLIO, OTHER

### Interview Management

- **Create interview**: Link to application, round number, type
- **Interview types**: Phone, Video, In-person, Technical
- **Multiple interviewers**: Primary + optional interviewers
- **Schedule conflict check**: Prevent scheduling conflicts
- **Update / Cancel**: Reschedule or cancel interviews

### Comments

- **Internal comments**: Comments on applications (HR only)
- **Internal vs external**: Internal flag for visibility

### Notifications

- **In-app notifications**: Create, list, mark read
- **Application received**: Email to HR when candidate applies
- **Status change**: Notifications on status updates

### Email

- **Email queue**: EmailOutbox + EmailScheduler (Brevo)
- **Templates**: Customizable email templates stored in DB
- **Variables**: Dynamic variables (company name, user name, links, etc.)
- **Email history**: Resend and track sent emails

### Subscription & Billing

- **Subscription plans**: FREE, BASIC, PRO, ENTERPRISE (configurable)
- **Plan limits**: `maxJobs`, `maxUsers`, `maxApplications`
- **Plan limit enforcement**: Block create job/application/invite when limit exceeded
- **PlanLimitScheduler**: Expire subscriptions when `end_date` passed
- **Company subscription**: CRUD, active subscription lookup
- **Payments**: VNPay integration for subscription payment

### User Management

- **Invite user**: Admin invites HR (billable)
- **Add employee**: Add employees (non-billable, no app access)
- **Resend invite**: Resend invitation email
- **User CRUD**: List, update, delete, restore
- **Profile**: Update profile, change password, upload avatar

### Company Management

- **Company CRUD**: Create, read, update, delete
- **Company verification**: Verify company status

### Skills & Roles

- **Skills**: CRUD, active/inactive
- **Roles**: CRUD with permissions
- **Permissions**: Role-permission management

### Audit & Logging

- **Audit logs**: Track entity changes

---

## Multi-Tenant Architecture

- **Data isolation**: All queries filtered by `company_id`
- **Roles**: SYSTEM_ADMIN, ADMIN_COMPANY, RECRUITER
- **Subscription**: `subscription_plans` + `company_subscriptions` for billing

---

## Database Migration

Liquibase runs migrations on application startup:

```bash
./mvnw liquibase:update
```

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

Made with ❤️ by the JobTracker ATS Team

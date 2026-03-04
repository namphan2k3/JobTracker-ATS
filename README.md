# 🎯 JobTracker ATS - SME Applicant Tracking System

## 📋 Tổng quan dự án

JobTracker ATS là hệ thống quản lý tuyển dụng dành cho các doanh nghiệp vừa và nhỏ (SME), giúp HR và Recruiter quản lý toàn bộ quy trình tuyển dụng từ đăng tin, nhận ứng tuyển, phỏng vấn đến tuyển dụng thành công.

### 🎯 Mục tiêu chính
- **Quản lý tin tuyển dụng**: Tạo, đăng và quản lý các tin tuyển dụng
- **Quản lý ứng tuyển**: Theo dõi ứng viên qua các giai đoạn (NEW → SCREENING → INTERVIEWING → OFFERED → HIRED/REJECTED)
- **Phỏng vấn**: Lên lịch và quản lý các vòng phỏng vấn
- **Hợp tác nhóm**: Bình luận và trao đổi nội bộ về ứng viên
- **Phân tích hiệu quả**: Thống kê tỷ lệ chuyển đổi, thời gian tuyển dụng
- **Multi-tenant**: Hỗ trợ nhiều công ty trên cùng một hệ thống

### 🏗️ Kiến trúc hệ thống
- **Pattern**: Monolithic Architecture với modular design, Multi-tenant
- **Backend**: Spring Boot 3 + Java 21
- **Frontend**: React 18 + JavaScript + Create React App
- **Database**: MySQL 8.0 với Liquibase migration
- **Authentication**: OAuth2 Resource Server + OAuth2 Client (Google)
- **File Storage**: Cloudinary API
- **Email Service**: Brevo API
- **Deployment**: Docker + Docker Compose

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Node.js 18+
- MySQL 8.0+
- Docker & Docker Compose
- Cloudinary account (for file storage)
- Brevo account (for email service)
- Google OAuth2 credentials

### Installation
```bash
# Clone repository
git clone https://github.com/your-username/jobtracker.git
cd jobtracker

# Configure environment variables
cp .env.example .env
# Edit .env with your Cloudinary, Brevo, and OAuth2 credentials

# Start with Docker Compose
docker-compose up -d

# Or run locally
# Backend
cd jobtracker-app
./mvnw spring-boot:run

# Frontend
cd jobtracker-frontend
npm install
npm start
```

### Environment Variables
Xem chi tiết trong [Deployment Guide](./docs/DEPLOYMENT.md) để cấu hình:
- `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`
- `BREVO_API_KEY`
- `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`
- Database credentials

## 📚 Documentation

- [API Documentation](./docs/API.md) - Chi tiết các API endpoints
- [Database Schema](./docs/DATABASE.md) - Thiết kế database (Multi-tenant ATS)
- [Architecture Guide](./docs/ARCHITECTURE.md) - Kiến trúc hệ thống
- [Technical Specifications](./docs/TECHNICAL_SPECS.md) - Thông số kỹ thuật chi tiết
- [Deployment Guide](./docs/DEPLOYMENT.md) - Hướng dẫn deploy

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.2+
- **Language**: Java 21
- **ORM**: Spring Data JPA + Hibernate 6
- **Database**: MySQL 8.0 với Liquibase migration
- **Security**: Spring Security 6 + OAuth2 Resource Server (Multi-tenant RBAC)
- **Validation**: Jakarta Validation
- **Email**: Brevo API
- **File Storage**: Cloudinary API
- **Scheduling**: Spring @Scheduled
- **WebSocket**: Spring WebSocket + STOMP
- **Documentation**: SpringDoc OpenAPI 3

### Frontend
- **Framework**: React 18 + JavaScript
- **Build Tool**: Create React App (CRA)
- **State Management**: Redux Toolkit
- **Routing**: React Router v6
- **UI Library**: TailwindCSS + shadcn/ui
- **Forms**: React Hook Form + Yup
- **HTTP Client**: Axios
- **Charts**: Recharts
- **Notifications**: React Toastify

### DevOps & Tools
- **Containerization**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **Code Quality**: SonarQube
- **Monitoring**: Spring Boot Actuator
- **Logging**: SLF4J + Logback

## 📊 Features

### 🔐 Authentication & Authorization
- [x] User registration/login
- [x] Google OAuth2 login
- [x] OAuth2 token authentication
- [x] Multi-tenant data isolation
- [x] Role-based access control (RBAC)
- [x] Company-based user management

### 💼 Job Posting Management
- [x] CRUD operations for job postings
- [x] Job status workflow (DRAFT → PUBLISHED → CLOSED)
- [x] Skills tagging
- [x] Company information management
- [x] Job analytics (views, applications count)

### 📝 Application Management (Core ATS)
- [x] Application workflow (NEW → SCREENING → INTERVIEWING → OFFERED → HIRED/REJECTED)
- [x] Candidate information management
- [x] Application status history tracking
- [x] Assignment to recruiters
- [x] Rating and notes
- [x] File attachments (CV, certificates)

### 💬 Team Collaboration
- [x] Internal comments on applications
- [x] Team discussion threads
- [x] Activity tracking

### 🎤 Interview Management
- [x] Schedule interviews
- [x] Multiple interview rounds
- [x] Interview types (Phone, Video, In-person, Technical)
- [x] Interview feedback and results
- [x] Meeting links integration

### 📈 Analytics & Dashboard
- [x] Hiring funnel metrics
- [x] Time-to-hire analysis
- [x] Application statistics
- [x] Job performance metrics
- [x] Team productivity tracking

### 🔔 Notifications
- [x] Email notifications via Brevo
- [x] Real-time in-app notifications
- [x] Application received alerts
- [x] Interview reminders
- [x] Deadline alerts

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🏢 Multi-Tenant Architecture

JobTracker ATS được thiết kế với kiến trúc multi-tenant, cho phép nhiều công ty sử dụng cùng một hệ thống với dữ liệu hoàn toàn tách biệt:
- Mỗi công ty có dữ liệu riêng biệt (jobs, applications, users)
- Data isolation thông qua `company_id`
- Role-based access control (SYSTEM_ADMIN, ADMIN_COMPANY, RECRUITER) – Global RBAC
- Subscription được mô hình hóa bằng lookup table `subscription_plans` và bảng history `company_subscriptions` (FREE, BASIC, PRO, ENTERPRISE, ... thay đổi linh hoạt theo business)

## 🔄 Database Migration

Hệ thống sử dụng Liquibase để quản lý database migrations:
```bash
# Migrations được tự động chạy khi ứng dụng khởi động
# Hoặc chạy thủ công:
./mvnw liquibase:update
```

## 👥 Team

- **Backend Developer**: [Your Name]
- **Frontend Developer**: [Your Name]
- **DevOps Engineer**: [Your Name]

## 📞 Support

For support, email support@jobtracker.com or join our Slack channel.

---

Made with ❤️ by the JobTracker ATS Team

# 🌉 TalentBridge - Modern Job Portal

TalentBridge is a premium, full-stack job portal designed to connect students with top-tier employers. Built with Spring Boot 3 and Thymeleaf, it features a modern SaaS-style interface, robust authentication, and an automated data seeding mechanism for immediate use.

## 🚀 Live Features

### 👤 For Students
- **Smart Job Discovery**: Browse jobs with advanced sidebar filters for Salary, Location, and Job Type.
- **Interactive Job Cards**: Quick view of skills, experience level, and salary ranges.
- **Application Tracking**: Manage and monitor the status of your job applications.
- **Saved Jobs**: Bookmark interesting roles for later application.
- **Profile Management**: Professional resume upload and profile customization.

### 🏢 For Employers
- **Job Management**: Post, edit, and deactivate job listings.
- **Applicant Tracking**: Review applications and manage candidate status (Applied, Shortlisted, Rejected).
- **Company Branding**: Automatic visual branding for your company on job cards.

### 🛡️ For Administrators
- **Platform Management**: Monitor all users and job postings.
- **Proxy Posting**: Post jobs on behalf of any employer on the platform.
- **Security**: Full control over user status and content moderation.

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.2.5, Spring Security 6, Spring Data JPA
- **Frontend**: Thymeleaf, Vanilla CSS3 (Modern SaaS Design System), JavaScript (ES6+)
- **Database**: MySQL 8.0
- **Tools**: Maven, Git

## 📋 Getting Started

### Prerequisites
- Java 17 or higher
- MySQL Server
- Maven

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/rupali1802/TalentBridge.git
   ```
2. Configure your database in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/talentbridge
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Access the portal at `http://localhost:8081`.

## 🔑 Demo Credentials

| Role | Email | Password |
|---|---|---|
| **Admin** | `admin@talentbridge.com` | `Admin@123` |
| **Employer** | `hr@google.com` | `Password@123` |

## 📸 UI Showcase
*Project includes a modern sidebar-based filter system and responsive dashboard layouts.*

---
Developed by [Rupali](https://github.com/rupali1802)

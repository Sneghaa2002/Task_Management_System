# Task Management System

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)

A secure role-based task management system with JWT authentication, real-time notifications, and performance analytics.

## Features

### Core Modules
- **Authentication**  
  ✅ JWT-based secure login  
  ✅ Role-based access (Admin/Employee)  
  ✅ Password encryption (BCrypt)  

- **Task Management**  
  📝 Create/assign tasks with priorities  
  🗓️ Track deadlines & completion status  
  🔍 Filter by status/priority/employee  

- **Real-time Notifications**  
  🔔 In-app alerts for task updates  
  ⏰ Deadline reminders  

- **Analytics Dashboard**  
  📊 Completion rate metrics  
  📈 Weekly performance trends  

## Tech Stack
- **Backend**: Spring Boot 3.1, Java 17
- **Security**: JWT, Spring Security
- **Database**: MySQL 8.0
- **Tools**: Lombok, MapStruct

## API Documentation
| Module       | Endpoints                          |
|--------------|------------------------------------|
| Auth         | `POST /api/auth/{register,login}` |
| Admin        | `GET/POST/PUT /api/admin/tasks`    |
| Employee     | `GET /api/employee/tasks`          |
| Analytics    | `GET /api/analytics`               |

## Quick Start

### Prerequisites
- Java 17+
- MySQL 8.0+
- Maven 3.8+

### Installation
1. Clone repo:
   ```bash
   git clone https://github.com/your-repo/task-management.git

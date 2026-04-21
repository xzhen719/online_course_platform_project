# 📚 Online Course Platform (線上課程平台)

A full-stack online course platform where students can browse, purchase, and learn courses, and instructors can create and manage course content.

## ✨ Features

**Students**
- 🔍 Browse & search courses by category
- 🛒 Shopping cart & checkout (ECPay integration)
- 📖 Watch course videos & track learning progress
- ⭐ Rate & review courses
- 🎫 Coupon rewards on course completion
- 👤 Profile management & password reset

**Instructors**
- 📝 Create, edit & manage courses with chapters
- 💰 View earnings & enrollment data
- 🤖 AI-powered course description generator (OpenRouter)

**General**
- 🔐 JWT-based authentication (login / register)
- 📧 Email-based password reset (Gmail SMTP)
- 💬 Inspirational quote of the day on homepage

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Vue 3, Vite, Pinia, Vue Router, Bootstrap 5 |
| Backend | Spring Boot 3, Spring Security, JPA/Hibernate |
| Database | MySQL |
| Payment | ECPay (sandbox) |
| AI | OpenRouter API |
| Icons | Lucide Vue |

---

## 📋 Prerequisites

Make sure you have installed:

- **Node.js** (v18+) — [Download](https://nodejs.org/)
- **Java JDK** (17+) — [Download](https://adoptium.net/)
- **MySQL** (8.0+) — [Download](https://dev.mysql.com/downloads/)
- **Maven** (3.9+) — or use the included Maven wrapper
- **Ngrok** (optional, for ECPay payment callback) — [Download](https://ngrok.com/)

---

## 🚀 Getting Started

### 1. Clone the repo

```bash
git clone https://github.com/YOUR_USERNAME/OCP_project.git
cd OCP_project
```

### 2. Set up MySQL database

Open MySQL and run:

```sql
CREATE DATABASE online_course_platform;
CREATE USER 'ocp_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON online_course_platform.* TO 'ocp_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure Backend

```bash
cd backend/src/main/resources
copy application.properties.example application.properties
```

Open `application.properties` and replace every `YOUR_xxx_HERE` with your real values:

| Key | Where to get it |
|-----|----------------|
| `DB_PASSWORD` | The MySQL password you set in Step 2 |
| `JWT_SECRET` | Any random long string (e.g. `MySecretKey12345!@#`) |
| `ECPAY_HASH_KEY` | From [ECPay sandbox](https://www.ecpay.com.tw/) — test values: `5294y06JbISpM5x9` |
| `ECPAY_HASH_IV` | From ECPay sandbox — test value: `v77hoKGq4kWxNNIS` |
| `MAIL_USERNAME` | Your Gmail address |
| `MAIL_PASSWORD` | Gmail App Password ([how to get one](https://support.google.com/accounts/answer/185833)) |
| `OPENROUTER_API_KEY` | Sign up at [openrouter.ai](https://openrouter.ai/) → Keys |

### 4. Configure Frontend

```bash
cd frontend
copy .env.example .env
```

Open `.env` and add your API Ninjas key (get a free key at [api-ninjas.com](https://api-ninjas.com/)).

### 5. Run Backend

```bash
cd backend
mvnw spring-boot:run
```

The backend will start on **http://localhost:8080**.

### 6. Run Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will start on **http://localhost:5173**.

### 7. (Optional) ECPay Payment Testing

To test ECPay payments locally, you need Ngrok to expose your backend:

```bash
ngrok http 8080
```

Copy the generated URL (e.g. `https://xxxx.ngrok-free.dev`) and update `ecpay.backend-url` in your backend `application.properties`.

---

## 📁 Project Structure

```
OCP_project/
├── backend/                    # Spring Boot backend
│   └── src/main/
│       ├── java/com/ocp/backend/
│       │   ├── controller/     # REST API endpoints
│       │   ├── service/        # Business logic
│       │   ├── model/          # JPA entities
│       │   ├── repository/     # Data access layer
│       │   ├── config/         # Security & app config
│       │   └── util/           # Utility classes (JWT, etc.)
│       └── resources/
│           ├── application.properties.example  # Config template
│           └── data.sql        # Seed data
│
├── frontend/                   # Vue 3 frontend
│   └── src/
│       ├── views/              # Page components
│       ├── components/         # Reusable UI components
│       ├── stores/             # Pinia state management
│       ├── api/                # Axios API layer
│       └── router/             # Vue Router config
│
└── uploads/                    # Course images (generated at runtime)
```

---

## 🔑 Test Accounts

After the app starts, the following seed accounts are available (password for all: **`123456`**):

| Role | Email |
|------|-------|
| Student | ocpstudent01@gmail.com |
| Instructor | andy@gmail.com |
| Instructor | kat@gmail.com |
| Instructor | chris@gmail.com |

---

## 📄 License

This project is for educational purposes.

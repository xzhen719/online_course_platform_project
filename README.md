# iKnowVation - Online Course & Learning Platform 

一個結合獎勵機制的線上課程網站，旨在提供最流暢的學習體驗與開課環境。

---

## 專案說明

- **前後端分離架構**：本專案採用Spring Boot 3 + Vue 3，並整合綠界金流 (ECPay) 與 JWT 安全機制，模擬電商從產品瀏覽到支付完成的業務流程。
- **獎勵機制**：為提升學習動機，此專案實作了「完課獎勵系統」。當學員完成某一課程的所有章節後，系統將自動觸發邏輯，隨機發放 5%~15% 的折價券，鼓勵學員繼續探索下一門課程。
- **課程描述生成**：整合 OpenRouter AI，幫助講師快速生成專業的課程簡介與大綱，減少開課前的內容準備時間。

---

## 網站功能

### 學習者端 (Student)

- **課程探索**：使用者可以瀏覽所有課程，並依類別進行篩選。
- **註冊與登入**：支援 Email 註冊與 JWT 安全認證。
- **收藏系統**：使用者可以收藏感興趣的課程，並在個人中心查看。
- **購物車與交易**：支援加入購物車、套用折價券，並整合綠界 (ECPay)進行金流支付。
- **學習追蹤**：
  - 瀏覽課程介紹與詳細大綱。
  - 線上觀看課程影音。
  - 勾選完成章節，即時同步學習進度百分比。
  - 完成課程時獲得專屬優惠券獎勵。
- **互動與社群**：使用者可以對課程進行評價。
- **密碼管理**：支援透過 Gmail SMTP 發送重設密碼信件。

### 講師端 (Instructor)

- **課程建立**：
  - 編輯課程基本資訊（名稱、簡介、封面圖上傳）。
  - 輸入關鍵字自動產生課程描述。
- **章節管理**：
  - 建立章節序號、名稱、簡介與時數。
  - 支援管理多個單元與影音連結。
- **數據後台**：
  - 查看自己所開課程的學生註冊人數。

---

## 使用技術 / 框架

### Front-end
- **Vue 3** (Composition API)
- **Vite** (Build tool)
- **Pinia** (State Management)
- **Vue Router** (Single Page Routing)
- **Bootstrap 5** (Layout & UI)
- **Axios** (API Requests)

### Back-end
- **Java 17**
- **Spring Boot 3.x**
- **Spring Security** (Authentication & Authorization)
- **JWT** (Stateless Token-based Auth)
- **Spring Data JPA** (Hibernate)
- **Maven** (Dependency Management)

### Database
- **MySQL 8.0**

### 第三方整合
- **ECPay** (金流支付介面)
- **OpenRouter API** (LLM 模型輔助)
- **Gmail SMTP** (非同步發送密碼重設信)

---

## 環境需求 

請確保已安裝以下軟體：

- **Node.js** (v18+) — [下載](https://nodejs.org/)
- **Java JDK** (17+) — [下載](https://adoptium.net/)
- **MySQL** (8.0+) — [下載](https://dev.mysql.com/downloads/)
- **Maven** (3.9+) — 或使用專案內附的 Maven wrapper (`mvnw`)
- **Ngrok** (用於開發環境接收綠界金流回傳) — [下載](https://ngrok.com/)

---

## Installation 本地端安裝與執行

### 1. 複製專案 (Clone the repo)

```bash
git clone https://github.com/YOUR_USERNAME/OCP_project.git
cd OCP_project
```

### 2. 設定 MySQL 資料庫

開啟 MySQL 並執行下列指令：

```sql
CREATE DATABASE IF NOT EXISTS online_course_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'ocp_user'@'localhost' IDENTIFIED BY '你設定的密碼';
GRANT ALL PRIVILEGES ON online_course_platform.* TO 'ocp_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. 配置後端 (Configure Backend)

```bash
cd backend/src/main/resources
copy application.properties.example application.properties
```

開啟 `application.properties` 並將 `YOUR_xxx_HERE` 替換為你的實際數值：

| 欄位 (Key) | 說明與取得方式 |
|-----|----------------|
| `DB_PASSWORD` | 你在步驟 2 中欲設定的 MySQL 密碼 |
| `JWT_SECRET` | 任意隨機長字串（如：`MySecretKey12345!@#`） |
| `ECPAY_HASH_KEY` | [綠界測試環境](https://www.ecpay.com.tw/) — 測試值為：`5294y06JbISpM5x9` |
| `ECPAY_HASH_IV` | 綠界測試環境 — 測試值為：`v77hoKGq4kWxNNIS` |
| `MAIL_USERNAME` | 你的 Gmail 地址 |
| `MAIL_PASSWORD` | Gmail 應用程式密碼 ([取得教學](https://support.google.com/accounts/answer/185833)) |
| `OPENROUTER_API_KEY` | 到 [openrouter.ai](https://openrouter.ai/) 註冊並取得 Keys |

### 4. 配置前端 (Configure Frontend)

```bash
cd frontend
copy .env.example .env
```

開啟 `.env` 並填入你的 API Ninjas key（可至 [api-ninjas.com](https://api-ninjas.com/) 免費申請）。

### 5. 啟動後端 (Run Backend)

```bash
cd backend
mvnw spring-boot:run
```

後端將在 **http://localhost:8080** 啟動。

### 6. 啟動前端 (Run Frontend)

```bash
cd frontend
npm install
npm run dev
```

前端將在 **http://localhost:5173** 啟動。

### 7. (Optional) 綠界金流測試 (ECPay Payment Testing)

若要在本地測試綠界付款，你需要使用 Ngrok 來公開你的後端 API：

```bash
ngrok http 8080
```

複製產生的網址（例如 `https://xxxx.ngrok-free.dev`），並更新後端 `application.properties` 中的 `ecpay.backend-url`。

---

## 測試帳號 (Test Accounts)

啟動後，系統已預載測試資料，你可以使用以下帳號體驗功能：
*密碼皆為：`123456`*

| 角色 | Email |
|------|-------|
| 學生 | ocpstudent01@gmail.com |
| 講師 | andy@gmail.com |
| 講師 | kat@gmail.com |



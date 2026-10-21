# Quiz Builder

A full-stack quiz application for creating, attempting, and reviewing quizzes.

The project contains:

- A React + Vite frontend
- A Spring Boot backend
- MySQL persistence through Spring Data JPA
- BCrypt password hashing
- Server-side session authentication
- Creator-only quiz management
- Participant quiz submissions and results

## Application flow

1. Sign up as a **Creator** or **Participant**.
2. Log in to create a session.
3. Creators can create, update, and delete their own quizzes.
4. Participants can browse quizzes and submit answers.
5. Participants can view their results from the results page.

## Project structure

```text
quizapp/
├── backend/     Spring Boot REST API
└── frontend/    React + Vite web application
```

## Requirements

Install the following before running the project:

- Java 21
- Maven 3.9 or later
- Node.js 18 or later
- npm
- MySQL 8

Verify the main tools:

```powershell
java -version
mvn -version
node --version
npm --version
```

## Database setup

Start MySQL and create the application database:

```sql
CREATE DATABASE IF NOT EXISTS quiz;
```

The backend uses Hibernate's `update` mode, so its tables are created or updated automatically when it starts.

The database must be created on the same MySQL host and port configured in `DB_URL`. The default is:

```text
jdbc:mysql://localhost:3306/quiz
```

## Configuration

### Backend

Copy the example values from `backend/.env.example` into the environment used to start Spring Boot:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/quiz"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
```

Do not commit real passwords or `.env` files. The example file contains placeholders only.

### Frontend

The frontend defaults to:

```text
http://localhost:9096/api
```

If the backend runs elsewhere, create `frontend/.env` from `frontend/.env.example`:

```text
VITE_API_BASE_URL=http://localhost:9096/api
```

The frontend API client uses Axios with credentials enabled so the session cookie is sent with requests.

## Run locally

Open two PowerShell windows.

### Terminal 1: backend

```powershell
cd C:\Users\pulip\quizapp\backend

$env:DB_URL="jdbc:mysql://localhost:3306/quiz"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"

mvn spring-boot:run
```

The API starts on:

```text
http://localhost:9096
```

### Terminal 2: frontend

```powershell
cd C:\Users\pulip\quizapp\frontend
npm install
npm run dev
```

Open the URL printed by Vite, normally:

```text
http://localhost:5173
```

## Build and validation

Build the backend without running tests:

```powershell
cd backend
mvn -q -DskipTests package
```

Run backend tests:

```powershell
mvn test
```

Build the frontend:

```powershell
cd frontend
npm run build
```

Run the frontend linter:

```powershell
npm run lint
```

## API overview

All API routes are prefixed with `/api`.

| Area | Method | Endpoint | Authentication |
| --- | --- | --- | --- |
| Users | `POST` | `/users/signup` | Public |
| Users | `POST` | `/users/login` | Public |
| Users | `POST` | `/users/logout` | Logged in |
| Quizzes | `GET` | `/quizzes` | Public |
| Quizzes | `GET` | `/quizzes/{id}` | Public |
| Quizzes | `GET` | `/quizzes/domain/{domain}` | Public |
| Quizzes | `POST` | `/quizzes` | Logged-in creator |
| Quizzes | `PUT` | `/quizzes/{id}` | Owning creator |
| Quizzes | `DELETE` | `/quizzes/{id}` | Owning creator |
| Quizzes | `POST` | `/quizzes/{id}/submit` | Logged in |
| Results | `GET` | `/results/user` | Logged-in participant |
| Results | `GET` | `/results/quiz/{quizId}` | Logged in |

Passwords are stored as BCrypt hashes and are never returned in API responses. Login uses an HTTP session cookie rather than exposing a password or token to the frontend.

## Troubleshooting

### `Unknown database 'quiz'`

Create the database in MySQL Workbench or the MySQL client:

```sql
CREATE DATABASE IF NOT EXISTS quiz;
```

Also confirm that `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` point to the same MySQL instance.

### `Access denied for user`

Check the username and password in the same PowerShell window where `mvn spring-boot:run` is executed. Environment variables are scoped to that terminal session.

### Frontend cannot reach the backend

Confirm that:

- Spring Boot is running on port `9096`.
- Vite is using the correct `VITE_API_BASE_URL`.
- The browser is opened at an origin allowed by the backend CORS configuration (`localhost:5173` or `localhost:3000`).

### Session or login problems

Use the same browser origin throughout the flow and do not disable cookies. The frontend is configured with `withCredentials: true` for session-based authentication.

## Security notes

This project is configured for local or personal use:

- Passwords are hashed with BCrypt.
- Session cookies are HTTP-only and same-site.
- Quiz creation, updates, and deletion require an authenticated creator.
- Creators can only modify their own quizzes.
- Database credentials are supplied through environment variables.

For production deployment, use HTTPS, a dedicated database user with limited permissions, secure cookie settings, and a managed secrets store.

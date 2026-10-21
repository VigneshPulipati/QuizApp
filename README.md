# Quiz Builder

## Configuration

1. Create the `quiz` MySQL database.
2. Copy `backend/.env.example` values into your environment and set `DB_USERNAME` and `DB_PASSWORD`.
3. Copy `frontend/.env.example` to `frontend/.env` if the backend is not running at the default URL.

The backend uses BCrypt for passwords and an HTTP session cookie for login. Keep the frontend and backend origins in the configured CORS list when changing ports.

## Run locally

```bash
cd backend
mvn spring-boot:run

cd frontend
npm install
npm run dev
```

Sign up as a creator to create and manage quizzes. Sign up as a participant to attempt quizzes and view results.

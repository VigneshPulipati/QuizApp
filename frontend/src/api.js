import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:9096/api";

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: { "Content-Type": "application/json" },
});

export async function signup(data) {
  const res = await api.post("/users/signup", data);
  return res.data;
}

export async function login(data) {
  const res = await api.post("/users/login", data);
  return res.data;
}

export async function logout() {
  await api.post("/users/logout");
}

export async function getAllUsers() {
  const res = await api.get("/users/all");
  return res.data;
}

export async function createQuiz(data) {
  const res = await api.post("/quizzes", data);
  return res.data;
}

export async function getAllQuizzes() {
  const res = await api.get("/quizzes");
  return res.data;
}

export async function getMyQuizzes(username) {
  const res = await api.get(`/quizzes/myquizzes/${encodeURIComponent(username)}`);
  return res.data;
}

export async function deleteQuiz(quizId) {
  await api.delete(`/quizzes/${quizId}`);
}

export async function getQuizById(id) {
  const res = await api.get(`/quizzes/${id}`);
  return res.data;
}

export async function getQuizzesByDomain(domain) {
  const res = await api.get(`/quizzes/domain/${encodeURIComponent(domain)}`);
  return res.data;
}

export async function submitQuiz(id, submission) {
  const res = await api.post(`/quizzes/${id}/submit`, submission);
  return res.data;
}

export async function getMyResults() {
  const res = await api.get("/results/user");
  return res.data;
}

export default api;

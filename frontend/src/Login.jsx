import React, { useState } from "react";
import { login } from "./api";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
  e.preventDefault();
  setError("");
  try {
    const result = await login({ email, password }); // result is JSON object now

    if (result && result.id) {
      // store in localStorage
      localStorage.setItem("userId", result.id);
      localStorage.setItem("fullName", result.fullName);
      localStorage.setItem("email", result.email);
      localStorage.setItem("role", result.role);

      // redirect based on role
      if (result.role === "creator") {
        navigate("/creator");
      } else {
        navigate("/participant");
      }
    } else {
      setError("Invalid email or password.");
    }
  } catch (error) {
    setError(error.response?.data || "Unable to log in.");
  }
};


  return (
    <div className="login-page">
      <h2>Login</h2>
      {error && <p role="alert">{error}</p>}
      <form onSubmit={handleLogin}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Login</button>
      </form>
    </div>
  );
}

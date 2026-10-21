import React, { useState } from "react";
import "./App.css";
import { signup, login } from "./api";
import { useNavigate } from "react-router-dom";

export default function App() {
  const [showModal, setShowModal] = useState(false);
  const [isSignup, setIsSignup] = useState(false);
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");

    const data = {
      fullName: isSignup ? e.target[0].value : null,
      role: isSignup ? e.target[1].value : null,
      email: isSignup ? e.target[2].value : e.target[0].value,
      password: isSignup ? e.target[3].value : e.target[1].value,
    };

    try {
      if (isSignup) {
        // ✅ Signup flow
        const result = await signup(data); // result is user object

        if (result && result.id) {
          setMessage("Account created successfully. You can now sign in.");
          setShowModal(false);
          setIsSignup(false);
          navigate("/"); // redirect back to login modal
        } else {
          setMessage("Failed to create account.");
        }
      } else {
        // ✅ Login flow
        const result = await login(data); // result is user object

        if (result && result.id) {
          // store user in localStorage
          localStorage.setItem("userId", result.id);
          localStorage.setItem("fullName", result.fullName);
          localStorage.setItem("email", result.email);
          localStorage.setItem("role", result.role);

          setShowModal(false);

          // redirect based on role
          if (result.role === "creator") {
            navigate("/creator");
          } else {
            navigate("/participant");
          }
        } else {
          setMessage("Login failed. Check your email and password.");
        }
      }
    } catch (error) {
      setMessage(error.response?.data || "Unable to complete the request.");
    }
  };

  return (
    <div className="app-container">
      {/* Header */}
      <header className="header">
        <h1 className="logo">Quiz Builder</h1>
      </header>

      {/* Body Section */}
      <main className="main-content">
        <h2 className="tagline">Participate and gain knowledge here</h2>
        <p className="subtext">
          Challenge yourself with fun quizzes, compete with friends, and track
          your progress in real-time. Learning has never been this exciting!
        </p>
        <button className="glow-btn" onClick={() => setShowModal(true)}>
          Sign In
        </button>
      </main>

      {/* Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div
            className="modal-content"
            onClick={(e) => e.stopPropagation()}
          >
            <h2>{isSignup ? "Create Account" : "Sign In"}</h2>
            {message && <p role="alert" className="form-message">{message}</p>}
            <form onSubmit={handleSubmit}>
              {isSignup && (
                <>
                  <input type="text" placeholder="Full Name" required />
                  <select required>
                    <option value="">Select Role</option>
                    <option value="creator">Creator (Add Quizzes)</option>
                    <option value="participant">
                      Participant (Join Quizzes)
                    </option>
                  </select>
                </>
              )}
              <input type="email" placeholder="Email" required />
              <input type="password" placeholder="Password" required />
              <button type="submit" className="submit-btn">
                {isSignup ? "Sign Up" : "Login"}
              </button>
            </form>
            <p className="toggle-text">
              {isSignup ? "Already have an account?" : "Don’t have an account?"}{" "}
              <span onClick={() => setIsSignup(!isSignup)}>
                {isSignup ? "Sign In" : "Create one"}
              </span>
            </p>
          </div>
        </div>
      )}
    </div>
  );
}

import React, { useEffect, useState } from "react";
import { getMyResults } from "./api";
import "./MyResults.css";

export default function MyResults() {
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    getMyResults()
      .then(setResults)
      .catch(() => setError("Failed to load your quiz results."))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="results-container">
      <h1 className="results-header">My Quiz Results</h1>
      {loading ? (
        <p>Loading results...</p>
      ) : error ? (
        <p className="text-red-600">{error}</p>
      ) : results.length === 0 ? (
        <p>No quiz attempts yet.</p>
      ) : (
        <table className="results-table">
          <thead>
            <tr><th>Quiz Title</th><th>Domain</th><th>Score</th><th>Total</th><th>Date</th></tr>
          </thead>
          <tbody>
            {results.map((result, index) => (
              <tr key={`${result.submittedAt}-${index}`}>
                <td>{result.quizTitle || "Untitled Quiz"}</td>
                <td>{result.quizDomain || "N/A"}</td>
                <td>{result.score}</td>
                <td>{result.total}</td>
                <td>{new Date(result.submittedAt).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

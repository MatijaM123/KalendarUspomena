import React from 'react';
import { AuthProvider } from './components/AuthProvider';
import ProtectedRoute from './components/ProtectedRoute';
import RegisterPage from './components/RegisterPage';
import LoginPage from './components/LoginPage';
import CalendarPage from './components/CalendarPage';
import MemoryPage from './components/MemoryPage';
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate, useParams } from 'react-router-dom';
import './App.css'; // Optional: add your CSS here

// Create an authentication context to simulate login state 


// ----- Register Screen ----- 


// ----- Main App Component ----- 
function App() {
  return (
    <AuthProvider>
      <Router basename='/KalendarUspomena'>
        <Routes>
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/calendar" element={<ProtectedRoute><CalendarPage /></ProtectedRoute>} />
          <Route path="/memories/:date" element={<ProtectedRoute><MemoryPage /></ProtectedRoute>} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;

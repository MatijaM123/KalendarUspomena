import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './RegisterPage.css'
const RegisterPage = () => {
  const navigate = useNavigate();
  const [message, setMessage] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const username = e.target[0].value;
    const email = e.target[3].value;
    const password = e.target[4].value;
    const ime = e.target[1].value;
    const prezime = e.target[2].value;

    try {
      const response = await axios.post('http://localhost:8082/api/auth/register', { username, password,email,ime,prezime });
      navigate('/login');
    } catch (error) {
      setMessage(()=>(error.response.data.message.join("\n")));
        alert(error.response.data.message.join("\n"));
    }
  };

  return (
    <div className="auth-container">
      <h2>Register</h2>
      <form onSubmit={handleSubmit} className="form">
        <input type="text" placeholder="Username" required />
        <input type="text" placeholder="First Name" required />
        <input type="text" placeholder="Last Name" required />
        <input type="email" placeholder="Email" required />
        <input type="password" placeholder="Password" required />
        <button type="submit">Register</button>
      </form>
      <p> Already have an account? <a href="/login">Login here</a> </p>
    </div>
  );
};

export default RegisterPage;

import React, { useContext, useState } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';

import { useNavigate } from 'react-router-dom';
import { AuthContext } from './AuthProvider';

const LoginPage = () => {
  const [message, setMessage] = useState(''); // State for error messages

  const navigate = useNavigate();
  const { login } = useContext(AuthContext);

   const handleSubmit = async (e) => {
    e.preventDefault();
    
    const identificator = e.target[0].value;
    const password = e.target[1].value;

    try {
      const response = await axios.post('http://localhost:8082/api/auth/login', { identificator, password });
        Cookies.set('accessToken', response.data.accessToken);
        Cookies.set('refreshToken', response.data.refreshToken);
        login();
        navigate('/calendar');
      
    } catch (error) {
      setMessage(()=>(error.response.data.message.join("\n")));
        alert(error.response.data.message.join("\n"));
    }

  };

  return (
    <div className="auth-container">
      <h2>Login</h2>
      <form onSubmit={handleSubmit} className="form">
        <input type="email" placeholder="Email" required />
        <input type="password" placeholder="Password" required />
        <button type="submit">Login</button>
      </form>
      <p> Don’t have an account? <a href="/register">Register here</a> </p>
    </div>
  );
};

export default LoginPage;
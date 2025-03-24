import axios from 'axios';
import Cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom';
import { Navigate } from 'react-router-dom';


const LoginPage = () => {
  const navigate = useNavigate();
   const handleSubmit = async (e) => {
    e.preventDefault();
    
    const identificator = e.target[0].value;
    const password = e.target[1].value;

    try {
      const response = await axios.post('http://localhost:8082/api/auth/login', { identificator, password });
        Cookies.set('accessToken', response.data.accesToken);
        Cookies.set('refreshToken', response.data.refreshToken);
        navigate('/calendar');     
    } catch (error) {
        alert(error.response.data.message.join("\n"));
    }

  };

  if(Cookies.get('accessToken')&&Cookies.get('refreshToken'))
    return <Navigate to="/calendar" replace />;

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
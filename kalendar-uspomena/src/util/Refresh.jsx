import axios from 'axios';
import Cookies from 'js-cookie';


const refreshAccessToken = async (navigate) => {
    try {
      const refreshToken = Cookies.get('refreshToken');
      const response = await axios.post('http://localhost:8082/api/auth/refresh', { refreshToken });
  
      Cookies.set('accessToken', response.data.accesToken);
    } catch (error) {
      Cookies.remove('refreshToken');
      Cookies.remove('accessToken');
      navigate("/login", { replace: true });
      return;
    }
  };

  export default refreshAccessToken;
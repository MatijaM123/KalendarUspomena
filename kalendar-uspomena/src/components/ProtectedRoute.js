import React from 'react';
import { Navigate } from 'react-router-dom';
import axios from 'axios';
import Cookies from 'js-cookie';
import backendUrl from '../config';

const ProtectedRoute = ({ children }) => {  
  let refreshToken = Cookies.get('refreshToken');
  let accessToken = Cookies.get('accessToken');

  if (!refreshToken || !accessToken) {
    return <Navigate to="/login" replace />;
  }

  axios.post(`${backendUrl}/api/auth/refresh`, { refreshToken })
    .then(response => {
      Cookies.set('accessToken',response.data.accesToken)
      accessToken = response.data.accesToken;
    })
    .catch(error => {
      return <Navigate to="/login" replace />;
    });

  return children;

};

export default ProtectedRoute;

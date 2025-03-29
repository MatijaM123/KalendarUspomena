import React, { useState, useEffect } from 'react';
import {useNavigate } from 'react-router-dom';
import axios from 'axios';
import Cookies from 'js-cookie';
import "./CalendarPage.css";
import refreshAccessToken from '../util/Refresh.jsx';
import backendUrl from '../config.js';

const CalendarPage = () => {
  const navigate = useNavigate();
  const [currentDate, setCurrentDate] = useState(new Date());
  const [memoryCounts, setMemoryCounts] = useState({});

  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();

  const firstDay = new Date(year, month, 1).getDay();
  const daysInMonth = new Date(year, month + 1, 0).getDate();

  const getNumbersOfMemories = async () => {
    const accessToken = Cookies.get('accessToken');
    const refreshToken = Cookies.get('refreshToken');
    if(!accessToken||!refreshToken){
      navigate("/login", { replace: true });
      return;
    }

    try {
      const response = await axios.get(`${backendUrl}/api/uspomene/counts`
        ,{ params:{year, month: month+1}, headers: {'Authorization': `Bearer ${accessToken}`, 'Content-Type': 'application/json'} });
      setMemoryCounts(response.data.counts);
    } catch (error){
        const errorMessage = error.response?.data?.message ? error.response.data.message.join("\n") : "An error occurred while fetching memory counts.";
        alert(errorMessage);
      }
  };

  useEffect(() => {
    const fetchData = async () => {
      await refreshAccessToken(navigate);  // Wait for token refresh
      await getNumbersOfMemories(); // Fetch memories after refresh
    };
  
    fetchData();
  }, [year, month]); // Run when year or month changes

  const handleDateClick = (day) => {
    const selectedDate = new Date(year, month, day+1).toISOString().split('T')[0];
      navigate(`/memories/${selectedDate}`);
  };

  const handleGoBack = () => {
    setCurrentDate(new Date(year, month - 1, 1));
  };

  const handleGoForward = () => {
    setCurrentDate(new Date(year, month + 1, 1));
  };

  const calendarCells = [];
  for (let i = 0; i < firstDay; i++) {
    calendarCells.push(<td key={`empty-${i}`}></td>);
  }
  for (let day = 1; day <= daysInMonth; day++) {
    calendarCells.push(
      <td key={day} onClick={() => handleDateClick(day)} style={{ cursor: 'pointer', textAlign: 'center' }}>
        <div>{day}</div>
        <div style={{ fontSize: '0.8em', color: '#555' }}>{memoryCounts[day-1] || 0} memories</div>
      </td>
    );
  }

  const rows = [];
  for (let i = 0; i < calendarCells.length; i += 7) {
    rows.push(<tr key={i}>{calendarCells.slice(i, i + 7)}</tr>);
  }

  return (
    <div className="calendar-container">
      <h2>Memory Calendar</h2>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
        <button onClick={handleGoBack}>Go Back</button>
        <span style={{ fontSize: '1.5em' }}>{`${currentDate.toLocaleString('default', { month: 'long' })} ${year}`}</span>
        <button onClick={handleGoForward}>Go Forward</button>
      </div>
      <table style={{ borderCollapse: 'collapse', width: '100%', height: '100%' }}>
        <thead>
          <tr>
            <th>Sun</th>
            <th>Mon</th>
            <th>Tue</th>
            <th>Wed</th>
            <th>Thu</th>
            <th>Fri</th>
            <th>Sat</th>
          </tr>
        </thead>
        <tbody>
          {rows}
        </tbody>
      </table>
    </div>
  );
};

export default CalendarPage;

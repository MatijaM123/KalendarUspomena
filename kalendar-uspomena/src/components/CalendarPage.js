import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useParams } from 'react-router-dom';

const CalendarPage = () => {
  const navigate = useNavigate();

  const currentDate = new Date();
  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();

  const firstDay = new Date(year, month, 1).getDay();
  const daysInMonth = new Date(year, month + 1, 0).getDate();

  const memoryCounts = {};
  for (let day = 1; day <= daysInMonth; day++) {
    memoryCounts[day] = Math.floor(Math.random() * 5);
  }

  const handleDateClick = (day) => {
    const selectedDate = new Date(year, month, day).toISOString().split('T')[0];
    navigate(`/memories/${selectedDate}`);
  };

  const calendarCells = [];
  for (let i = 0; i < firstDay; i++) {
    calendarCells.push(<td key={`empty-${i}`}></td>);
  }
  for (let day = 1; day <= daysInMonth; day++) {
    calendarCells.push(
      <td key={day} onClick={() => handleDateClick(day)} style={{ cursor: 'pointer', padding: '10px', border: '1px solid #ccc', textAlign: 'center' }}>
        <div>{day}</div>
        <div style={{ fontSize: '0.8em', color: '#555' }}>{memoryCounts[day]} memories</div>
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
      <table style={{ borderCollapse: 'collapse', width: '100%' }}>
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

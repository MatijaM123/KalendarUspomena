import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const MemoryPage = () => {
  const { date } = useParams();
  const navigate = useNavigate();
  const [memories, setMemories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    const fetchMemories = async () => {
      try {
        const response = await axios.get('/api/memories'); // Replace with your API endpoint
        setMemories(response.data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchMemories();
  }, []);

  const handleNext = () => {
    setCurrentIndex((prevIndex) => (prevIndex + 1) % memories.length);
  };

  const handlePrevious = () => {
    setCurrentIndex((prevIndex) => (prevIndex - 1 + memories.length) % memories.length);
  };

  const handleAddMemory = async (event) => {
    const file = event.target.files[0];
    const formData = new FormData();
    formData.append('image', file);
    formData.append('date', date);

    try {
      await axios.post('/api/memories', formData); // Replace with your API endpoint
      // Optionally, refresh the memories after adding a new one
      const response = await axios.get('/api/memories');
      setMemories(response.data);
    } catch (err) {
      setError(err.message);
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="memories-container">
      <h2 style={{ fontSize: '2em' }}>{date}</h2>
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <button onClick={handlePrevious}>←</button>
        <img src={memories[currentIndex]} alt={`Memory ${currentIndex + 1}`} style={{ width: '600px', height: '400px', objectFit: 'cover' }} />
        <button onClick={handleNext}>→</button>
      </div>
      <div>
        <input type="file" onChange={handleAddMemory} />
      </div>
    </div>
  );
};

export default MemoryPage;

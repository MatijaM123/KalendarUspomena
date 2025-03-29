import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Cookies from 'js-cookie';
import refreshAccessToken from '../util/Refresh';
import './MemoryPage.css';
import backendUrl from '../config';

const MemoryPage = () => {
  const { date } = useParams();
  const navigate = useNavigate();
  const [memories, setMemories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentIndex, setCurrentIndex] = useState(0);

  const fetchUspomene = async () => {
    const accessToken = Cookies.get('accessToken');
    const refreshToken = Cookies.get('refreshToken');
    if(!accessToken||!refreshToken){
      navigate("/login", { replace: true });
      return;
    }

    try {
      const [year, month, day] = date.split("-").map(Number);
      const response = await axios.get(`${backendUrl}/api/uspomene/`
        ,{ params:{year, month, day}, headers: {'Authorization': `Bearer ${accessToken}`, 'Content-Type': 'application/json'} });
      setMemories(response.data.uspomene);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };


  useEffect(() => {
    
    const fetchMemories = async () => {
      await refreshAccessToken(navigate);
      await fetchUspomene();
    };
    fetchMemories();
  }, []);

  const handleNext = () => {
    setCurrentIndex((prevIndex) => memories.length>0?(prevIndex + 1) % memories.length:0);
  };

  const handlePrevious = () => {
    setCurrentIndex((prevIndex) => memories.length>0?(prevIndex - 1 + memories.length) % memories.length : 0);
  };
  const handleBack = () => {
    navigate("/calendar", { replace: true });
      return;
  };
  const toBase64 = (file) =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result); // Keeps "data:image/png;base64,"
      reader.onerror = (error) => reject(error);
    });

  const handleAddMemory = async (event) => {
    const file = event.target.files[0];
    if(!file) return;
    const formattedDate = `${date}T00:00:00.000+00:00`;
    try{
      const base64Image = await toBase64(file);
      
      const payload = {
        slika: base64Image, // Includes "data:image/png;base64,<base64_string>"
        datum: formattedDate,
      };
        await refreshAccessToken(navigate);
        const accessToken = Cookies.get("accessToken");
        await axios.post(`${backendUrl}/api/uspomene/add`,payload,  {headers: {'Authorization': `Bearer ${accessToken}`, 'Content-Type': 'application/json'}});
        await fetchUspomene();
    }
     catch (err) {
      setError(err.message);
    }
  };
  const deleteMemory = async ()=>{
    try{
      const accessToken = Cookies.get('accessToken');
      const response = await axios.delete(`${backendUrl}/api/uspomene/delete`
        ,{ params:{id: memories[currentIndex].id}, headers: {'Authorization': `Bearer ${accessToken}`, 'Content-Type': 'application/json'} });
        return;
    }catch{}
  }

  const handleDelete = async ()=>{
    const userConfirmed = window.confirm("Are you sure you want to delete?");
  if (userConfirmed) {
    await refreshAccessToken(navigate);
    await deleteMemory();
    await fetchUspomene();
    setCurrentIndex(Math.max(0, currentIndex - 1));
  }
  }

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <>
      <button onClick={handleBack}>BACK</button>
      <h2 style={{ fontSize: '2em' }}>{date}</h2>
      <div className="memories-container">
    
        {/* Left Button */}
        <div className="nav-column">
          <button onClick={handlePrevious} className="nav-btn">←</button>
        </div>
  
        {/* Image with Delete Button */}
        <div className="image-wrapper">
          <div className="image-container">
            <img src={memories.length>0 ? memories[currentIndex].slika : "/logo512.png"} alt={`Memory ${currentIndex}`} />
            {memories.length>0?<button className="delete-btn" onClick={() => handleDelete(currentIndex)}>🗑️</button> : <></>}
          </div>
        </div>
  
        {/* Right Button */}
        <div className="nav-column">
          <button onClick={handleNext} className="nav-btn">→</button>
        </div>
  
      </div>
      <div style={{ alignItems: "center" }}>
        <input type="file" onChange={handleAddMemory} />
      </div>
    </>
  );
};

export default MemoryPage;

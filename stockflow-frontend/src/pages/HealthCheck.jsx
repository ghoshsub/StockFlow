import React, { useState, useEffect } from 'react';
import api from '../api/axios';

const HealthCheck = () => {
  const [status, setStatus] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchHealth = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.get('/health');
      setStatus(response.data.status);
    } catch (err) {
      setError(err.message || 'Failed to connect to StockFlow Backend');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHealth();
  }, []);

  return (
    <div className="max-w-2xl mx-auto space-y-8 animate-fade-in">
      <div className="flex flex-col gap-2">
        <h1 className="text-3xl font-extrabold tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-white via-slate-200 to-slate-400">
          System Health Monitoring
        </h1>
        <p className="text-slate-400 text-sm">
          Tests real-time communication between React frontend and Spring Boot backend via Axios.
        </p>
      </div>

      <div className="glass-panel rounded-2xl p-8 relative overflow-hidden flex flex-col items-center justify-center min-h-[300px] border border-white/5">
        <div className="absolute top-0 right-0 p-4">
          <button
            onClick={fetchHealth}
            disabled={loading}
            className="p-2 rounded-lg bg-white/5 hover:bg-white/10 text-slate-300 disabled:opacity-50 transition-all duration-200"
            title="Refresh Status"
          >
            <svg className={`w-5 h-5 ${loading ? 'animate-spin' : ''}`} fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 1121.21 8H18" />
            </svg>
          </button>
        </div>

        {loading ? (
          <div className="flex flex-col items-center gap-4">
            <div className="w-12 h-12 rounded-full border-4 border-brand-500/20 border-t-brand-500 animate-spin" />
            <span className="text-slate-400 text-sm">Connecting to backend health service...</span>
          </div>
        ) : error ? (
          <div className="flex flex-col items-center gap-6 text-center max-w-md">
            <div className="w-16 h-16 rounded-full bg-red-500/10 flex items-center justify-center text-red-400 border border-red-500/20">
              <svg className="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
            <div className="space-y-2">
              <h3 className="text-lg font-bold text-white">Backend Offline</h3>
              <p className="text-slate-400 text-sm">{error}</p>
            </div>
            <div className="text-xs bg-slate-900/60 border border-white/5 rounded-lg p-3 text-slate-500 w-full text-left space-y-1">
              <p>1. Start the Spring Boot backend (`mvn spring-boot:run` or via IDE)</p>
              <p>2. Verify backend runs on port 8080</p>
              <p>3. CORS is enabled, so this check will execute automatically</p>
            </div>
          </div>
        ) : (
          <div className="flex flex-col items-center gap-6 text-center">
            <div className="w-16 h-16 rounded-full bg-emerald-500/10 flex items-center justify-center text-emerald-400 border border-emerald-500/20 animate-pulse">
              <svg className="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div className="space-y-2">
              <h3 className="text-lg font-bold text-white">All Systems Operational</h3>
              <p className="text-brand-400 text-sm font-semibold">{status}</p>
            </div>
            <span className="text-xs text-slate-500">Connected to http://localhost:8080/api/health</span>
          </div>
        )}
      </div>
    </div>
  );
};

export default HealthCheck;

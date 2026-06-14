import axios from 'axios';

// sessionId: se genera una vez y se persiste en localStorage
// El backend lo usa para identificar la sesion anonima (X-Session-Id)
function getSessionId(): string {
  let sessionId = localStorage.getItem('pc-advisor-session-id');
  if (!sessionId) {
    sessionId = crypto.randomUUID();
    localStorage.setItem('pc-advisor-session-id', sessionId);
  }
  return sessionId;
}

const client = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Inyecta el X-Session-Id en cada request automaticamente
client.interceptors.request.use((config) => {
  config.headers['X-Session-Id'] = getSessionId();
  return config;
});

export default client;
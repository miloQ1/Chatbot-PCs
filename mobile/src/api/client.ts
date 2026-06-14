import axios from 'axios';

function getSessionId(): string {
  let sessionId = localStorage.getItem('pc-advisor-session-id');
  if (!sessionId) {
    sessionId = crypto.randomUUID();
    localStorage.setItem('pc-advisor-session-id', sessionId);
  }
  return sessionId;
}

const client = axios.create({
  baseURL: '',
  headers: { 'Content-Type': 'application/json' },
});

client.interceptors.request.use((config) => {
  config.headers['X-Session-Id'] = getSessionId();
  return config;
});

export default client;
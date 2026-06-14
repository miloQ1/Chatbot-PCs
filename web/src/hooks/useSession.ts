import { useState } from 'react';

// Expone el sessionId por si algun componente lo necesita mostrar (debug, etc.)
export function useSession(): string {
  const [sessionId] = useState<string>(() => {
    let id = localStorage.getItem('pc-advisor-session-id');
    if (!id) {
      id = crypto.randomUUID();
      localStorage.setItem('pc-advisor-session-id', id);
    }
    return id;
  });

  return sessionId;
}
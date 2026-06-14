import { useState } from 'react';

interface Props {
  onSend: (content: string) => void;
  disabled: boolean;
}

export function ChatInput({ onSend, disabled }: Props) {
  const [value, setValue] = useState('');

  const handleSend = () => {
    const trimmed = value.trim();
    if (!trimmed || disabled) return;
    onSend(trimmed);
    setValue('');
  };

  return (
    <div style={{
      display: 'flex',
      gap: '10px',
      padding: '16px',
      borderTop: '1px solid #e2e8f0',
      background: '#fff',
    }}>
      <input
        type="text"
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onKeyDown={(e) => e.key === 'Enter' && !e.shiftKey && handleSend()}
        placeholder="Pregúntale al asesor de PC..."
        disabled={disabled}
        style={{
          flex: 1,
          padding: '10px 14px',
          borderRadius: '999px',
          border: '1px solid #e2e8f0',
          fontSize: '14px',
          outline: 'none',
          background: disabled ? '#f8fafc' : '#fff',
        }}
      />
      <button
        onClick={handleSend}
        disabled={disabled || !value.trim()}
        style={{
          padding: '10px 20px',
          borderRadius: '999px',
          border: 'none',
          background: disabled || !value.trim() ? '#e2e8f0' : '#3b82f6',
          color: disabled || !value.trim() ? '#9ca3af' : '#fff',
          fontWeight: 600,
          fontSize: '14px',
          cursor: disabled || !value.trim() ? 'not-allowed' : 'pointer',
          transition: 'all 0.15s',
        }}
      >
        Enviar
      </button>
    </div>
  );
}
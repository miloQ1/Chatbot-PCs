export function Spinner() {
  return (
    <div style={{
      width: '24px',
      height: '24px',
      border: '3px solid #e2e8f0',
      borderTop: '3px solid #3b82f6',
      borderRadius: '50%',
      animation: 'spin 0.8s linear infinite',
    }} />
  );
}
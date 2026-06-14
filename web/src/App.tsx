import { useState, useRef, useCallback } from 'react';
import { CatalogPanel } from './components/catalog/CatalogPanel';
import { ChatPanel } from './components/chat/ChatPanel';
import type { ProductResponse } from './types';

export default function App() {
  const [highlightedIds, setHighlightedIds] = useState<number[]>([]);
  const [chatVisible, setChatVisible] = useState(true);
  const [catalogWidth, setCatalogWidth] = useState(460);
  const isResizing = useRef(false);
  const containerRef = useRef<HTMLDivElement>(null);

  const handleRecommend = (ids: number[], _products: ProductResponse[]) => {
    setHighlightedIds(ids);
  };

  const startResize = useCallback((e: React.MouseEvent) => {
    isResizing.current = true;
    e.preventDefault();
    const onMouseMove = (e: MouseEvent) => {
      if (!isResizing.current || !containerRef.current) return;
      const left = containerRef.current.getBoundingClientRect().left;
      setCatalogWidth(Math.min(700, Math.max(280, e.clientX - left)));
    };
    const onMouseUp = () => {
      isResizing.current = false;
      window.removeEventListener('mousemove', onMouseMove);
      window.removeEventListener('mouseup', onMouseUp);
    };
    window.addEventListener('mousemove', onMouseMove);
    window.addEventListener('mouseup', onMouseUp);
  }, []);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100vh', background: '#0f172a' }}>

      {/* Navbar */}
      <header style={{
        height: '56px',
        background: '#0f172a',
        borderBottom: '1px solid #1e293b',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '0 24px',
        flexShrink: 0,
        zIndex: 100,
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <span style={{ fontSize: '20px' }}>🖥️</span>
          <span style={{ fontWeight: 800, fontSize: '16px', color: '#f8fafc', letterSpacing: '-0.3px' }}>
            Pc<span style={{ color: '#3b82f6' }}>Consultas</span>
          </span>
        </div>
          <span style={{
            background: '#1e293b',
            color: '#94a3b8',
            fontSize: '11px',
            padding: '2px 8px',
            borderRadius: '999px',
            marginLeft: '4px',
          }}>
            +Chatbot
          </span>
        </div>

        <button
          onClick={() => setChatVisible((v) => !v)}
          style={{
            background: chatVisible ? '#1e40af' : '#3b82f6',
            border: 'none',
            borderRadius: '8px',
            padding: '7px 14px',
            fontSize: '13px',
            fontWeight: 600,
            color: '#fff',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '6px',
          }}
        >
          {chatVisible ? '✕ Cerrar asesor' : '💬 Abrir asesor'}
        </button>
      </header>

      {/* Contenido principal */}
      <div ref={containerRef} style={{ display: 'flex', flex: 1, overflow: 'hidden' }}>

        {/* Panel catalogo */}
        <div style={{
          width: chatVisible ? `${catalogWidth}px` : '100%',
          minWidth: '280px',
          background: '#f8fafc',
          overflowY: 'auto',
          flexShrink: 0,
          transition: chatVisible ? 'none' : 'width 0.2s',
        }}>
          <CatalogPanel highlightedIds={highlightedIds} />
        </div>

        {/* Divisor */}
        {chatVisible && (
          <div
            onMouseDown={startResize}
            style={{
              width: '4px',
              background: '#1e293b',
              cursor: 'col-resize',
              flexShrink: 0,
              transition: 'background 0.15s',
            }}
            onMouseEnter={(e) => (e.currentTarget.style.background = '#3b82f6')}
            onMouseLeave={(e) => (e.currentTarget.style.background = '#1e293b')}
          />
        )}

        {/* Panel chat */}
        {chatVisible && (
          <div style={{ flex: 1, minWidth: 0, background: '#fff' }}>
            <ChatPanel onRecommend={handleRecommend} />
          </div>
        )}
      </div>
    </div>
  );
}
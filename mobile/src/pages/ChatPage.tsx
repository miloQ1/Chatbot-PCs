import { useEffect, useRef, useState } from 'react';
import {
  IonPage, IonHeader, IonToolbar, IonTitle, IonContent,
  IonFooter, IonButton, IonIcon, IonSpinner, IonAlert,
} from '@ionic/react';
import { refreshOutline, sendOutline } from 'ionicons/icons';
import { useChat } from '../hooks/useChat';
import { MessageBubble } from '../components/MessageBubble';

export function ChatPage() {
  const { messages, recommendedMap, loading, sending, error, sendMessage, resetConversation } = useChat();
  const [input, setInput] = useState('');
  const [showAlert, setShowAlert] = useState(false);
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, sending]);

  const handleSend = () => {
    const trimmed = input.trim();
    if (!trimmed || sending) return;
    sendMessage(trimmed);
    setInput('');
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar color="primary">
          <IonTitle>
            <span style={{ fontWeight: 800 }}>Pc</span>Consultas
          </IonTitle>
          <IonButton
            slot="end"
            fill="clear"
            color="light"
            onClick={() => setShowAlert(true)}
          >
            <IonIcon icon={refreshOutline} />
          </IonButton>
        </IonToolbar>
      </IonHeader>

      <IonContent style={{ '--background': '#f8fafc' }}>
        <div style={{ paddingTop: '16px', paddingBottom: '16px' }}>

          {loading && (
            <div style={{ display: 'flex', justifyContent: 'center', marginTop: '60px' }}>
              <IonSpinner name="crescent" color="primary" />
            </div>
          )}

          {!loading && messages.length === 0 && (
            <div style={{ textAlign: 'center', color: '#9ca3af', marginTop: '80px', padding: '0 32px' }}>
              <div style={{ fontSize: '48px', marginBottom: '16px' }}>🖥️</div>
              <div style={{ fontSize: '16px', fontWeight: 600, color: '#374151', marginBottom: '8px' }}>
                Asesor de PC
              </div>
              <div style={{ fontSize: '14px', lineHeight: 1.6 }}>
                Cuéntame qué necesitas y te ayudo a encontrar los mejores componentes para tu PC.
              </div>
            </div>
          )}

          {messages.map((message) => (
            <MessageBubble
              key={message.id}
              message={message}
              recommendedProducts={(recommendedMap as any)[message.id] ?? []}
            />
          ))}

          {sending && (
            <div style={{ padding: '0 16px', marginBottom: '16px' }}>
              <div style={{
                display: 'inline-block',
                padding: '10px 14px',
                borderRadius: '18px 18px 18px 4px',
                background: '#f1f5f9',
                fontSize: '14px',
                color: '#6b7280',
              }}>
                Buscando en el catálogo...
              </div>
            </div>
          )}

          {error && (
            <div style={{ color: '#ef4444', fontSize: '13px', textAlign: 'center', padding: '8px 16px' }}>
              {error}
            </div>
          )}

          <div ref={bottomRef} />
        </div>
      </IonContent>

      <IonFooter>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '8px',
          padding: '10px 12px',
          background: '#fff',
          borderTop: '1px solid #e2e8f0',
        }}>
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            placeholder="Pregúntale al asesor..."
            style={{
              flex: 1,
              padding: '10px 14px',
              borderRadius: '999px',
              border: '1px solid #e2e8f0',
              fontSize: '14px',
              outline: 'none',
              background: '#f8fafc',
            }}
          />
          <IonButton
            shape="round"
            color="primary"
            disabled={sending || !input.trim()}
            onClick={handleSend}
          >
            <IonIcon icon={sendOutline} />
          </IonButton>
        </div>
      </IonFooter>

      <IonAlert
        isOpen={showAlert}
        header="Nueva conversación"
        message="¿Deseas borrar el historial y empezar de nuevo?"
        buttons={[
          { text: 'Cancelar', role: 'cancel' },
          { text: 'Confirmar', handler: () => resetConversation() },
        ]}
        onDidDismiss={() => setShowAlert(false)}
      />
    </IonPage>
  );
}
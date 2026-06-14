import { useEffect, useRef, useState } from 'react';
import client from '../api/client';
import type {
  ChatMessageResponse,
  ConversationResponse,
  MessageResponse,
  ProductResponse,
} from '../types';

interface UseChatResult {
  messages: MessageResponse[];
  recommendedProducts: ProductResponse[];
  recommendedIds: number[];
  loading: boolean;
  sending: boolean;
  error: string | null;
  conversationId: number | null;
  sendMessage: (content: string) => Promise<void>;
  resetConversation: () => Promise<void>;
}

// Asocia los productos recomendados al id del mensaje del asistente
type RecommendedMap = Record<number, ProductResponse[]>;

export function useChat(): UseChatResult {
  const [conversationId, setConversationId] = useState<number | null>(null);
  const [messages, setMessages] = useState<MessageResponse[]>([]);
  const [recommendedMap, setRecommendedMap] = useState<RecommendedMap>({});
  const [recommendedIds, setRecommendedIds] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const initialized = useRef(false);

  useEffect(() => {
    if (initialized.current) return;
    initialized.current = true;

    client
      .get<ConversationResponse>('/api/v1/conversations/current')
      .then((res) => {
        setConversationId(res.data.id);
        setMessages(res.data.messages);
      })
      .catch(() => setError('No se pudo conectar con el servidor.'))
      .finally(() => setLoading(false));
  }, []);


  const resetConversation = async () => {
  try {
    await client.delete('/api/v1/conversations/current');
    setMessages([]);
    setRecommendedMap({});
    setRecommendedIds([]);
    setConversationId(null);
    initialized.current = false;

    // Crea una nueva conversacion
    const res = await client.get<ConversationResponse>('/api/v1/conversations/current');
    setConversationId(res.data.id);
    setMessages(res.data.messages);
  } catch {
    setError('No se pudo reiniciar la conversación.');
  }
};

  const sendMessage = async (content: string) => {
    if (!conversationId || sending) return;
    setSending(true);
    setError(null);

    try {
      const res = await client.post<ChatMessageResponse>(
        `/api/v1/conversations/${conversationId}/messages`,
        { content }
      );

      const { userMessage, assistantMessage, recommendedProducts } = res.data;

      setMessages((prev) => [...prev, userMessage, assistantMessage]);

      // Asocia los productos al id del mensaje del asistente
      if (recommendedProducts.length > 0) {
        setRecommendedMap((prev) => ({
          ...prev,
          [assistantMessage.id]: recommendedProducts,
        }));
        setRecommendedIds(recommendedProducts.map((p) => p.id));
      }
    } catch {
      setError('El asesor no está disponible en este momento, intenta nuevamente.');
    } finally {
      setSending(false);
    }
  };

  // Todos los productos recomendados (para el catalogo)
  const allRecommended = Object.values(recommendedMap).flat();
  const uniqueRecommended = Array.from(
    new Map(allRecommended.map((p) => [p.id, p])).values()
  );

  return {
    messages,
    recommendedProducts: uniqueRecommended,
    recommendedIds,
    loading,
    sending,
    error,
    conversationId,
    sendMessage,
    resetConversation,
    // Expone el mapa para que ChatPanel lo pase a MessageBubble
    // @ts-ignore
    recommendedMap,
  };


  
}
import { useEffect, useRef, useState } from 'react';
import client from '../api/client';
import type {
  ChatMessageResponse,
  ConversationResponse,
  MessageResponse,
  ProductResponse,
} from '../types';

type RecommendedMap = Record<number, ProductResponse[]>;

export function useChat() {
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
      if (recommendedProducts.length > 0) {
        setRecommendedMap((prev) => ({
          ...prev,
          [assistantMessage.id]: recommendedProducts,
        }));
        setRecommendedIds(recommendedProducts.map((p) => p.id));
      }
    } catch {
      setError('El asesor no está disponible, intenta nuevamente.');
    } finally {
      setSending(false);
    }
  };

  const resetConversation = async () => {
    try {
      await client.delete('/api/v1/conversations/current');
      setMessages([]);
      setRecommendedMap({});
      setRecommendedIds([]);
      setConversationId(null);
      initialized.current = false;
      const res = await client.get<ConversationResponse>('/api/v1/conversations/current');
      setConversationId(res.data.id);
      setMessages(res.data.messages);
    } catch {
      setError('No se pudo reiniciar la conversación.');
    }
  };

  return {
    messages,
    recommendedMap,
    recommendedIds,
    loading,
    sending,
    error,
    conversationId,
    sendMessage,
    resetConversation,
  };
}
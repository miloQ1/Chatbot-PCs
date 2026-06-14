import { useEffect, useRef } from "react";
import type { ProductResponse } from "../../types";
import { useChat } from "../../hooks/useChat";
import { MessageBubble } from "./MessageBubble";
import { ChatInput } from "./ChatInput";
import { Spinner } from "../ui/Spinner";

interface Props {
  onRecommend: (ids: number[], products: ProductResponse[]) => void;
}

export function ChatPanel({ onRecommend }: Props) {
  const chat = useChat() as any;
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [chat.messages]);

  useEffect(() => {
    if (chat.recommendedIds.length > 0) {
      onRecommend(chat.recommendedIds, chat.recommendedProducts);
    }
  }, [chat.recommendedIds]);

  return (
    <div style={{ flex: 1, height: "100vh", display: "flex", flexDirection: "column", background: "#fff" }}>

      <div style={{ padding: "20px", borderBottom: "1px solid #e2e8f0", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <div>
          <h2 style={{ margin: 0, fontSize: "18px", fontWeight: 700, color: "#111827" }}>Asesor de PC</h2>
          <p style={{ margin: 0, fontSize: "13px", color: "#6b7280" }}>Cuentame que necesitas y te ayudo a armar tu PC ideal.</p>
        </div>
        <button onClick={chat.resetConversation} style={{ background: "none", border: "1px solid #e2e8f0", borderRadius: "8px", padding: "6px 12px", fontSize: "13px", color: "#6b7280", cursor: "pointer" }}>
          Nueva conversacion
        </button>
      </div>

      <div style={{ flex: 1, overflowY: "auto", padding: "20px" }}>
        {chat.loading && (
          <div style={{ display: "flex", justifyContent: "center", marginTop: "40px" }}>
            <Spinner />
          </div>
        )}
        {!chat.loading && chat.messages.length === 0 && (
          <div style={{ textAlign: "center", color: "#9ca3af", marginTop: "60px", fontSize: "14px" }}>
            Escribe un mensaje para comenzar
          </div>
        )}
        {chat.messages.map((message: any) => (
          <MessageBubble key={message.id} message={message} recommendedProducts={chat.recommendedMap?.[message.id] ?? []} />
        ))}
        {chat.sending && (
          <div style={{ display: "flex", justifyContent: "flex-start", marginBottom: "16px" }}>
            <div style={{ padding: "10px 14px", borderRadius: "18px 18px 18px 4px", background: "#f1f5f9", fontSize: "14px", color: "#6b7280" }}>
              Buscando en el catalogo...
            </div>
          </div>
        )}
        {chat.error && (
          <div style={{ color: "#ef4444", fontSize: "13px", textAlign: "center", margin: "8px 0" }}>
            {chat.error}
          </div>
        )}
        <div ref={bottomRef} />
      </div>

      <ChatInput onSend={chat.sendMessage} disabled={chat.sending || chat.loading} />
    </div>
  );
}
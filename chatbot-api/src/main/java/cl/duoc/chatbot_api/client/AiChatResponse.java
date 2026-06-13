package cl.duoc.chatbot_api.client;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiChatResponse {
 
    /** Mensaje generado por el asistente (texto y/o tool calls). */
    private final AiMessage message;
}

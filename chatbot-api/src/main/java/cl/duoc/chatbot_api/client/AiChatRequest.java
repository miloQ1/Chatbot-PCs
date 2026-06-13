package cl.duoc.chatbot_api.client;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiChatRequest {
 
    private final List<AiMessage> messages;
 
    /** Funciones disponibles para esta llamada. Null/vacio = sin function calling. */
    private final List<AiTool> tools;
}
 

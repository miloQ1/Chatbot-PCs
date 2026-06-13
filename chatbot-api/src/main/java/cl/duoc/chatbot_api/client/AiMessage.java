package cl.duoc.chatbot_api.client;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * Representacion neutral (independiente del proveedor) de un mensaje dentro
 * de una conversacion con la IA. GroqClient traduce esto al formato especifico
 * de Groq (compatible con OpenAI) y viceversa.
 */
@Getter
@Builder
public class AiMessage {
 
    /** "system" | "user" | "assistant" | "tool" */
    private final String role;
 
    /** Contenido en texto. Puede ser null para mensajes "assistant" que solo traen tool calls. */
    private final String content;
 
    /** Tool calls solicitados por el asistente (solo aplica a role="assistant"). */
    private final List<AiToolCall> toolCalls;
 
    /** id del tool call que esta respondiendo (solo aplica a role="tool"). */
    private final String toolCallId;
 
    public static AiMessage system(String content) {
        return AiMessage.builder().role("system").content(content).build();
    }
 
    public static AiMessage user(String content) {
        return AiMessage.builder().role("user").content(content).build();
    }
 
    public static AiMessage assistant(String content) {
        return AiMessage.builder().role("assistant").content(content).build();
    }
 
    public static AiMessage toolResult(String toolCallId, String content) {
        return AiMessage.builder().role("tool").toolCallId(toolCallId).content(content).build();
    }
 
    public boolean hasToolCalls() {
        return toolCalls != null && !toolCalls.isEmpty();
    }
}

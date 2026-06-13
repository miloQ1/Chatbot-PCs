package cl.duoc.chatbot_api.client;

import lombok.Builder;
import lombok.Getter;

/**
 * Solicitud de la IA para ejecutar una funcion (function calling).
 */
@Getter
@Builder
public class AiToolCall {
 
    /** Identificador del tool call, usado para asociar la respuesta (AiMessage.toolResult). */
    private final String id;
 
    /** Nombre de la funcion solicitada, ej. "buscar_productos". */
    private final String functionName;
 
    /** Argumentos en formato JSON (string), tal como los entrega el modelo. */
    private final String argumentsJson;
}
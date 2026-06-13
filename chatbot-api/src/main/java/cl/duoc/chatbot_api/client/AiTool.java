package cl.duoc.chatbot_api.client;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

/**
 * Definicion de una funcion (tool) que la IA puede decidir invocar.
 */
@Getter
@Builder
public class AiTool {
 
    private final String name;
 
    private final String description;
 
    /** JSON Schema describiendo los parametros de la funcion. */
    private final Map<String, Object> parametersSchema;
}
 
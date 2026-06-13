package cl.duoc.chatbot_api.dtos.response;

import java.util.List;

/**
 * Respuesta de POST /api/v1/conversations/{id}/messages: el mensaje del usuario
 * (ya persistido), la respuesta del asistente, y los productos recomendados
 * (puede venir vacio si el bot no uso buscar_productos en esta respuesta).
 */
public record ChatMessageResponse(
        MessageResponse userMessage,
        MessageResponse assistantMessage,
        List<ProductResponse> recommendedProducts
) {
}
 

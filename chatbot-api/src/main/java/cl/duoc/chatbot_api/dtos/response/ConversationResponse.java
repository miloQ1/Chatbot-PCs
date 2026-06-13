package cl.duoc.chatbot_api.dtos.response;

import java.time.Instant;
import java.util.List;

/**
 * Conversacion activa de una sesion, con su historial completo de mensajes.
 * Devuelto por GET /api/v1/conversations/current.
 */
public record ConversationResponse(
        Long id,
        String title,
        Instant createdAt,
        Instant updatedAt,
        List<MessageResponse> messages
) {
}

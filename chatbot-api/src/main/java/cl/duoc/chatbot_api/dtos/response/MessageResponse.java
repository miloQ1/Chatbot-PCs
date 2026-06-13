package cl.duoc.chatbot_api.dtos.response;

import java.time.Instant;

import cl.duoc.chatbot_api.model.MessageRole;

/**
 * Representacion de un mensaje (de usuario o del asistente) devuelta por la API.
 */
public record MessageResponse(
        Long id,
        MessageRole role,
        String content,
        Instant createdAt
) {
}
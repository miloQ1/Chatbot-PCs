package cl.duoc.chatbot_api.dtos.response;

import java.time.Instant;

/**
 * Formato estandar de respuesta de error para toda la API.
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path);
    }
}
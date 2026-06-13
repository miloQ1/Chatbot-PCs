package cl.duoc.chatbot_api.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Mensaje enviado por el usuario al asesor.
 */
public record ChatRequest(
 
        @NotBlank(message = "content no puede estar vacio")
        @Size(max = 2000, message = "content no puede superar los 2000 caracteres")
        String content
) {
}
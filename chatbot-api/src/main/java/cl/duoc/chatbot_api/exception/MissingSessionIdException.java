package cl.duoc.chatbot_api.exception;

/**
 * Se lanza cuando una request a un endpoint que requiere identificar la sesion
 * (conversaciones/chat) no incluye el header X-Session-Id. Traducida a HTTP 400
 * por el GlobalExceptionHandler.
 */
public class MissingSessionIdException extends RuntimeException {

    public MissingSessionIdException() {
        super("El header X-Session-Id es obligatorio.");
    }
}

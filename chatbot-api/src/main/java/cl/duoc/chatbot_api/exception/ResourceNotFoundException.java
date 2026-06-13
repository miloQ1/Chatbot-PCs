package cl.duoc.chatbot_api.exception;

/**
 * Se lanza cuando un recurso solicitado (conversacion, producto, etc.) no existe
 * o no pertenece a la sesion actual. El GlobalExceptionHandler la traduce a HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

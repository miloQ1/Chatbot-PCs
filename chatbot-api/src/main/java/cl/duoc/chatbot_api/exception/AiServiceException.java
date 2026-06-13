package cl.duoc.chatbot_api.exception;

/**
 * Se lanza cuando la llamada a Groq falla por timeout, rate limit (429) u otro
 * problema del proveedor de IA. El GlobalExceptionHandler la traduce a HTTP 503,
 * cumpliendo RNF7 (fallback de mensaje amigable al usuario).
 */
public class AiServiceException extends RuntimeException {

    public AiServiceException(String message) {
        super(message);
    }

    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
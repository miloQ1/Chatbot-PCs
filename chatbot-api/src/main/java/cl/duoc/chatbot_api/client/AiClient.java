package cl.duoc.chatbot_api.client;

/**
 * Abstraccion del proveedor de IA (RNF8).
 *
 * El resto de la aplicacion (ChatService) solo conoce esta interfaz y los DTOs
 * neutrales (AiMessage, AiTool, etc.). La implementacion actual es GroqClient;
 * cambiar de proveedor en el futuro solo requiere una nueva implementacion de
 * AiClient, sin tocar ChatService.
 */
public interface AiClient {
 
    AiChatResponse chat(AiChatRequest request);
}
 

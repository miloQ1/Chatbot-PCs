package cl.duoc.chatbot_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.chatbot_api.dtos.request.ChatRequest;
import cl.duoc.chatbot_api.dtos.response.ChatMessageResponse;
import cl.duoc.chatbot_api.dtos.response.ConversationResponse;
import cl.duoc.chatbot_api.exception.MissingSessionIdException;
import cl.duoc.chatbot_api.mapper.ConversationMapper;
import cl.duoc.chatbot_api.model.Conversation;
import cl.duoc.chatbot_api.service.ChatService;
import cl.duoc.chatbot_api.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Endpoints de conversacion y chat (RF1-RF4, RF8).
 * Todas las rutas requieren el header X-Session-Id (ver convencion general del
 * documento de diseno).
 */
@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {
 
    private static final String SESSION_HEADER = "X-Session-Id";
 
    private final ConversationService conversationService;
    private final ChatService chatService;
 
    /**
     * Recupera la conversacion activa de la sesion, o la crea si no existe (RF2).
     */
    @GetMapping("/current")
    public ResponseEntity<ConversationResponse> getCurrent(
            @RequestHeader(value = SESSION_HEADER, required = false) String sessionId) {
 
        requireSessionId(sessionId);
 
        boolean existed = conversationService.existsForSession(sessionId);
        Conversation conversation = conversationService.getOrCreateCurrent(sessionId);
        ConversationResponse body = ConversationMapper.toResponse(conversation);
 
        return existed
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
 
    /**
     * Envia un mensaje del usuario y retorna la respuesta del asesor (RF3, RF8).
     */
    @PostMapping("/{id}/messages")
    public ChatMessageResponse sendMessage(
            @PathVariable Long id,
            @RequestHeader(value = SESSION_HEADER, required = false) String sessionId,
            @Valid @RequestBody ChatRequest request) {
 
        requireSessionId(sessionId);
        return chatService.sendMessage(id, sessionId, request.content());
    }
 
    private void requireSessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new MissingSessionIdException();
        }
    }

    @DeleteMapping("/current")
    public ResponseEntity<Void> deleteCurrent(
        @RequestHeader(value = SESSION_HEADER, required = false) String sessionId) {
    requireSessionId(sessionId);
    conversationService.deleteCurrent(sessionId);
    return ResponseEntity.noContent().build();
}
}
 
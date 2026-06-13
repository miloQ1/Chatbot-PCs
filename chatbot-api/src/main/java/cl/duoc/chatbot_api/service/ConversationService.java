package cl.duoc.chatbot_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.chatbot_api.model.Conversation;
import cl.duoc.chatbot_api.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationService {
 
    private static final String DEFAULT_TITLE = "Nueva conversación";
 
    private final ConversationRepository conversationRepository;
 
    /**
     * Indica si ya existe una conversacion para esta sesion (usado por el
     * controller para decidir si responder 200 o 201).
     */
    @Transactional(readOnly = true)
    public boolean existsForSession(String sessionId) {
        return conversationRepository.findBySessionId(sessionId).isPresent();
    }
 
    /**
     * Recupera la conversacion activa de la sesion, o la crea si no existe (RF2).
     */
    @Transactional
    public Conversation getOrCreateCurrent(String sessionId) {
        return conversationRepository.findBySessionId(sessionId)
                .orElseGet(() -> conversationRepository.save(
                        Conversation.builder()
                                .sessionId(sessionId)
                                .title(DEFAULT_TITLE)
                                .build()
                ));
    }
}
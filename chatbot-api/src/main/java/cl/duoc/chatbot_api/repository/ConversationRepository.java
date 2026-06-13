package cl.duoc.chatbot_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.chatbot_api.model.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
 
    /**
     * Busca la conversacion asociada a una sesion (ver GET /api/v1/conversations/current).
     */
    Optional<Conversation> findBySessionId(String sessionId);
}

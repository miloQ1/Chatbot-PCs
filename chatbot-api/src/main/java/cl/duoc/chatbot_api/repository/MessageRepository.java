package cl.duoc.chatbot_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.chatbot_api.model.Message;

/**
 * En el flujo normal los mensajes se persisten a traves de la relacion
 * Conversation -> messages (cascade ALL). Este repositorio se deja disponible
 * para consultas puntuales (ej. paginar mensajes de una conversacion grande
 * en el futuro).
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
}
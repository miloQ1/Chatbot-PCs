package cl.duoc.chatbot_api.mapper;

import cl.duoc.chatbot_api.dtos.response.ConversationResponse;
import cl.duoc.chatbot_api.dtos.response.MessageResponse;
import cl.duoc.chatbot_api.model.Conversation;
import cl.duoc.chatbot_api.model.Message;

public final class ConversationMapper {
 
    private ConversationMapper() {
    }
 
    public static MessageResponse toMessageResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
 
    public static ConversationResponse toResponse(Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt(),
                conversation.getMessages().stream()
                        .map(ConversationMapper::toMessageResponse)
                        .toList()
        );
    }
}
 

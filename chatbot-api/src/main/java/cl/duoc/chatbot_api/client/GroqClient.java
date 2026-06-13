package cl.duoc.chatbot_api.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cl.duoc.chatbot_api.exception.AiServiceException;
import lombok.RequiredArgsConstructor;

/**
 * Implementacion de AiClient para Groq (https://console.groq.com), cuya API
 * es compatible con el formato de chat completions de OpenAI, incluyendo
 * function calling (tools / tool_calls).
 */
@Component
@RequiredArgsConstructor
public class GroqClient implements AiClient {
 
    private final RestClient groqRestClient;
    private final ObjectMapper objectMapper;
 
    @Value("${groq.model}")
    private String model;
 
    @Override
    public AiChatResponse chat(AiChatRequest request) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("model", model);
        body.set("messages", toMessagesJson(request.getMessages()));
 
        if (request.getTools() != null && !request.getTools().isEmpty()) {
            body.set("tools", toToolsJson(request.getTools()));
            body.put("tool_choice", "auto");
        }
 
        try {
            JsonNode response = groqRestClient.post()
                    .uri("/chat/completions")
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
 
            return parseResponse(response);
        } catch (RestClientException ex) {
            // Cubre timeouts, errores de red y respuestas de error de Groq
            // (ej. 429 por rate limit). Se traduce a 503 (RNF7).
            throw new AiServiceException("Error al comunicarse con el proveedor de IA (Groq)", ex);
        }
    }
 
    private ArrayNode toMessagesJson(List<AiMessage> messages) {
        ArrayNode array = objectMapper.createArrayNode();
 
        for (AiMessage message : messages) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("role", message.getRole());
 
            if (message.getContent() != null) {
                node.put("content", message.getContent());
            } else {
                node.putNull("content");
            }
 
            if (message.getToolCallId() != null) {
                node.put("tool_call_id", message.getToolCallId());
            }
 
            if (message.hasToolCalls()) {
                node.set("tool_calls", toToolCallsJson(message.getToolCalls()));
            }
 
            array.add(node);
        }
 
        return array;
    }
 
    private ArrayNode toToolCallsJson(List<AiToolCall> toolCalls) {
        ArrayNode array = objectMapper.createArrayNode();
 
        for (AiToolCall toolCall : toolCalls) {
            ObjectNode toolCallNode = objectMapper.createObjectNode();
            toolCallNode.put("id", toolCall.getId());
            toolCallNode.put("type", "function");
 
            ObjectNode functionNode = objectMapper.createObjectNode();
            functionNode.put("name", toolCall.getFunctionName());
            functionNode.put("arguments", toolCall.getArgumentsJson());
            toolCallNode.set("function", functionNode);
 
            array.add(toolCallNode);
        }
 
        return array;
    }
 
    private ArrayNode toToolsJson(List<AiTool> tools) {
        ArrayNode array = objectMapper.createArrayNode();
 
        for (AiTool tool : tools) {
            ObjectNode toolNode = objectMapper.createObjectNode();
            toolNode.put("type", "function");
 
            ObjectNode functionNode = objectMapper.createObjectNode();
            functionNode.put("name", tool.getName());
            functionNode.put("description", tool.getDescription());
            functionNode.set("parameters", objectMapper.valueToTree(tool.getParametersSchema()));
            toolNode.set("function", functionNode);
 
            array.add(toolNode);
        }
 
        return array;
    }
 
    private AiChatResponse parseResponse(JsonNode response) {
        JsonNode messageNode = response.path("choices").path(0).path("message");
 
        String role = messageNode.path("role").asText("assistant");
        String content = messageNode.hasNonNull("content") ? messageNode.get("content").asText() : null;
 
        List<AiToolCall> toolCalls = new ArrayList<>();
        JsonNode toolCallsNode = messageNode.path("tool_calls");
        if (toolCallsNode.isArray()) {
            for (JsonNode toolCallNode : toolCallsNode) {
                toolCalls.add(AiToolCall.builder()
                        .id(toolCallNode.path("id").asText())
                        .functionName(toolCallNode.path("function").path("name").asText())
                        .argumentsJson(toolCallNode.path("function").path("arguments").asText())
                        .build());
            }
        }
 
        AiMessage message = AiMessage.builder()
                .role(role)
                .content(content)
                .toolCalls(toolCalls.isEmpty() ? null : toolCalls)
                .build();
 
        return AiChatResponse.builder().message(message).build();
    }
}
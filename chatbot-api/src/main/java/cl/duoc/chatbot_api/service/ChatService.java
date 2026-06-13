package cl.duoc.chatbot_api.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.chatbot_api.client.AiChatRequest;
import cl.duoc.chatbot_api.client.AiChatResponse;
import cl.duoc.chatbot_api.client.AiClient;
import cl.duoc.chatbot_api.client.AiMessage;
import cl.duoc.chatbot_api.client.AiTool;
import cl.duoc.chatbot_api.client.AiToolCall;
import cl.duoc.chatbot_api.dtos.response.ChatMessageResponse;
import cl.duoc.chatbot_api.dtos.response.ProductResponse;
import cl.duoc.chatbot_api.exception.AiServiceException;
import cl.duoc.chatbot_api.exception.ResourceNotFoundException;
import cl.duoc.chatbot_api.mapper.ConversationMapper;
import cl.duoc.chatbot_api.model.Conversation;
import cl.duoc.chatbot_api.model.Message;
import cl.duoc.chatbot_api.model.MessageRole;
import cl.duoc.chatbot_api.model.ProductCategory;
import cl.duoc.chatbot_api.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;

/**
 * Orquesta la conversacion con el asesor de PC: construye el contexto para la IA,
 * ejecuta el flujo de function calling (buscar_productos) cuando corresponde,
 * y persiste el historial de mensajes (RF3, RF4, RF8 y seccion 7 del documento
 * de diseno).
 */
@Service
@RequiredArgsConstructor
public class ChatService {
 
    /**
     * Limite de "rondas" de function calling por mensaje, como salvaguarda ante
     * un modelo que pidiera tool calls indefinidamente.
     */
    private static final int MAX_TOOL_ITERATIONS = 3;
 
    private static final String SYSTEM_PROMPT = """
            Eres un asesor experto en armado de PCs y hardware de computadores.
 
            Tu unico ambito de conocimiento es: componentes de PC (CPU, GPU, RAM,
            placas madre, fuentes de poder, almacenamiento, gabinetes, refrigeracion),
            compatibilidad entre componentes, armado de equipos segun presupuesto y
            uso, y comparacion entre productos.
 
            Reglas:
            - Si necesitas recomendar o comparar productos especificos, usa la funcion
              buscar_productos para obtener datos reales del catalogo antes de
              responder. Nunca inventes productos, marcas, precios o
              especificaciones que no esten en los resultados de esa funcion.
            - Si buscar_productos no devuelve resultados, indicalo honestamente en
              vez de inventar una recomendacion.
            - Si el usuario pregunta algo fuera del ambito de hardware de PC,
              indica amablemente que no es tu especialidad y que solo puedes ayudar
              con temas de armado de PC y componentes.
            - Responde en español, de forma clara y concisa.
            """;
 
    private final ConversationRepository conversationRepository;
    private final ProductService productService;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper;
 
    /**
     * Procesa un nuevo mensaje del usuario dentro de una conversacion (RF3).
     */
    @Transactional
    public ChatMessageResponse sendMessage(Long conversationId, String sessionId, String content) {
        Conversation conversation = getConversationForSession(conversationId, sessionId);
 
        Message userMessage = Message.builder()
                .role(MessageRole.USER)
                .content(content)
                .build();
        conversation.addMessage(userMessage);
 
        List<AiMessage> aiMessages = buildAiMessages(conversation);
        List<ProductResponse> recommendedProducts = new ArrayList<>();
 
        AiChatResponse response = aiClient.chat(AiChatRequest.builder()
                .messages(aiMessages)
                .tools(List.of(buscarProductosTool()))
                .build());
 
        int iterations = 0;
        while (response.getMessage().hasToolCalls() && iterations < MAX_TOOL_ITERATIONS) {
            aiMessages.add(response.getMessage());
 
            for (AiToolCall toolCall : response.getMessage().getToolCalls()) {
                List<ProductResponse> found = executeToolCall(toolCall);
                recommendedProducts.addAll(found);
                aiMessages.add(AiMessage.toolResult(toolCall.getId(), toToolResultJson(found)));
            }
 
            response = aiClient.chat(AiChatRequest.builder()
                    .messages(aiMessages)
                    .build());
            iterations++;
        }
 
        Message assistantMessage = Message.builder()
                .role(MessageRole.ASSISTANT)
                .content(response.getMessage().getContent())
                .build();
        conversation.addMessage(assistantMessage);
 
        conversationRepository.save(conversation);
 
        return new ChatMessageResponse(
                ConversationMapper.toMessageResponse(userMessage),
                ConversationMapper.toMessageResponse(assistantMessage),
                deduplicateById(recommendedProducts)
        );
    }
 
    /**
     * Verifica que la conversacion exista y pertenezca a la sesion (RF1).
     * Si no, se trata como "no encontrada" (404) para no filtrar informacion
     * sobre conversaciones de otras sesiones.
     */
    private Conversation getConversationForSession(Long conversationId, String sessionId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversacion no encontrada: id=" + conversationId));
 
        if (!conversation.getSessionId().equals(sessionId)) {
            throw new ResourceNotFoundException("Conversacion no encontrada: id=" + conversationId);
        }
 
        return conversation;
    }
 
    /**
     * Construye el arreglo de mensajes para la IA: system prompt + historial completo
     * (incluyendo el mensaje del usuario recien agregado a la conversacion).
     */
    private List<AiMessage> buildAiMessages(Conversation conversation) {
        List<AiMessage> messages = new ArrayList<>();
        messages.add(AiMessage.system(SYSTEM_PROMPT));
 
        for (Message message : conversation.getMessages()) {
            messages.add(message.getRole() == MessageRole.USER
                    ? AiMessage.user(message.getContent())
                    : AiMessage.assistant(message.getContent()));
        }
 
        return messages;
    }
 
    /**
     * Definicion de la funcion buscar_productos (JSON Schema de sus parametros),
     * todos opcionales.
     */
    private AiTool buscarProductosTool() {
        Map<String, Object> categoria = Map.of(
                "type", "string",
                "enum", Arrays.stream(ProductCategory.values()).map(Enum::name).toList(),
                "description", "Categoria del componente de hardware"
        );
        Map<String, Object> precioMax = Map.of(
                "type", "number",
                "description", "Precio maximo en pesos chilenos (CLP)"
        );
        Map<String, Object> marca = Map.of(
                "type", "string",
                "description", "Marca del producto, ej. AMD, Intel, NVIDIA"
        );
        Map<String, Object> useCase = Map.of(
                "type", "string",
                "description", "Uso previsto del componente, ej. gaming, edicion-video, oficina"
        );
 
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("categoria", categoria);
        properties.put("precioMax", precioMax);
        properties.put("marca", marca);
        properties.put("useCase", useCase);
 
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
 
        return AiTool.builder()
                .name("buscar_productos")
                .description("Busca productos del catalogo de hardware segun categoria, precio "
                        + "maximo, marca y/o uso previsto. Usar siempre antes de recomendar "
                        + "o comparar productos especificos.")
                .parametersSchema(schema)
                .build();
    }
 
    /**
     * Ejecuta un tool call solicitado por la IA contra el catalogo real (paso 4
     * del flujo de function calling). Si los argumentos vienen mal formados o la
     * categoria no es valida, retorna una lista vacia en vez de fallar todo el
     * flujo.
     */
    private List<ProductResponse> executeToolCall(AiToolCall toolCall) {
        if (!"buscar_productos".equals(toolCall.getFunctionName())) {
            return List.of();
        }
 
        try {
            JsonNode args = objectMapper.readTree(toolCall.getArgumentsJson());
 
            ProductCategory category = args.hasNonNull("categoria")
                    ? ProductCategory.valueOf(args.get("categoria").asText().toUpperCase())
                    : null;
 
            BigDecimal priceMax = args.hasNonNull("precioMax")
                    ? BigDecimal.valueOf(args.get("precioMax").asDouble())
                    : null;
 
            String brand = args.hasNonNull("marca") ? args.get("marca").asText() : null;
            String useCase = args.hasNonNull("useCase") ? args.get("useCase").asText() : null;
 
            return productService.search(category, priceMax, brand, useCase);
        } catch (Exception e) {
            return List.of();
        }
    }
 
    /**
     * Serializa los productos encontrados para enviarlos a la IA como tool result,
     * omitiendo imageUrl y useCaseTags (no aportan a la redaccion de la respuesta
     * y solo consumirian tokens innecesariamente - ver Docu-Impacto-Imagenes).
     */
    private String toToolResultJson(List<ProductResponse> products) {
        List<Map<String, Object>> simplified = products.stream()
                .map(p -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", p.id());
                    item.put("category", p.category());
                    item.put("brand", p.brand());
                    item.put("name", p.name());
                    item.put("priceClp", p.priceClp());
                    item.put("specs", p.specs());
                    return item;
                })
                .toList();
 
        try {
            return objectMapper.writeValueAsString(simplified);
        } catch (Exception e) {
            throw new AiServiceException("Error al preparar los resultados del catalogo para la IA", e);
        }
    }
 
    /**
     * El bot puede llamar buscar_productos varias veces (o devolver el mismo
     * producto en distintas llamadas); se deduplica por id antes de devolver
     * recommendedProducts, preservando el orden de primera aparicion.
     */
    private List<ProductResponse> deduplicateById(List<ProductResponse> products) {
        Map<Long, ProductResponse> byId = new LinkedHashMap<>();
        for (ProductResponse product : products) {
            byId.put(product.id(), product);
        }
        return new ArrayList<>(byId.values());
    }
}
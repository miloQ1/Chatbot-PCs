


backend/
├── pom.xml
├── Dockerfile
├── .dockerignore
├── .env.example
└── src/main/
    ├── resources/
    │   └── application.properties
    └── java/cl/miloq/pcadvisor/
        ├── PcAdvisorApplication.java
        │
        ├── client/                          ← NUEVO (paso 5)
        │   ├── AiClient.java                 → interfaz (RNF8)
        │   ├── AiChatRequest.java
        │   ├── AiChatResponse.java
        │   ├── AiMessage.java
        │   ├── AiTool.java
        │   ├── AiToolCall.java
        │   └── GroqClient.java               → implementacion para Groq
        │
        ├── config/
        │   ├── CorsConfig.java
        │   ├── OpenApiConfig.java
        │   └── GroqClientConfig.java          ← NUEVO (RestClient hacia Groq)
        │
        ├── controller/
        │   ├── ProductController.java
        │   └── ConversationController.java    ← NUEVO (chat + conversacion)
        │
        ├── service/
        │   ├── ProductService.java            ← MODIFICADO (+ search con useCase)
        │   ├── ConversationService.java       ← NUEVO
        │   └── ChatService.java               ← NUEVO (function calling)
        │
        ├── repository/
        │   ├── ConversationRepository.java
        │   ├── MessageRepository.java
        │   └── ProductRepository.java
        │
        ├── model/
        │   ├── Conversation.java
        │   ├── Message.java
        │   ├── MessageRole.java
        │   ├── Product.java
        │   └── ProductCategory.java
        │
        ├── dto/
        │   ├── ErrorResponse.java
        │   ├── ProductRequest.java
        │   ├── ProductResponse.java
        │   ├── ChatRequest.java               ← NUEVO
        │   ├── MessageResponse.java           ← NUEVO
        │   ├── ConversationResponse.java      ← NUEVO
        │   └── ChatMessageResponse.java       ← NUEVO
        │
        ├── mapper/
        │   ├── ProductMapper.java
        │   └── ConversationMapper.java        ← NUEVO
        │
        └── exception/
            ├── AiServiceException.java
            ├── GlobalExceptionHandler.java
            ├── MissingSessionIdException.java
            └── ResourceNotFoundException.java
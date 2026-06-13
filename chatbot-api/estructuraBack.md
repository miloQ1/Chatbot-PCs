# Estructura del Backend — pc-advisor

```
backend/
├── pom.xml
├── .env.example
└── src/main/
    ├── resources/
    │   └── application.properties
    └── java/cl/miloq/pcadvisor/
        ├── PcAdvisorApplication.java     → clase principal (main)
        │
        ├── config/
        │   ├── CorsConfig.java            → orígenes permitidos (web/mobile)
        │   └── OpenApiConfig.java         → configuración de Swagger
        │
        ├── controller/
        │   └── ProductController.java     → endpoints REST /api/v1/products
        │
        ├── service/
        │   └── ProductService.java        → lógica de negocio del catálogo
        │
        ├── repository/
        │   ├── ConversationRepository.java → acceso a datos: conversations
        │   ├── MessageRepository.java       → acceso a datos: messages
        │   └── ProductRepository.java       → acceso a datos: products (+ @Query search)
        │
        ├── model/                          → entidades JPA (tablas de la BD)
        │   ├── Conversation.java
        │   ├── Message.java
        │   ├── MessageRole.java            (enum: USER, ASSISTANT)
        │   ├── Product.java
        │   └── ProductCategory.java        (enum: CPU, GPU, RAM, etc.)
        │
        ├── dto/                            → objetos de entrada/salida de la API
        │   ├── ErrorResponse.java
        │   ├── ProductRequest.java
        │   └── ProductResponse.java
        │
        ├── mapper/
        │   └── ProductMapper.java          → traduce Product <-> DTOs
        │
        ├── exception/
        │   ├── AiServiceException.java       (-> 503)
        │   ├── GlobalExceptionHandler.java    → manejo centralizado de errores
        │   ├── MissingSessionIdException.java (-> 400)
        │   └── ResourceNotFoundException.java (-> 404)
        │
        └── client/                          → (vacío, próximo paso: GroqClient)
```
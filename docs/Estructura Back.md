
backend/
├── .env.example
├── pom.xml
└── src/main/
    ├── java/cl/miloq/pcadvisor/
    │   ├── PcAdvisorApplication.java
    │   ├── config/
    │   │   ├── CorsConfig.java
    │   │   └── OpenApiConfig.java
    │   ├── dto/
    │   │   └── ErrorResponse.java
    │   ├── exception/
    │   │   ├── AiServiceException.java
    │   │   ├── GlobalExceptionHandler.java
    │   │   ├── MissingSessionIdException.java
    │   │   └── ResourceNotFoundException.java
    │   ├── controller/   (vacío)
    │   ├── service/       (vacío)
    │   ├── repository/    (vacío)
    │   ├── model/          (vacío)
    │   ├── mapper/          (vacío)
    │   └── client/           (vacío)
    └── resources/
        └── application.yml

pc-advisor/docker-compose.yml   (MySQL local)

model/
├── Conversation.java     (id, sessionId, title, timestamps, OneToMany messages)
├── Message.java          (id, conversation, role, content, createdAt)
├── MessageRole.java       (enum: USER, ASSISTANT)
├── Product.java            (id, category, brand, name, specs[JSON], useCaseTags[JSON], priceClp, imageUrl, createdAt)
└── ProductCategory.java    (enum: CPU, GPU, RAM, MOTHERBOARD, PSU, STORAGE, CASE, COOLER)

repository/
├── ConversationRepository.java   (+ findBySessionId)
├── MessageRepository.java
├── ProductRepository.java         (+ JpaSpecificationExecutor)
└── ProductSpecifications.java     (filtros: category, priceMax, brand)



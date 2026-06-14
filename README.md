# PcConsultas 🖥️

Chatbot asesor de armado y recomendación de hardware para PC. El usuario describe su necesidad (uso, presupuesto, compatibilidad entre componentes) y recibe recomendaciones basadas en un catálogo real de productos, potenciadas por IA con **function calling** — la IA consulta el catálogo antes de responder, nunca inventa productos ni precios.

![Demo PcConsultas](assets/demoChatBot.gif)

---

## ¿Cómo funciona?

1. El usuario escribe su necesidad en lenguaje natural ("quiero armar un PC para gaming con $500.000")
2. El backend envía el mensaje a **Groq** (Llama 3.3 70B) junto con la definición de la función `buscar_productos()`
3. La IA decide llamar a `buscar_productos(categoria='GPU', precioMax=500000)` con los parámetros que extrajo del mensaje
4. El backend consulta MySQL y devuelve productos reales del catálogo a la IA
5. La IA genera una respuesta en lenguaje natural basada en esos productos reales
6. El frontend muestra la respuesta + tarjetas de productos con enlace directo a Google Shopping

Sin login — cada sesión se identifica con un UUID generado en el navegador (`X-Session-Id`), persistido en `localStorage`.

---

## Stack

| Capa | Tecnología |
|---|---|
| Backend | Java 17 · Spring Boot 3 · Spring Data JPA · Bean Validation · MySQL |
| Web | React 18 · TypeScript · Vite · Axios |
| Mobile | Ionic React 8 · Capacitor 7 · APK Android |
| IA | Groq API — Llama 3.3 70B (free tier, sin costo) |
| Infraestructura | Docker · Docker Compose · Swagger/OpenAPI |

---

## Estructura del repositorio

    pc-advisor/
    ├── backend/        # API REST Spring Boot
    │   ├── src/main/java/cl/duoc/chatbot_api/
    │   │   ├── controller/     # Endpoints REST
    │   │   ├── service/        # Lógica de negocio (ChatService, ProductService)
    │   │   ├── repository/     # Acceso a datos (JPA)
    │   │   ├── model/          # Entidades: Conversation, Message, Product
    │   │   ├── dto/            # Contratos de la API (request/response)
    │   │   ├── mapper/         # Traducción entre entidades y DTOs
    │   │   ├── client/         # AiClient (interfaz) + GroqClient (implementación)
    │   │   ├── exception/      # GlobalExceptionHandler + excepciones custom
    │   │   ├── config/         # CorsConfig, OpenApiConfig, GroqClientConfig
    │   │   └── seed/           # DataSeeder (50 productos al primer arranque)
    │   └── Dockerfile
    ├── web/            # Cliente web React + Vite
    ├── mobile/         # Cliente mobile Ionic + Capacitor
    ├── docs/           # Documentación de diseño
    └── docker-compose.yml

---

## Modelo de datos

| Tabla | Campos principales |
|---|---|
| `conversations` | id, session_id, title, created_at, updated_at |
| `messages` | id, conversation_id (FK), role (USER/ASSISTANT), content, created_at |
| `products` | id, category (ENUM), brand, name, specs (JSON), use_case_tags (JSON), price_clp, image_url, created_at |

---

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/v1/conversations/current` | Recupera o crea la conversación activa de la sesión |
| `POST` | `/api/v1/conversations/{id}/messages` | Envía mensaje → respuesta IA + productos recomendados |
| `DELETE` | `/api/v1/conversations/current` | Nueva conversación (borra historial) |
| `GET` | `/api/v1/products` | Catálogo filtrable por `category`, `priceMax`, `brand` |
| `POST/PUT/DELETE` | `/api/v1/products` | CRUD del catálogo |

Todos los endpoints requieren el header `X-Session-Id: <uuid>`.

---

## Requisitos previos

- Java 17+
- Node.js 18+
- Docker y Docker Compose
- API key de [Groq](https://console.groq.com/) (gratis, sin tarjeta de crédito)

---

## Levantar en desarrollo

**1. Clona el repositorio**

    git clone https://github.com/miloQ1/Chatbot-PCs.git
    cd Chatbot-PCs

**2. Configura las variables de entorno**

    cp backend/.env.example .env

Edita `.env` y completa `GROQ_API_KEY` y `DB_PASSWORD`.

**3. Levanta el backend + base de datos**

    docker compose up -d --build

Al levantar por primera vez, el `DataSeeder` carga automáticamente 50 productos de hardware (CPU, GPU, RAM, etc.) con imágenes obtenidas de Wikimedia Commons. Puede tardar ~60 segundos en el primer arranque.

**4. Levanta el frontend web**

    cd web
    npm install
    npm run dev

Disponible en `http://localhost:5173`

**5. Levanta la app mobile**

    cd mobile
    npm install
    npm run dev

Disponible en `http://localhost:8100`

---

## Variables de entorno

| Variable | Descripción |
|---|---|
| `GROQ_API_KEY` | API key de Groq (obtener en console.groq.com) |
| `DB_PASSWORD` | Contraseña de MySQL |
| `DB_HOST` | Host de la base de datos (default: localhost) |
| `DB_PORT` | Puerto de MySQL (default: 3306) |
| `DB_NAME` | Nombre de la base de datos (default: pcadvisor) |
| `DB_USERNAME` | Usuario de MySQL (default: root) |

---

## Documentación

- Swagger UI disponible en `http://localhost:8080/swagger-ui.html`
- Documentación de diseño en [`/docs`](./docs/) — requisitos, modelo de datos, endpoints, flujo de function calling e impacto de imágenes

---

## Buenas prácticas aplicadas

- **Arquitectura en capas** — Controller → Service → Repository → Model, responsabilidades claras y desacopladas
- **Patrón Mapper** — `ProductMapper` y `ConversationMapper` separan entidades JPA de los DTOs de la API
- **Desacoplamiento del proveedor de IA** — `AiClient` es interfaz, `GroqClient` es la implementación; cambiar de proveedor no toca `ChatService` (RNF8)
- **Manejo centralizado de errores** — `@ControllerAdvice` con formato consistente para 400, 404 y 503 (rate limit de Groq)
- **Secrets seguros** — todas las credenciales vía variables de entorno; `.env` no incluido en el repo
- **Docker con healthcheck** — MySQL levanta con healthcheck antes de iniciar el backend, evitando race conditions al arrancar
- **Optimización de tokens** — `imageUrl` y `useCaseTags` se excluyen del tool result enviado a Groq; solo se incluyen en la respuesta final al frontend

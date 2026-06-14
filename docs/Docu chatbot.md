# PC Advisor Chatbot — Documentación de Diseño

## 1. Descripción general

Chatbot asesor de armado de PC / hardware, compuesto por:

- **Backend**: Spring Boot 3 (Java 17), groupId `cl.miloq.pcadvisor` — API REST
- **Web**: React + Vite + TypeScript — layout de dos paneles (catálogo + chat)
- **Mobile**: Ionic React + Capacitor — pantalla única de chat

Ambos clientes consumen la misma API REST documentada con OpenAPI/Swagger. No hay login: cada cliente se identifica con un `session_id` (UUID) generado y persistido localmente (localStorage / almacenamiento del dispositivo).

El motor de IA es **Groq** (free tier, sin costo), accedido vía interfaz `AiClient` (implementación `GroqClient`), desacoplado del resto del sistema. La IA se conecta al catálogo de productos mediante **function calling**.

---

## 2. Requisitos funcionales (RF)

| ID  | Requisito                                                                                                                                                                                            |
| --- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| RF1 | El sistema identifica al visitante mediante un `session_id` anónimo (sin registro/login) generado por el frontend                                                                                    |
| RF2 | El sistema recupera o crea automáticamente la conversación activa asociada al `session_id`                                                                                                           |
| RF3 | El usuario describe sus necesidades (uso, presupuesto, preferencias, o consultas de compatibilidad/comparación) y recibe una respuesta generada por IA, limitada al dominio de hardware/armado de PC |
| RF4 | El sistema persiste el historial de mensajes de la conversación activa                                                                                                                               |
| RF5 | El sistema mantiene un catálogo de productos (CPU, GPU, RAM, placa madre, fuente, etc.)                                                                                                              |
| RF6 | El catálogo de productos es administrable vía API (CRUD), sin restricción de rol en el MVP                                                                                                           |
| RF7 | El bot responde solo sobre temas de hardware/armado de PC; ante preguntas fuera de tema, indica que no es su especialidad                                                                            |
| RF8 | Cuando corresponde, el bot usa **function calling** para buscar productos reales en el catálogo y basar su recomendación/comparación en ellos, devolviéndolos como `recommendedProducts`             |
| RF9 | En la web, los productos en `recommendedProducts` se resaltan visualmente en el panel de catálogo                                                                                                    |

---

## 3. Requisitos no funcionales (RNF)

| ID | Requisito |
|----|-----------|
| RNF1 | API documentada con OpenAPI/Swagger |
| RNF2 | Manejo de errores centralizado (`@ControllerAdvice`) con respuestas consistentes |
| RNF3 | Validación de entrada con Bean Validation |
| RNF4 | CORS configurado para permitir web y mobile |
| RNF5 | Secrets (API key de Groq, credenciales DB) vía variables de entorno, nunca en el repo |
| RNF6 | Backend dockerizado (Dockerfile + docker-compose con la BD) |
| RNF7 | Manejo de timeouts/errores 429 al llamar a Groq, con fallback de mensaje al usuario (`503`) |
| RNF8 | El `AiClient` queda desacoplado del proveedor (interfaz + implementación `GroqClient`), facilitando cambiar de proveedor a futuro |

---

## 4. Modelo de datos (MySQL)

### 4.1 `conversations`

| Campo | Tipo | Notas |
|---|---|---|
| id | BIGINT PK AUTO_INCREMENT | |
| session_id | VARCHAR(36) | UUID generado por el frontend, sin FK |
| title | VARCHAR(255) | generado automáticamente o desde el primer mensaje |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

Índice recomendado: `session_id` (lookup frecuente para `GET /conversations/current`).

### 4.2 `messages`

| Campo | Tipo | Notas |
|---|---|---|
| id | BIGINT PK AUTO_INCREMENT | |
| conversation_id | BIGINT, FK → conversations.id | |
| role | ENUM('USER','ASSISTANT') | |
| content | TEXT | |
| created_at | TIMESTAMP | |

### 4.3 `products`

| Campo | Tipo | Notas |
|---|---|---|
| id | BIGINT PK AUTO_INCREMENT | |
| category | ENUM('CPU','GPU','RAM','MOTHERBOARD','PSU','STORAGE','CASE','COOLER') | |
| brand | VARCHAR(100) | |
| name | VARCHAR(150) | |
| specs | JSON | specs flexibles según categoría (socket, watts, capacidad, vram, tdp, etc.) |
| use_case_tags | JSON | array de etiquetas (ej. ["gaming", "edicion-video"]) |
| price_clp | DECIMAL(10,0) | |
| created_at | TIMESTAMP | |

### 4.4 Relaciones

- `conversations` 1:N `messages`
- `products` es independiente — se consulta a través de la función `buscar_productos`, invocada por la IA vía function calling.

---

## 5. Endpoints

### 5.1 Conversación activa

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/conversations/current` | Recupera (o crea si no existe) la conversación activa del `session_id`, con su historial de mensajes |

### 5.2 Chat

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/conversations/{id}/messages` | Envía mensaje del usuario, retorna mensaje + respuesta del bot + productos recomendados |

### 5.3 Productos

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/products` | Lista productos (filtrable por `category`, `priceMax`, `useCase`) — usado para poblar el catálogo en la web |
| GET | `/api/v1/products/{id}` | Detalle de producto |
| POST | `/api/v1/products` | Crear |
| PUT | `/api/v1/products/{id}` | Actualizar |
| DELETE | `/api/v1/products/{id}` | Eliminar |

---

## 6. Contrato detallado

**Convención general**

Todos los requests llevan el header `X-Session-Id: <uuid>`. Si no viene, el backend responde `400`.

---

**GET `/api/v1/conversations/current`**

Response `200`:
```json
{
  "id": 1,
  "title": "Nueva conversación",
  "createdAt": "2026-06-12T10:00:00Z",
  "updatedAt": "2026-06-12T10:05:00Z",
  "messages": [
    { "id": 10, "role": "USER", "content": "Quiero armar un PC para gaming con $600.000", "createdAt": "2026-06-12T10:01:00Z" },
    { "id": 11, "role": "ASSISTANT", "content": "Te recomiendo esta configuración...", "createdAt": "2026-06-12T10:01:05Z" }
  ]
}
```
Si no existe conversación para ese `session_id`, el backend la crea (vacía, `messages: []`) y la retorna igual con `201`.

---

**POST `/api/v1/conversations/{id}/messages`**

Request:
```json
{
  "content": "Quiero armar un PC para gaming con presupuesto de 600000"
}
```
Validación: `content` no vacío, máx. ~2000 caracteres (`@NotBlank @Size`).

Response `200`:
```json
{
  "userMessage": {
    "id": 20,
    "role": "USER",
    "content": "Quiero armar un PC para gaming con presupuesto de 600000",
    "createdAt": "2026-06-12T10:10:00Z"
  },
  "assistantMessage": {
    "id": 21,
    "role": "ASSISTANT",
    "content": "Con $600.000 te recomiendo esta combinación: ...",
    "createdAt": "2026-06-12T10:10:03Z"
  },
  "recommendedProducts": [
    {
      "id": 5,
      "category": "GPU",
      "brand": "AMD",
      "name": "Radeon RX 6600",
      "priceClp": 220000,
      "specs": { "vram": "8GB", "tdp": "132W" }
    },
    {
      "id": 12,
      "category": "CPU",
      "brand": "AMD",
      "name": "Ryzen 5 5600",
      "priceClp": 130000,
      "specs": { "socket": "AM4", "tdp": "65W" }
    }
  ]
}
```
`recommendedProducts` puede venir vacío `[]` si el bot no usó `buscar_productos` (ej. preguntas conceptuales como "qué es el TDP").

---

**Errores — formato global (todos los endpoints)**

Vía `@ControllerAdvice`:
```json
{
  "timestamp": "2026-06-12T10:10:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "content no puede estar vacío",
  "path": "/api/v1/conversations/1/messages"
}
```

Caso especial RNF7 (Groq con rate limit o timeout) → `503`:
```json
{
  "timestamp": "2026-06-12T10:10:00Z",
  "status": 503,
  "error": "Service Unavailable",
  "message": "El asesor no está disponible en este momento, intenta nuevamente en unos segundos.",
  "path": "/api/v1/conversations/1/messages"
}
```

---

## 7. Flujo de Function Calling

1. El usuario envía un mensaje describiendo su necesidad o consulta (armado completo, una pieza específica, compatibilidad, comparación entre productos).
2. El backend envía el mensaje a Groq junto con:
   - El **system prompt** (rol: asesor de armado de PC, alcance limitado a hardware).
   - La **definición de la función** `buscar_productos(categoria?, precioMax?, useCase?, marca?)`.
3. Groq decide si necesita datos del catálogo. Si es así, responde con una *tool call* indicando la función y los parámetros extraídos del mensaje del usuario.
4. El backend (`ChatService`) ejecuta `buscar_productos(...)` contra la tabla `products` (vía `ProductRepository`), obteniendo resultados reales.
5. El backend envía los resultados de vuelta a Groq como *tool result*.
6. Groq genera la respuesta final en lenguaje natural, recomendando/comparando los productos obtenidos.
7. El backend persiste el mensaje del usuario y la respuesta final del asistente en `messages`, y retorna la respuesta junto con `recommendedProducts` al frontend.

Este flujo se implementa dentro de `ChatService`; `GroqClient` se enfoca solo en la comunicación HTTP con la API de Groq (incluyendo el manejo del ciclo de *tool calls*).

---

## 8. Frontend

### 8.1 Web (React + Vite + TS)

Layout de dos paneles, fijo, sin navegación adicional:

- **Izquierda**: catálogo de productos (grid/lista, filtrable por categoría), poblado desde `GET /api/v1/products`.
- **Derecha**: panel de chat (mensajes + input).

Cuando `recommendedProducts` llega en la respuesta del chat, el frontend hace match por `id` contra los productos ya cargados en el catálogo y los resalta (borde/badge "Recomendado"), además de mostrarlos como tarjetas dentro del chat.

### 8.2 Mobile (Ionic React + Capacitor)

Pantalla única de chat (sin catálogo separado). Los `recommendedProducts` se muestran como tarjetas dentro del flujo de la conversación.

---

## 9. Decisiones pendientes

- [ ] Definir el set inicial de productos de prueba (seed data) por categoría — se puede resolver junto con la creación de entidades

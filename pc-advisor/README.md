# PC Advisor 🖥️

Chatbot asesor de armado y recomendación de hardware para PC. El usuario describe su necesidad (uso, presupuesto, consultas de compatibilidad) y recibe recomendaciones basadas en un catálogo real de productos, potenciadas por IA con function calling.

---

## Stack

| Capa | Tecnología |
|---|---|
| Backend | Java 17 · Spring Boot 3 · Maven · MySQL |
| Web | React 18 · TypeScript · Vite |
| Mobile | Ionic React · Capacitor |
| IA | Groq API (function calling) |
| Infraestructura | Docker · Docker Compose |

---

## Estructura del repositorio

```
pc-advisor/
├── backend/    # API REST Spring Boot
├── web/        # Cliente web React + Vite
├── mobile/     # Cliente mobile Ionic + Capacitor
└── docs/       # Documentación de diseño
```

---

## Requisitos previos

- Java 17+
- Node.js 18+
- Docker y Docker Compose
- Una API key de [Groq](https://console.groq.com/)

---

## Configuración

Copia el archivo de ejemplo y completa tus variables:

```bash
cp .env.example .env
```

Variables requeridas:

```env
GROQ_API_KEY=your_key_here
MYSQL_ROOT_PASSWORD=your_password
MYSQL_DATABASE=pcadvisor
```

---

## Levantar en desarrollo

```bash
# 1. Base de datos
docker-compose up -d

# 2. Backend (en /backend)
./mvnw spring-boot:run

# 3. Web (en /web)
npm install && npm run dev
```

La API queda disponible en `http://localhost:8080` y el frontend en `http://localhost:5173`.

---

## Documentación

La API se documenta automáticamente con Swagger UI en:

```
http://localhost:8080/swagger-ui.html
```

Más detalles de diseño en [`/docs`](./docs/).

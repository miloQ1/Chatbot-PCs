# Docu Impacto de Imágenes en Function Calling (Groq)

## Punto clave

Groq no necesita ver `image_url` para razonar ni redactar la recomendación. Incluirlo en el *tool result* solo consumiría tokens de forma innecesaria (las URLs de Wikimedia Commons pueden ser largas).

## Flujo ajustado

1. Groq solicita la función `buscar_productos(...)`.
2. El backend consulta `products` en MySQL (trae todos los campos, incluido `image_url`).
3. Al construir el *tool result* para Groq, el backend **omite `image_url`** (y `use_case_tags` si tampoco aporta) — solo se envía `category`, `brand`, `name`, `priceClp`, `specs`.
4. Groq genera la respuesta en texto e indica qué productos recomendó (por `id`).
5. El backend, al armar el JSON final de `recommendedProducts` para el frontend, vuelve a tomar los datos completos desde la consulta del paso 2 (incluyendo `image_url`) y los junta por `id`.

## Resumen del impacto

- **Consumo de tokens**: sin impacto, siempre que se excluya `image_url` del *tool result*.
- **Performance**: sin impacto — las imágenes nunca pasan por Groq, solo viajan backend → frontend.
- **Diseño**: refuerza la separación de responsabilidades — Groq "piensa" solo con los datos relevantes, y el backend enriquece la respuesta final (imagen, tags, etc.) antes de devolverla al cliente.

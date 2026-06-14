# Docu Imágenes de Productos

## Campo en el modelo de datos

```
products.image_url  VARCHAR(500)  NULLABLE
```

Guarda la URL (absoluta o relativa) de la imagen del producto. El frontend (web y mobile) la usa directo en las tarjetas de producto y en el panel de catálogo.

## Estrategia de obtención de imágenes

### Opción 1 — Wikimedia Commons API (recomendada para el seed inicial)

API pública, sin API key, sin límites prácticos. Permite buscar imágenes reales de componentes con licencia libre.

Ejemplo de consulta:

```
https://commons.wikimedia.org/w/api.php?action=query&list=search&srsearch=Radeon RX 6600&srnamespace=6&format=json
```

Esto retorna archivos de imagen relacionados al término de búsqueda, desde donde se extrae la URL directa de la imagen.

**Uso**: se ejecuta como **script de seed**, una sola vez al poblar la base de datos — para cada producto del catálogo, se busca en Commons, se toma la primera imagen razonable, y se guarda esa URL en `image_url`. No se llama a esta API en tiempo de ejecución (no depende de ella para servir requests de usuarios).

### Opción 2 — Iconos genéricos por categoría (fallback)

Un set de 8 íconos/ilustraciones, uno por categoría (`CPU`, `GPU`, `RAM`, `MOTHERBOARD`, `PSU`, `STORAGE`, `CASE`, `COOLER`), servidos como archivos estáticos desde `src/main/resources/static/images/categories/`.

**Uso**: si un producto no tiene `image_url` (o la búsqueda en Commons no encontró nada razonable), el frontend usa el ícono correspondiente a su `category` como fallback.

## Enfoque acordado

1. Script de seed busca imágenes reales vía Wikimedia Commons para poblar `image_url` de cada producto.
2. Si no se encuentra una imagen adecuada, `image_url` queda `null`.
3. El frontend muestra `image_url` si existe; si es `null`, muestra el ícono genérico de la `category` del producto.

Servir las imágenes propias (íconos de categoría) es directo con Spring Boot: cualquier archivo en `src/main/resources/static/` queda disponible automáticamente, ej. `http://localhost:8080/images/categories/gpu.svg`.

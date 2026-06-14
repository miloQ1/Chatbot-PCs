package cl.duoc.chatbot_api.seed;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Busca imagenes reales de componentes en Wikimedia Commons para el seed
 * inicial del catalogo (ver Docu-Imagenes-Productos.md).
 *
 * Si no se encuentra una imagen adecuada (o falla la consulta), retorna null
 * y el producto queda sin image_url -> el frontend usa el icono de su categoria
 * como fallback.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WikimediaImageService {
 
    private final RestClient wikimediaRestClient;
 
    public String findImageUrl(String searchTerm) {
        try {
            String fileTitle = searchFileTitle(searchTerm);
            if (fileTitle == null) {
                return null;
            }
            return fetchImageUrl(fileTitle);
        } catch (Exception e) {
            log.warn("No se pudo obtener imagen de Wikimedia Commons para '{}': {}", searchTerm, e.getMessage());
            return null;
        }
    }
 
    /**
     * Paso 1: busca archivos (namespace 6 = File:) que coincidan con el termino
     * y retorna el titulo del primer resultado, ej. "File:AMD Ryzen 5 5600.jpg".
     */
    private String searchFileTitle(String searchTerm) {
        JsonNode response = wikimediaRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("action", "query")
                        .queryParam("list", "search")
                        .queryParam("srsearch", searchTerm)
                        .queryParam("srnamespace", "6")
                        .queryParam("srlimit", "1")
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .body(JsonNode.class);
 
        JsonNode results = response.path("query").path("search");
        if (!results.isArray() || results.isEmpty()) {
            return null;
        }
        return results.get(0).path("title").asText(null);
    }
 
    /**
     * Paso 2: dado el titulo del archivo, obtiene la URL directa de la imagen.
     */
    private String fetchImageUrl(String fileTitle) {
    JsonNode response = wikimediaRestClient.get()
            .uri(uriBuilder -> uriBuilder
                    .queryParam("action", "query")
                    .queryParam("titles", fileTitle)
                    .queryParam("prop", "imageinfo")
                    .queryParam("iiprop", "url")
                    .queryParam("format", "json")
                    .build())
            .retrieve()
            .body(JsonNode.class);

    if (response == null || !response.has("query")) {
        return null;
    }

    JsonNode pages = response.path("query").path("pages");
    for (JsonNode page : pages) {
        JsonNode imageInfo = page.path("imageinfo");
        if (imageInfo.isArray() && !imageInfo.isEmpty()) {
            return imageInfo.get(0).path("url").asText(null);
        }
    }
    return null;
}
}

package cl.duoc.chatbot_api.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import cl.duoc.chatbot_api.model.ProductCategory;

/**
 * Representacion de un producto devuelta por la API (catalogo, recommendedProducts, etc.).
 */
public record ProductResponse(
        Long id,
        ProductCategory category,
        String brand,
        String name,
        Map<String, Object> specs,
        List<String> useCaseTags,
        BigDecimal priceClp,
        String imageUrl
) {
}
 
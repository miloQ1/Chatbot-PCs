package cl.duoc.chatbot_api.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import cl.duoc.chatbot_api.model.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Datos de entrada para crear o actualizar un producto del catalogo.
 */
public record ProductRequest(
 
        @NotNull(message = "category es obligatorio")
        ProductCategory category,
 
        @NotBlank(message = "brand no puede estar vacio")
        String brand,
 
        @NotBlank(message = "name no puede estar vacio")
        String name,
 
        Map<String, Object> specs,
 
        List<String> useCaseTags,
 
        @NotNull(message = "priceClp es obligatorio")
        @PositiveOrZero(message = "priceClp no puede ser negativo")
        BigDecimal priceClp,
 
        String imageUrl
) {
}
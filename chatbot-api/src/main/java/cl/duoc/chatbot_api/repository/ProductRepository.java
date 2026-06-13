package cl.duoc.chatbot_api.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.duoc.chatbot_api.model.Product;
import cl.duoc.chatbot_api.model.ProductCategory;

public interface ProductRepository extends JpaRepository<Product, Long> {
 
    /**
     * Busqueda con filtros opcionales: si un parametro viene null, esa condicion
     * no se aplica (gracias al patron ":param IS NULL OR ...").
     * Usado por GET /api/v1/products y por la funcion buscar_productos
     * (function calling) del ChatService.
     *
     * El filtro por useCaseTags (columna JSON) se maneja por separado con una
     * consulta nativa (JSON_CONTAINS), ya que JPQL no soporta columnas JSON.
     */
    @Query("""
            SELECT p FROM Product p
            WHERE (:category IS NULL OR p.category = :category)
              AND (:priceMax IS NULL OR p.priceClp <= :priceMax)
              AND (:brand IS NULL OR LOWER(p.brand) = LOWER(:brand))
            """)
    List<Product> search(
            @Param("category") ProductCategory category,
            @Param("priceMax") BigDecimal priceMax,
            @Param("brand") String brand
    );
}
package cl.duoc.chatbot_api.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private ProductCategory category;
 
    @Column(name = "brand", nullable = false, length = 100)
    private String brand;
 
    @Column(name = "name", nullable = false, length = 150)
    private String name;
 
    /**
     * Specs flexibles segun la categoria (socket, watts, capacidad, vram, tdp, etc.).
     * Mapeado a una columna JSON nativa de MySQL.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "specs", columnDefinition = "json")
    private Map<String, Object> specs;
 
    /**
     * Etiquetas de uso (ej. ["gaming", "edicion-video"]) para apoyar la busqueda
     * de la funcion buscar_productos.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "use_case_tags", columnDefinition = "json")
    private List<String> useCaseTags;
 
    @Column(name = "price_clp", nullable = false, precision = 10, scale = 0)
    private BigDecimal priceClp;
 
    /**
     * URL de la imagen del producto (Wikimedia Commons via seed, o null si no se
     * encontro una imagen adecuada -> el frontend usa el icono de la categoria).
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;
 
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
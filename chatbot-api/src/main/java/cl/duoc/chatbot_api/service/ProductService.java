package cl.duoc.chatbot_api.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.chatbot_api.dtos.request.ProductRequest;
import cl.duoc.chatbot_api.dtos.response.ProductResponse;
import cl.duoc.chatbot_api.exception.ResourceNotFoundException;
import cl.duoc.chatbot_api.mapper.ProductMapper;
import cl.duoc.chatbot_api.model.Product;
import cl.duoc.chatbot_api.model.ProductCategory;
import cl.duoc.chatbot_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
 
    private final ProductRepository productRepository;
 
    /**
     * Lista productos del catalogo, con filtros opcionales (category, priceMax, brand).
     * Usado por GET /api/v1/products.
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll(ProductCategory category, BigDecimal priceMax, String brand) {
        return productRepository.search(category, priceMax, brand).stream()
                .map(ProductMapper::toResponse)
                .toList();
    }
 
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = getProductOrThrow(id);
        return ProductMapper.toResponse(product);
    }
 
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = ProductMapper.toEntity(request);
        Product saved = productRepository.save(product);
        return ProductMapper.toResponse(saved);
    }
 
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProductOrThrow(id);
        ProductMapper.updateEntity(product, request);
        Product saved = productRepository.save(product);
        return ProductMapper.toResponse(saved);
    }
 
    @Transactional
    public void delete(Long id) {
        Product product = getProductOrThrow(id);
        productRepository.delete(product);
    }
 
    private Product getProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: id=" + id));
    }
 
    /**
     * Busqueda con filtro adicional por useCase (etiqueta de uso), usada por la
     * funcion buscar_productos durante el flujo de function calling.
     *
     * useCaseTags es una columna JSON, por lo que el filtro por useCase se aplica
     * en memoria sobre el resultado de la consulta a la BD (category/priceMax/brand).
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> search(ProductCategory category, BigDecimal priceMax, String brand, String useCase) {
        List<ProductResponse> results = findAll(category, priceMax, brand);
 
        if (useCase == null || useCase.isBlank()) {
            return results;
        }
 
        String normalized = useCase.toLowerCase();
        return results.stream()
                .filter(p -> p.useCaseTags() != null && p.useCaseTags().stream()
                        .anyMatch(tag -> tag.toLowerCase().contains(normalized)))
                .toList();
    }
}
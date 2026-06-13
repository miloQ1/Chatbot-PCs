package cl.duoc.chatbot_api.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.chatbot_api.dtos.request.ProductRequest;
import cl.duoc.chatbot_api.dtos.response.ProductResponse;
import cl.duoc.chatbot_api.model.ProductCategory;
import cl.duoc.chatbot_api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Endpoints del catalogo de productos (RF5, RF6).
 * Sin restriccion de rol en el MVP (no hay login).
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
 
    private final ProductService productService;
 
    /**
     * Lista el catalogo, con filtros opcionales por query params.
     * Ej: /api/v1/products?category=GPU&priceMax=300000
     */
    @GetMapping
    public List<ProductResponse> findAll(
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) String brand
    ) {
        return productService.findAll(category, priceMax, brand);
    }
 
    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return productService.findById(id);
    }
 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }
 
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
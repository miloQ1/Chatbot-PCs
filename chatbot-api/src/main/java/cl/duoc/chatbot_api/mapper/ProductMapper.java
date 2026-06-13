package cl.duoc.chatbot_api.mapper;

import cl.duoc.chatbot_api.dtos.request.ProductRequest;
import cl.duoc.chatbot_api.dtos.response.ProductResponse;
import cl.duoc.chatbot_api.model.Product;

/**
 * Conversion manual entre Product (entidad JPA) y los DTOs de la API.
 * Se mantiene manual (sin MapStruct) por simplicidad, dado el tamano del proyecto.
 */
public final class ProductMapper {
 
    private ProductMapper() {
    }
 
    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getCategory(),
                product.getBrand(),
                product.getName(),
                product.getSpecs(),
                product.getUseCaseTags(),
                product.getPriceClp(),
                product.getImageUrl()
        );
    }
 
    /**
     * Crea una nueva entidad Product a partir de un request (POST).
     */
    public static Product toEntity(ProductRequest request) {
        return Product.builder()
                .category(request.category())
                .brand(request.brand())
                .name(request.name())
                .specs(request.specs())
                .useCaseTags(request.useCaseTags())
                .priceClp(request.priceClp())
                .imageUrl(request.imageUrl())
                .build();
    }
 
    /**
     * Aplica los datos de un request sobre una entidad existente (PUT).
     */
    public static void updateEntity(Product product, ProductRequest request) {
        product.setCategory(request.category());
        product.setBrand(request.brand());
        product.setName(request.name());
        product.setSpecs(request.specs());
        product.setUseCaseTags(request.useCaseTags());
        product.setPriceClp(request.priceClp());
        product.setImageUrl(request.imageUrl());
    }
}
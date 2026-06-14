# Docu Mapper (ProductMapper)

## El problema que resuelve

Existen dos "formas" de representar un producto:

1. **`Product` (entidad JPA)**: el "molde" de la tabla `products` en la base de datos. Tiene anotaciones de Hibernate, relaciones, `@CreationTimestamp`, etc.
2. **`ProductRequest` / `ProductResponse` (DTOs)**: el "molde" de lo que entra/sale por la API (JSON).

¿Por qué no usar directamente `Product` en los endpoints?

- **Seguridad/control**: si se devuelve la entidad directo, se exponen campos que quizás no se quieren mostrar (o se permite que el cliente envíe campos que no debería poder setear, como `id` o `createdAt`).
- **Desacople**: si cambia el nombre de una columna en la BD, eso no debería romper el contrato de la API.
- **Validación**: las anotaciones `@NotBlank`, `@NotNull`, etc. van en el DTO de entrada (`ProductRequest`), no en la entidad — la entidad representa "lo que ya está guardado", el DTO representa "lo que llega del cliente, sin validar aún".

## El mapper es el "traductor" entre esos dos mundos

```java
public final class ProductMapper {
```

`final` + constructor privado (`private ProductMapper() {}`) = es una clase utilitaria, no se instancia, solo tiene métodos `static`. Es un patrón común para "agrupadores de funciones" en Java (como `Math` o `Collections`).

## Método 1: `toResponse` — Entidad → DTO de salida

```java
public static ProductResponse toResponse(Product product) {
    return new ProductResponse(
            product.getId(),
            product.getCategory(),
            ...
    );
}
```

Toma un `Product` (lo que viene de la base de datos vía `ProductRepository`) y construye un `ProductResponse` (lo que se serializa a JSON y se manda al frontend). Es literalmente copiar campo por campo de un objeto a otro.

## Método 2: `toEntity` — DTO de entrada → Entidad nueva

```java
public static Product toEntity(ProductRequest request) {
    return Product.builder()
            .category(request.category())
            ...
            .build();
}
```

Se usa en el `POST` (crear). Toma lo que mandó el cliente (`ProductRequest`, ya validado por `@Valid`) y construye un `Product` nuevo, listo para guardar con `productRepository.save(...)`. No se setean `id` ni `createdAt` — esos los genera la base de datos/Hibernate automáticamente.

## Método 3: `updateEntity` — aplicar cambios sobre una entidad existente

```java
public static void updateEntity(Product product, ProductRequest request) {
    product.setCategory(request.category());
    ...
}
```

Se usa en el `PUT` (actualizar). Diferencia clave con `toEntity`: aquí **no se crea un objeto nuevo**, se modifica uno que ya existe (que vino de `productRepository.findById(id)`). Ese objeto ya tiene su `id` y `createdAt` originales — solo se actualizan los campos editables.

## El flujo completo, con el `ProductService`

```
JSON del cliente → ProductRequest (validado por @Valid)
                         ↓
              ProductMapper.toEntity()  (POST) o updateEntity()  (PUT)
                         ↓
                  Product (entidad)
                         ↓
              productRepository.save(...)
                         ↓
                  Product guardado (con id generado)
                         ↓
              ProductMapper.toResponse()
                         ↓
              ProductResponse → se serializa a JSON → respuesta al cliente
```

## Resumen

El mapper es el punto único donde se "traduce" entre el modelo de base de datos y el contrato de la API. Sin él, ese código de "copiar campos" quedaría repetido y mezclado dentro del `ProductService`, dificultando su lectura y mantenimiento. Separarlo en su propia clase es una buena práctica de arquitectura (separación de responsabilidades / capas), mencionable en el informe del proyecto.

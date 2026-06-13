package cl.duoc.chatbot_api.seed;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.duoc.chatbot_api.model.Product;
import cl.duoc.chatbot_api.model.ProductCategory;
import cl.duoc.chatbot_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Carga un catalogo inicial de productos al levantar el backend, solo si la
 * tabla products esta vacia. Para cada producto, busca una imagen real en
 * Wikimedia Commons (ver WikimediaImageService y Docu-Imagenes-Productos.md).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {
 
    private final ProductRepository productRepository;
    private final WikimediaImageService wikimediaImageService;
 
    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            log.info("La tabla products ya tiene datos, se omite el seed inicial.");
            return;
        }
 
        log.info("Poblando catalogo inicial de productos ({} items)...", SEED_PRODUCTS.size());
 
        for (SeedProduct seed : SEED_PRODUCTS) {
            String imageUrl = wikimediaImageService.findImageUrl(seed.brand() + " " + seed.name());
 
            Product product = Product.builder()
                    .category(seed.category())
                    .brand(seed.brand())
                    .name(seed.name())
                    .specs(seed.specs())
                    .useCaseTags(seed.useCaseTags())
                    .priceClp(seed.priceClp())
                    .imageUrl(imageUrl)
                    .build();
 
            productRepository.save(product);
        }
 
        log.info("Catalogo inicial cargado.");
    }
 
    private record SeedProduct(
            ProductCategory category,
            String brand,
            String name,
            Map<String, Object> specs,
            List<String> useCaseTags,
            BigDecimal priceClp
    ) {
    }
 
    private static final List<SeedProduct> SEED_PRODUCTS = List.of(
 
            // ---- CPU ----
            new SeedProduct(ProductCategory.CPU, "AMD", "Ryzen 5 5600",
                    Map.of("socket", "AM4", "cores", 6, "threads", 12, "tdp", "65W"),
                    List.of("gaming", "oficina"),
                    BigDecimal.valueOf(130000)),
 
            new SeedProduct(ProductCategory.CPU, "Intel", "Core i5-12400F",
                    Map.of("socket", "LGA1700", "cores", 6, "threads", 12, "tdp", "65W"),
                    List.of("gaming", "edicion-video"),
                    BigDecimal.valueOf(150000)),
 
            new SeedProduct(ProductCategory.CPU, "AMD", "Ryzen 7 5800X3D",
                    Map.of("socket", "AM4", "cores", 8, "threads", 16, "tdp", "105W"),
                    List.of("gaming", "streaming"),
                    BigDecimal.valueOf(320000)),
 
            // ---- GPU ----
            new SeedProduct(ProductCategory.GPU, "AMD", "Radeon RX 6600",
                    Map.of("vram", "8GB", "tdp", "132W"),
                    List.of("gaming"),
                    BigDecimal.valueOf(220000)),
 
            new SeedProduct(ProductCategory.GPU, "NVIDIA", "GeForce RTX 4060",
                    Map.of("vram", "8GB", "tdp", "115W"),
                    List.of("gaming", "edicion-video"),
                    BigDecimal.valueOf(380000)),
 
            new SeedProduct(ProductCategory.GPU, "NVIDIA", "GeForce GTX 1650",
                    Map.of("vram", "4GB", "tdp", "75W"),
                    List.of("oficina", "gaming"),
                    BigDecimal.valueOf(140000)),
 
            // ---- RAM ----
            new SeedProduct(ProductCategory.RAM, "Kingston", "Fury Beast 16GB DDR4 3200MHz",
                    Map.of("capacity", "16GB", "speed", "3200MHz", "type", "DDR4"),
                    List.of("gaming", "oficina"),
                    BigDecimal.valueOf(35000)),
 
            new SeedProduct(ProductCategory.RAM, "Corsair", "Vengeance 32GB (2x16GB) DDR4 3600MHz",
                    Map.of("capacity", "32GB", "speed", "3600MHz", "type", "DDR4"),
                    List.of("gaming", "edicion-video"),
                    BigDecimal.valueOf(65000)),
 
            // ---- MOTHERBOARD ----
            new SeedProduct(ProductCategory.MOTHERBOARD, "ASUS", "Prime B550M-A",
                    Map.of("socket", "AM4", "formFactor", "mATX"),
                    List.of("gaming", "oficina"),
                    BigDecimal.valueOf(90000)),
 
            new SeedProduct(ProductCategory.MOTHERBOARD, "MSI", "PRO B660M-A",
                    Map.of("socket", "LGA1700", "formFactor", "mATX"),
                    List.of("gaming", "edicion-video"),
                    BigDecimal.valueOf(110000)),
 
            // ---- PSU ----
            new SeedProduct(ProductCategory.PSU, "EVGA", "600W 80+ Bronze",
                    Map.of("wattage", "600W", "efficiency", "80+ Bronze"),
                    List.of("oficina", "gaming"),
                    BigDecimal.valueOf(45000)),
 
            new SeedProduct(ProductCategory.PSU, "Corsair", "RM750x 750W 80+ Gold",
                    Map.of("wattage", "750W", "efficiency", "80+ Gold"),
                    List.of("gaming", "edicion-video"),
                    BigDecimal.valueOf(95000)),
 
            // ---- STORAGE ----
            new SeedProduct(ProductCategory.STORAGE, "Kingston", "NV2 1TB NVMe SSD",
                    Map.of("capacity", "1TB", "type", "NVMe SSD", "interface", "PCIe 4.0"),
                    List.of("gaming", "oficina"),
                    BigDecimal.valueOf(55000)),
 
            new SeedProduct(ProductCategory.STORAGE, "Western Digital", "Blue 2TB HDD",
                    Map.of("capacity", "2TB", "type", "HDD", "interface", "SATA"),
                    List.of("oficina", "almacenamiento"),
                    BigDecimal.valueOf(60000)),
 
            // ---- CASE ----
            new SeedProduct(ProductCategory.CASE, "NZXT", "H510",
                    Map.of("formFactor", "ATX Mid Tower"),
                    List.of("gaming"),
                    BigDecimal.valueOf(70000)),
 
            new SeedProduct(ProductCategory.CASE, "Cooler Master", "MasterBox Q300L",
                    Map.of("formFactor", "mATX Mid Tower"),
                    List.of("oficina", "gaming"),
                    BigDecimal.valueOf(45000)),
 
            // ---- COOLER ----
            new SeedProduct(ProductCategory.COOLER, "Cooler Master", "Hyper 212",
                    Map.of("type", "Aire", "socketSupport", "AM4/LGA1700"),
                    List.of("gaming", "edicion-video"),
                    BigDecimal.valueOf(30000)),
 
            new SeedProduct(ProductCategory.COOLER, "Thermalright", "Peerless Assassin 120 SE",
                    Map.of("type", "Aire", "socketSupport", "AM4/LGA1700"),
                    List.of("gaming", "streaming"),
                    BigDecimal.valueOf(38000))
    );
}

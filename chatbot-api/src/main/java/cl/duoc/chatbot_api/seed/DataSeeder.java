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
            
            // Pausa de 500ms para respetar el rate limit de Wikimedia
            try {
                Thread.sleep(500); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

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

        // ---- CPU (12 productos) ----
        new SeedProduct(ProductCategory.CPU, "AMD", "Ryzen 3 3200G",
                Map.of("socket", "AM4", "cores", 4, "threads", 4, "tdp", "65W", "igpu", "Vega 8"),
                List.of("oficina", "gaming-basico"),
                BigDecimal.valueOf(80000)),

        new SeedProduct(ProductCategory.CPU, "AMD", "Ryzen 5 5600",
                Map.of("socket", "AM4", "cores", 6, "threads", 12, "tdp", "65W"),
                List.of("gaming", "streaming"),
                BigDecimal.valueOf(130000)),

        new SeedProduct(ProductCategory.CPU, "AMD", "Ryzen 5 5600X",
                Map.of("socket", "AM4", "cores", 6, "threads", 12, "tdp", "65W"),
                List.of("gaming", "streaming"),
                BigDecimal.valueOf(150000)),

        new SeedProduct(ProductCategory.CPU, "AMD", "Ryzen 7 5700X",
                Map.of("socket", "AM4", "cores", 8, "threads", 16, "tdp", "65W"),
                List.of("gaming", "edicion-video"),
                BigDecimal.valueOf(200000)),

        new SeedProduct(ProductCategory.CPU, "AMD", "Ryzen 7 5800X3D",
                Map.of("socket", "AM4", "cores", 8, "threads", 16, "tdp", "105W"),
                List.of("gaming", "streaming"),
                BigDecimal.valueOf(320000)),

        new SeedProduct(ProductCategory.CPU, "AMD", "Ryzen 5 7600X",
                Map.of("socket", "AM5", "cores", 6, "threads", 12, "tdp", "105W"),
                List.of("gaming", "edicion-video"),
                BigDecimal.valueOf(280000)),

        new SeedProduct(ProductCategory.CPU, "AMD", "Ryzen 7 7700X",
                Map.of("socket", "AM5", "cores", 8, "threads", 16, "tdp", "105W"),
                List.of("gaming", "edicion-video", "streaming"),
                BigDecimal.valueOf(380000)),

        new SeedProduct(ProductCategory.CPU, "Intel", "Core i3-12100F",
                Map.of("socket", "LGA1700", "cores", 4, "threads", 8, "tdp", "58W"),
                List.of("oficina", "gaming-basico"),
                BigDecimal.valueOf(90000)),

        new SeedProduct(ProductCategory.CPU, "Intel", "Core i5-12400F",
                Map.of("socket", "LGA1700", "cores", 6, "threads", 12, "tdp", "65W"),
                List.of("gaming", "edicion-video"),
                BigDecimal.valueOf(150000)),

        new SeedProduct(ProductCategory.CPU, "Intel", "Core i5-13600K",
                Map.of("socket", "LGA1700", "cores", 14, "threads", 20, "tdp", "125W"),
                List.of("gaming", "edicion-video", "streaming"),
                BigDecimal.valueOf(320000)),

        new SeedProduct(ProductCategory.CPU, "Intel", "Core i7-13700K",
                Map.of("socket", "LGA1700", "cores", 16, "threads", 24, "tdp", "125W"),
                List.of("gaming", "edicion-video", "render-3d"),
                BigDecimal.valueOf(450000)),

        new SeedProduct(ProductCategory.CPU, "Intel", "Core i9-13900K",
                Map.of("socket", "LGA1700", "cores", 24, "threads", 32, "tdp", "125W"),
                List.of("gaming", "render-3d", "edicion-video"),
                BigDecimal.valueOf(650000)),

        // ---- GPU (14 productos) ----
        new SeedProduct(ProductCategory.GPU, "NVIDIA", "GeForce GT 1030",
                Map.of("vram", "2GB", "tdp", "30W"),
                List.of("oficina", "gaming-basico"),
                BigDecimal.valueOf(60000)),

        new SeedProduct(ProductCategory.GPU, "NVIDIA", "GeForce GTX 1650",
                Map.of("vram", "4GB", "tdp", "75W"),
                List.of("gaming-basico", "oficina"),
                BigDecimal.valueOf(140000)),

        new SeedProduct(ProductCategory.GPU, "NVIDIA", "GeForce GTX 1660 Super",
                Map.of("vram", "6GB", "tdp", "125W"),
                List.of("gaming", "streaming"),
                BigDecimal.valueOf(200000)),

        new SeedProduct(ProductCategory.GPU, "NVIDIA", "GeForce RTX 3060",
                Map.of("vram", "12GB", "tdp", "170W"),
                List.of("gaming", "edicion-video", "streaming"),
                BigDecimal.valueOf(280000)),

        new SeedProduct(ProductCategory.GPU, "NVIDIA", "GeForce RTX 3060 Ti",
                Map.of("vram", "8GB", "tdp", "200W"),
                List.of("gaming", "edicion-video"),
                BigDecimal.valueOf(320000)),

        new SeedProduct(ProductCategory.GPU, "NVIDIA", "GeForce RTX 3070",
                Map.of("vram", "8GB", "tdp", "220W"),
                List.of("gaming", "edicion-video", "streaming"),
                BigDecimal.valueOf(400000)),

        new SeedProduct(ProductCategory.GPU, "NVIDIA", "GeForce RTX 4060",
                Map.of("vram", "8GB", "tdp", "115W"),
                List.of("gaming", "edicion-video"),
                BigDecimal.valueOf(380000)),

        new SeedProduct(ProductCategory.GPU, "NVIDIA", "GeForce RTX 4070",
                Map.of("vram", "12GB", "tdp", "200W"),
                List.of("gaming", "edicion-video", "render-3d"),
                BigDecimal.valueOf(600000)),

        new SeedProduct(ProductCategory.GPU, "NVIDIA", "GeForce RTX 4070 Ti",
                Map.of("vram", "12GB", "tdp", "285W"),
                List.of("gaming", "render-3d", "edicion-video"),
                BigDecimal.valueOf(850000)),

        new SeedProduct(ProductCategory.GPU, "AMD", "Radeon RX 6600",
                Map.of("vram", "8GB", "tdp", "132W"),
                List.of("gaming"),
                BigDecimal.valueOf(220000)),

        new SeedProduct(ProductCategory.GPU, "AMD", "Radeon RX 6650 XT",
                Map.of("vram", "8GB", "tdp", "180W"),
                List.of("gaming", "streaming"),
                BigDecimal.valueOf(260000)),

        new SeedProduct(ProductCategory.GPU, "AMD", "Radeon RX 6700 XT",
                Map.of("vram", "12GB", "tdp", "230W"),
                List.of("gaming", "edicion-video"),
                BigDecimal.valueOf(340000)),

        new SeedProduct(ProductCategory.GPU, "AMD", "Radeon RX 7600",
                Map.of("vram", "8GB", "tdp", "165W"),
                List.of("gaming", "streaming"),
                BigDecimal.valueOf(300000)),

        new SeedProduct(ProductCategory.GPU, "AMD", "Radeon RX 7700 XT",
                Map.of("vram", "12GB", "tdp", "245W"),
                List.of("gaming", "edicion-video", "render-3d"),
                BigDecimal.valueOf(430000)),

        // ---- RAM (6 productos) ----
        new SeedProduct(ProductCategory.RAM, "Kingston", "Fury Beast 8GB DDR4 3200MHz",
                Map.of("capacity", "8GB", "speed", "3200MHz", "type", "DDR4"),
                List.of("oficina", "gaming-basico"),
                BigDecimal.valueOf(20000)),

        new SeedProduct(ProductCategory.RAM, "Kingston", "Fury Beast 16GB DDR4 3200MHz",
                Map.of("capacity", "16GB", "speed", "3200MHz", "type", "DDR4"),
                List.of("gaming", "oficina"),
                BigDecimal.valueOf(35000)),

        new SeedProduct(ProductCategory.RAM, "Corsair", "Vengeance 32GB DDR4 3600MHz",
                Map.of("capacity", "32GB", "speed", "3600MHz", "type", "DDR4"),
                List.of("gaming", "edicion-video"),
                BigDecimal.valueOf(65000)),

        new SeedProduct(ProductCategory.RAM, "G.Skill", "Trident Z5 32GB DDR5 6000MHz",
                Map.of("capacity", "32GB", "speed", "6000MHz", "type", "DDR5"),
                List.of("gaming", "edicion-video", "render-3d"),
                BigDecimal.valueOf(120000)),

        new SeedProduct(ProductCategory.RAM, "Corsair", "Vengeance 64GB DDR5 5600MHz",
                Map.of("capacity", "64GB", "speed", "5600MHz", "type", "DDR5"),
                List.of("render-3d", "edicion-video"),
                BigDecimal.valueOf(180000)),

        new SeedProduct(ProductCategory.RAM, "Kingston", "Fury Beast 16GB DDR5 5200MHz",
                Map.of("capacity", "16GB", "speed", "5200MHz", "type", "DDR5"),
                List.of("gaming", "oficina"),
                BigDecimal.valueOf(55000)),

        // ---- MOTHERBOARD (6 productos) ----
        new SeedProduct(ProductCategory.MOTHERBOARD, "ASUS", "Prime B550M-A",
                Map.of("socket", "AM4", "formFactor", "mATX", "ddr", "DDR4"),
                List.of("gaming", "oficina"),
                BigDecimal.valueOf(90000)),

        new SeedProduct(ProductCategory.MOTHERBOARD, "MSI", "B450 Tomahawk Max",
                Map.of("socket", "AM4", "formFactor", "ATX", "ddr", "DDR4"),
                List.of("gaming", "oficina"),
                BigDecimal.valueOf(110000)),

        new SeedProduct(ProductCategory.MOTHERBOARD, "Gigabyte", "B550 Aorus Elite",
                Map.of("socket", "AM4", "formFactor", "ATX", "ddr", "DDR4"),
                List.of("gaming", "edicion-video"),
                BigDecimal.valueOf(130000)),

        new SeedProduct(ProductCategory.MOTHERBOARD, "MSI", "PRO B660M-A",
                Map.of("socket", "LGA1700", "formFactor", "mATX", "ddr", "DDR4"),
                List.of("gaming", "oficina"),
                BigDecimal.valueOf(110000)),

        new SeedProduct(ProductCategory.MOTHERBOARD, "ASUS", "ROG Strix B650E-F",
                Map.of("socket", "AM5", "formFactor", "ATX", "ddr", "DDR5"),
                List.of("gaming", "edicion-video"),
                BigDecimal.valueOf(320000)),

        new SeedProduct(ProductCategory.MOTHERBOARD, "MSI", "MAG B650 Tomahawk",
                Map.of("socket", "AM5", "formFactor", "ATX", "ddr", "DDR5"),
                List.of("gaming", "edicion-video"),
                BigDecimal.valueOf(250000)),

        // ---- PSU (4 productos) ----
        new SeedProduct(ProductCategory.PSU, "EVGA", "600W 80+ Bronze",
                Map.of("wattage", "600W", "efficiency", "80+ Bronze"),
                List.of("oficina", "gaming-basico"),
                BigDecimal.valueOf(45000)),

        new SeedProduct(ProductCategory.PSU, "Corsair", "RM750x 750W 80+ Gold",
                Map.of("wattage", "750W", "efficiency", "80+ Gold"),
                List.of("gaming", "edicion-video"),
                BigDecimal.valueOf(95000)),

        new SeedProduct(ProductCategory.PSU, "Seasonic", "Focus GX 850W 80+ Gold",
                Map.of("wattage", "850W", "efficiency", "80+ Gold"),
                List.of("gaming", "render-3d"),
                BigDecimal.valueOf(120000)),

        new SeedProduct(ProductCategory.PSU, "be quiet!", "Straight Power 1000W 80+ Platinum",
                Map.of("wattage", "1000W", "efficiency", "80+ Platinum"),
                List.of("render-3d", "edicion-video"),
                BigDecimal.valueOf(200000)),

        // ---- STORAGE (4 productos) ----
        new SeedProduct(ProductCategory.STORAGE, "Kingston", "NV2 500GB NVMe SSD",
                Map.of("capacity", "500GB", "type", "NVMe SSD", "interface", "PCIe 4.0"),
                List.of("gaming-basico", "oficina"),
                BigDecimal.valueOf(35000)),

        new SeedProduct(ProductCategory.STORAGE, "Kingston", "NV2 1TB NVMe SSD",
                Map.of("capacity", "1TB", "type", "NVMe SSD", "interface", "PCIe 4.0"),
                List.of("gaming", "oficina"),
                BigDecimal.valueOf(55000)),

        new SeedProduct(ProductCategory.STORAGE, "Samsung", "980 Pro 2TB NVMe SSD",
                Map.of("capacity", "2TB", "type", "NVMe SSD", "interface", "PCIe 4.0"),
                List.of("gaming", "edicion-video"),
                BigDecimal.valueOf(150000)),

        new SeedProduct(ProductCategory.STORAGE, "Western Digital", "Blue 2TB HDD",
                Map.of("capacity", "2TB", "type", "HDD", "interface", "SATA"),
                List.of("almacenamiento", "oficina"),
                BigDecimal.valueOf(60000)),

        // ---- CASE (2 productos) ----
        new SeedProduct(ProductCategory.CASE, "NZXT", "H510",
                Map.of("formFactor", "ATX Mid Tower"),
                List.of("gaming"),
                BigDecimal.valueOf(70000)),

        new SeedProduct(ProductCategory.CASE, "Cooler Master", "MasterBox Q300L",
                Map.of("formFactor", "mATX Mid Tower"),
                List.of("oficina", "gaming"),
                BigDecimal.valueOf(45000)),

        // ---- COOLER (2 productos) ----
        new SeedProduct(ProductCategory.COOLER, "Cooler Master", "Hyper 212",
                Map.of("type", "Aire", "socketSupport", "AM4/LGA1700"),
                List.of("gaming", "oficina"),
                BigDecimal.valueOf(30000)),

        new SeedProduct(ProductCategory.COOLER, "Thermalright", "Peerless Assassin 120 SE",
                Map.of("type", "Aire", "socketSupport", "AM4/LGA1700"),
                List.of("gaming", "streaming"),
                BigDecimal.valueOf(38000))
);
}

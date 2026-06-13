package cl.duoc.chatbot_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * RestClient para la API publica de Wikimedia Commons (sin API key).
 * Usado por el seed inicial de productos para obtener imagenes reales
 * (ver Docu-Imagenes-Productos.md).
 */
@Configuration
public class WikimediaClientConfig {
 
    @Bean
    public RestClient wikimediaRestClient() {
        return RestClient.builder()
                .baseUrl("https://commons.wikimedia.org/w/api.php")
                .build();
    }
}

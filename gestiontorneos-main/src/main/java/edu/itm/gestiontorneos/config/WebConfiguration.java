package edu.itm.gestiontorneos.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Permite que el frontend independiente consuma las APIs desde orígenes explícitos. */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
  private final String[] allowedOrigins;

  public WebConfiguration(
      @Value("${app.cors.allowed-origins:http://localhost:5500,http://127.0.0.1:5500}")
          String allowedOrigins) {
    this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
        .map(String::trim).filter(origin -> !origin.isEmpty()).toArray(String[]::new);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    for (String path : new String[] {"/deportes/**", "/equipos/**", "/torneos/**"}) {
      registry.addMapping(path)
          .allowedOrigins(allowedOrigins)
          .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
          .allowedHeaders("Content-Type", "Accept")
          .allowCredentials(false)
          .maxAge(3600);
    }
  }
}

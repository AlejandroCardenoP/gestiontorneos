package edu.itm.gestiontorneos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
  @Bean
  public OpenAPI gestionTorneosApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Gestion de torneos")
                .version("2.0.0")
                .description(
                    "API JDBC de deportes, equipos, torneos e inscripciones. Estados: INSCRIPCION"
                        + " -> EN_CURSO -> FINALIZADO. Errores: 400 entrada invalida, 404 recurso"
                        + " inexistente, 409 conflicto de negocio. Demostracion academica sin"
                        + " autenticacion: limitar acceso de red."));
  }
}

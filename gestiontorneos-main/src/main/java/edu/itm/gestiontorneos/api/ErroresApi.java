package edu.itm.gestiontorneos.api;

import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import java.lang.annotation.*;

/** Respuestas comunes para mantener consistente el contrato de errores. */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
  @ApiResponse(
      responseCode = "400",
      description = "Campos, JSON o identificador invalidos",
      content =
          @Content(
              mediaType = "application/problem+json",
              schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
  @ApiResponse(
      responseCode = "404",
      description = "Recurso no encontrado",
      content =
          @Content(
              mediaType = "application/problem+json",
              schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
  @ApiResponse(
      responseCode = "409",
      description = "Regla de negocio, duplicado o relacion que impide la operacion",
      content =
          @Content(
              mediaType = "application/problem+json",
              schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
  @ApiResponse(
      responseCode = "500",
      description = "Error interno sin detalles sensibles",
      content =
          @Content(
              mediaType = "application/problem+json",
              schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
  @ApiResponse(
      responseCode = "503",
      description = "Base de datos no disponible",
      content =
          @Content(
              mediaType = "application/problem+json",
              schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
})
public @interface ErroresApi {}

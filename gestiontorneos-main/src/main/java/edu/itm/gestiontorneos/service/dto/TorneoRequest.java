package edu.itm.gestiontorneos.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record TorneoRequest(
    @NotBlank
        @Size(max = 100)
        @Schema(description = "Nombre del registro", example = "Copa universitaria")
        String nombre,
    @NotNull @Positive @Schema(description = "Identificador de un deporte existente", example = "1")
        Long deporteId,
    @Min(2) @Max(128) @Schema(description = "Cupo total; entre 2 y 128 equipos", example = "8")
        int cupoEquipos,
    @NotNull @Schema(description = "Fecha de inicio con formato YYYY-MM-DD", example = "2027-06-15")
        java.time.LocalDate fechaInicio) {}

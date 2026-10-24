package edu.itm.gestiontorneos.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record EquipoRequest(
    @NotBlank
        @Size(max = 100)
        @Schema(description = "Nombre del registro", example = "Copa universitaria")
        String nombre,
    @NotNull @Positive @Schema(description = "Identificador de un deporte existente", example = "1")
        Long deporteId,
    @Min(1)
        @Schema(description = "Jugadores del equipo dentro del rango del deporte", example = "7")
        int cantidadJugadores) {}

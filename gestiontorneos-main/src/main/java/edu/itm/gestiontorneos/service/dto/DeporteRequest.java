package edu.itm.gestiontorneos.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record DeporteRequest(
    @NotBlank
        @Size(max = 100)
        @Schema(description = "Nombre del registro", example = "Copa universitaria")
        String nombre,
    @Min(1) @Schema(description = "Minimo de jugadores permitido", example = "5") int minJugadores,
    @Min(1)
        @Schema(
            description = "Maximo de jugadores; debe ser mayor o igual al minimo",
            example = "11")
        int maxJugadores) {}

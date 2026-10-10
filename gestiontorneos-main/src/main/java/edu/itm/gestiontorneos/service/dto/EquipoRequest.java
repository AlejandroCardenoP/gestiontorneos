package edu.itm.gestiontorneos.service.dto;

import jakarta.validation.constraints.*;

public record EquipoRequest(
    @NotBlank @Size(max = 100) String nombre,
    @NotNull @Positive Long deporteId,
    @Min(1) int cantidadJugadores) {}

package edu.itm.gestiontorneos.service.dto;

import jakarta.validation.constraints.*;

public record TorneoRequest(
    @NotBlank @Size(max = 100) String nombre,
    @NotNull @Positive Long deporteId,
    @Min(2) @Max(128) int cupoEquipos,
    @NotNull java.time.LocalDate fechaInicio) {}

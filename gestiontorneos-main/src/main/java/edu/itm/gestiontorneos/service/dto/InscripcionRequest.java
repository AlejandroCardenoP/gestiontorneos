package edu.itm.gestiontorneos.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record InscripcionRequest(
    @NotNull
        @Positive
        @Schema(
            description = "Identificador del equipo del mismo deporte del torneo",
            example = "1")
        Long equipoId) {}

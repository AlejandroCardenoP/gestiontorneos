package edu.itm.gestiontorneos.service.dto;

import jakarta.validation.constraints.*;

public record InscripcionRequest(@NotNull @Positive Long equipoId) {}

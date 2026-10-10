package edu.itm.gestiontorneos.service.dto;

import jakarta.validation.constraints.*;

public record DeporteRequest(
    @NotBlank @Size(max = 100) String nombre, @Min(1) int minJugadores, @Min(1) int maxJugadores) {}

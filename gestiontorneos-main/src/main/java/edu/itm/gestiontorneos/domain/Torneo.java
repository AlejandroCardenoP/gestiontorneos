package edu.itm.gestiontorneos.domain;

public record Torneo(
    Long id,
    String nombre,
    Long deporteId,
    int cupoEquipos,
    java.time.LocalDate fechaInicio,
    EstadoTorneo estado) {}

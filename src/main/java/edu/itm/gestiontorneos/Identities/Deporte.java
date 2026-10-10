package edu.itm.gestiontorneos.Identities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un deporte del sistema de torneos.
 * Define la cantidad mínima y máxima de jugadores
 * que debe tener un equipo para poder inscribirse.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Deporte {

    /** Identificador único del deporte. */
    private Long id;

    /** Nombre del deporte, por ejemplo Fútbol o Baloncesto. */
    private String nombre;

    /** Cantidad mínima de jugadores permitida por equipo. */
    private int minJugadores;

    /** Cantidad máxima de jugadores permitida por equipo. */
    private int maxJugadores;
}
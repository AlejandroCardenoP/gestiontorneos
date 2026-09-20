package edu.itm.gestiontorneos.Identities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Deporte {
    private Long id;
    private String nombre;
    private int minJugadores;
    private int maxJugadores;
}
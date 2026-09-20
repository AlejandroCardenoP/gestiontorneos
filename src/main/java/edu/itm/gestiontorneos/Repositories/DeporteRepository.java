package edu.itm.gestiontorneos.Repositories;

import java.util.ArrayList;
import java.util.List;
import edu.itm.gestiontorneos.Identities.Deporte;
import org.springframework.stereotype.Repository;

@Repository

public class DeporteRepository {

    public List<Deporte> obtenerDeportes() {
        List<Deporte> deportes = new ArrayList<>();

        deportes.add(Deporte.builder()
                .id(1L)
                .nombre("Fútbol")
                .minJugadores(7)
                .maxJugadores(11)
                .build());

        deportes.add(Deporte.builder()
                .id(2L)
                .nombre("Baloncesto")
                .minJugadores(5)
                .maxJugadores(12)
                .build());

        return deportes;
    }
}

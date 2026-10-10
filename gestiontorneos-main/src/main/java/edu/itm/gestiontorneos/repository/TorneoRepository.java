package edu.itm.gestiontorneos.repository;

import edu.itm.gestiontorneos.domain.*;
import java.util.List;
import java.util.Optional;

public interface TorneoRepository {
  List<Torneo> listar();

  Optional<Torneo> buscar(Long id);

  Optional<Torneo> bloquear(Long id);

  Torneo crear(Torneo torneo);

  Torneo actualizar(Torneo torneo);

  void eliminar(Long id);

  void cambiarEstado(Long id, EstadoTorneo estado);
}

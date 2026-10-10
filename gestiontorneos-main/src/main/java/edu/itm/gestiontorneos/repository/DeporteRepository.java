package edu.itm.gestiontorneos.repository;

import edu.itm.gestiontorneos.domain.*;
import java.util.List;
import java.util.Optional;

public interface DeporteRepository {
  List<Deporte> listar();

  Optional<Deporte> buscar(Long id);

  Optional<Deporte> bloquear(Long id);

  Deporte crear(Deporte deporte);

  Deporte actualizar(Deporte deporte);

  void eliminar(Long id);

  boolean existenEquiposFueraDeRango(Long deporteId, int minimo, int maximo);
}

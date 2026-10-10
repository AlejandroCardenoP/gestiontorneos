package edu.itm.gestiontorneos.repository;

import edu.itm.gestiontorneos.domain.*;
import java.util.List;
import java.util.Optional;

public interface EquipoRepository {
  List<Equipo> listar();

  Optional<Equipo> buscar(Long id);

  Optional<Equipo> bloquear(Long id);

  Equipo crear(Equipo equipo);

  Equipo actualizar(Equipo equipo);

  void eliminar(Long id);

  boolean tieneInscripciones(Long equipoId);
}

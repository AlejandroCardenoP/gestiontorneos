package edu.itm.gestiontorneos.repository;

import edu.itm.gestiontorneos.domain.*;
import java.util.List;

public interface InscripcionRepository {
  List<Inscripcion> listar(Long torneoId);

  long contar(Long torneoId);

  boolean existe(Long torneoId, Long equipoId);

  Inscripcion crear(Long torneoId, Long equipoId);

  int eliminar(Long torneoId, Long equipoId);
}

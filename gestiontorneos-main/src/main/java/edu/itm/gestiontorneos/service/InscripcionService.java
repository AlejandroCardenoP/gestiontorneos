package edu.itm.gestiontorneos.service;

import edu.itm.gestiontorneos.domain.Inscripcion;
import java.util.List;

public interface InscripcionService {
  List<Inscripcion> listar(Long torneoId);

  Inscripcion inscribir(Long torneoId, Long equipoId);

  void cancelar(Long torneoId, Long equipoId);
}

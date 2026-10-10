package edu.itm.gestiontorneos.service;

import edu.itm.gestiontorneos.domain.Equipo;
import edu.itm.gestiontorneos.service.dto.EquipoRequest;
import java.util.List;

public interface EquipoService {
  List<Equipo> listar();

  Equipo obtener(Long id);

  Equipo crear(EquipoRequest request);

  Equipo actualizar(Long id, EquipoRequest request);

  void eliminar(Long id);
}

package edu.itm.gestiontorneos.service;

import edu.itm.gestiontorneos.domain.Deporte;
import edu.itm.gestiontorneos.service.dto.DeporteRequest;
import java.util.List;

public interface DeporteService {
  List<Deporte> listar();

  Deporte obtener(Long id);

  Deporte crear(DeporteRequest request);

  Deporte actualizar(Long id, DeporteRequest request);

  void eliminar(Long id);
}

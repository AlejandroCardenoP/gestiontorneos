package edu.itm.gestiontorneos.service;

import edu.itm.gestiontorneos.domain.Torneo;
import edu.itm.gestiontorneos.service.dto.TorneoRequest;
import java.util.List;

public interface TorneoService {
  List<Torneo> listar();

  Torneo obtener(Long id);

  Torneo crear(TorneoRequest request);

  Torneo actualizar(Long id, TorneoRequest request);

  void eliminar(Long id);

  Torneo iniciar(Long id);

  Torneo finalizar(Long id);
}

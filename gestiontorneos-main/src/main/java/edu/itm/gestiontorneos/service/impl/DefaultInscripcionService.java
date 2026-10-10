package edu.itm.gestiontorneos.service.impl;

import edu.itm.gestiontorneos.domain.*;
import edu.itm.gestiontorneos.repository.*;
import edu.itm.gestiontorneos.service.*;
import edu.itm.gestiontorneos.service.dto.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DefaultInscripcionService implements InscripcionService {
  private final TorneoRepository torneos;
  private final EquipoRepository equipos;
  private final InscripcionRepository inscripciones;

  public DefaultInscripcionService(
      TorneoRepository torneos, EquipoRepository equipos, InscripcionRepository inscripciones) {
    this.torneos = torneos;
    this.equipos = equipos;
    this.inscripciones = inscripciones;
  }

  public List<Inscripcion> listar(Long id) {
    torneos.buscar(id).orElseThrow(() -> new NoEncontradoException("Torneo", id));
    return inscripciones.listar(id);
  }

  @Transactional
  public Inscripcion inscribir(Long torneoId, Long equipoId) {
    // Serialize capacity checks and state changes on the same tournament row.
    var torneo = abiertoBloqueado(torneoId);
    var equipo =
        equipos.bloquear(equipoId).orElseThrow(() -> new NoEncontradoException("Equipo", equipoId));
    if (!torneo.deporteId().equals(equipo.deporteId()))
      throw new ReglaNegocioException("El equipo pertenece a otro deporte");
    if (inscripciones.existe(torneoId, equipoId))
      throw new ReglaNegocioException("El equipo ya esta inscrito");
    if (inscripciones.contar(torneoId) >= torneo.cupoEquipos())
      throw new ReglaNegocioException("El torneo no tiene cupos disponibles");
    return inscripciones.crear(torneoId, equipoId);
  }

  @Transactional
  public void cancelar(Long torneoId, Long equipoId) {
    abiertoBloqueado(torneoId);
    if (inscripciones.eliminar(torneoId, equipoId) == 0)
      throw new NoEncontradoException("Inscripcion del equipo", equipoId);
  }

  private Torneo abiertoBloqueado(Long id) {
    var torneo = torneos.bloquear(id).orElseThrow(() -> new NoEncontradoException("Torneo", id));
    if (torneo.estado() != EstadoTorneo.INSCRIPCION)
      throw new ReglaNegocioException("Las inscripciones estan cerradas");
    return torneo;
  }
}

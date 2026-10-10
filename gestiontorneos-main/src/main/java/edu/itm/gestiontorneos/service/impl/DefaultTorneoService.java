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
public class DefaultTorneoService implements TorneoService {
  private final TorneoRepository torneos;
  private final DeporteRepository deportes;
  private final InscripcionRepository inscripciones;

  public DefaultTorneoService(
      TorneoRepository torneos, DeporteRepository deportes, InscripcionRepository inscripciones) {
    this.torneos = torneos;
    this.deportes = deportes;
    this.inscripciones = inscripciones;
  }

  public List<Torneo> listar() {
    return torneos.listar();
  }

  public Torneo obtener(Long id) {
    return torneos.buscar(id).orElseThrow(() -> new NoEncontradoException("Torneo", id));
  }

  @Transactional
  public Torneo crear(TorneoRequest r) {
    validarDeporte(r.deporteId());
    return torneos.crear(convertir(null, r));
  }

  @Transactional
  public Torneo actualizar(Long id, TorneoRequest r) {
    var torneo = bloquear(id);
    exigirInscripcion(torneo);
    long inscritos = inscripciones.contar(id);
    if (inscritos > r.cupoEquipos())
      throw new ReglaNegocioException("El cupo no puede ser menor a los equipos inscritos");
    if (inscritos > 0 && !torneo.deporteId().equals(r.deporteId()))
      throw new ReglaNegocioException("No se cambia el deporte de un torneo con inscripciones");
    validarDeporte(r.deporteId());
    return torneos.actualizar(convertir(id, r));
  }

  @Transactional
  public void eliminar(Long id) {
    exigirInscripcion(bloquear(id));
    torneos.eliminar(id);
  }

  @Transactional
  public Torneo iniciar(Long id) {
    var torneo = bloquear(id);
    exigirInscripcion(torneo);
    if (inscripciones.contar(id) < 2)
      throw new ReglaNegocioException("Se necesitan al menos dos equipos para iniciar");
    torneos.cambiarEstado(id, EstadoTorneo.EN_CURSO);
    return obtener(id);
  }

  @Transactional
  public Torneo finalizar(Long id) {
    if (bloquear(id).estado() != EstadoTorneo.EN_CURSO)
      throw new ReglaNegocioException("Solo se finaliza un torneo en curso");
    torneos.cambiarEstado(id, EstadoTorneo.FINALIZADO);
    return obtener(id);
  }

  private Torneo bloquear(Long id) {
    return torneos.bloquear(id).orElseThrow(() -> new NoEncontradoException("Torneo", id));
  }

  private void exigirInscripcion(Torneo torneo) {
    if (torneo.estado() != EstadoTorneo.INSCRIPCION)
      throw new ReglaNegocioException("El torneo ya no esta en inscripcion");
  }

  private void validarDeporte(Long id) {
    deportes.buscar(id).orElseThrow(() -> new NoEncontradoException("Deporte", id));
  }

  private Torneo convertir(Long id, TorneoRequest r) {
    return new Torneo(
        id,
        r.nombre().strip(),
        r.deporteId(),
        r.cupoEquipos(),
        r.fechaInicio(),
        EstadoTorneo.INSCRIPCION);
  }
}

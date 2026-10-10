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
public class DefaultEquipoService implements EquipoService {
  private final EquipoRepository equipos;
  private final DeporteRepository deportes;

  public DefaultEquipoService(EquipoRepository equipos, DeporteRepository deportes) {
    this.equipos = equipos;
    this.deportes = deportes;
  }

  public List<Equipo> listar() {
    return equipos.listar();
  }

  public Equipo obtener(Long id) {
    return equipos.buscar(id).orElseThrow(() -> new NoEncontradoException("Equipo", id));
  }

  @Transactional
  public Equipo crear(EquipoRequest r) {
    validar(r);
    return equipos.crear(convertir(null, r));
  }

  @Transactional
  public Equipo actualizar(Long id, EquipoRequest r) {
    equipos.bloquear(id).orElseThrow(() -> new NoEncontradoException("Equipo", id));
    if (equipos.tieneInscripciones(id))
      throw new ReglaNegocioException(
          "No se modifica un equipo inscrito; retire primero su inscripcion");
    validar(r);
    return equipos.actualizar(convertir(id, r));
  }

  @Transactional
  public void eliminar(Long id) {
    obtener(id);
    equipos.eliminar(id);
  }

  private void validar(EquipoRequest r) {
    var deporte =
        deportes
            .bloquear(r.deporteId())
            .orElseThrow(() -> new NoEncontradoException("Deporte", r.deporteId()));
    if (r.cantidadJugadores() < deporte.minJugadores()
        || r.cantidadJugadores() > deporte.maxJugadores())
      throw new ReglaNegocioException("Cantidad de jugadores fuera del rango del deporte");
  }

  private Equipo convertir(Long id, EquipoRequest r) {
    return new Equipo(id, r.nombre().strip(), r.deporteId(), r.cantidadJugadores());
  }
}

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
public class DefaultDeporteService implements DeporteService {
  private final DeporteRepository deportes;

  public DefaultDeporteService(DeporteRepository deportes) {
    this.deportes = deportes;
  }

  public List<Deporte> listar() {
    return deportes.listar();
  }

  public Deporte obtener(Long id) {
    return deportes.buscar(id).orElseThrow(() -> new NoEncontradoException("Deporte", id));
  }

  @Transactional
  public Deporte crear(DeporteRequest r) {
    validar(r);
    return deportes.crear(convertir(null, r));
  }

  @Transactional
  public Deporte actualizar(Long id, DeporteRequest r) {
    deportes.bloquear(id).orElseThrow(() -> new NoEncontradoException("Deporte", id));
    validar(r);
    if (deportes.existenEquiposFueraDeRango(id, r.minJugadores(), r.maxJugadores()))
      throw new ReglaNegocioException(
          "El nuevo rango dejaria equipos existentes fuera de los limites");
    return deportes.actualizar(convertir(id, r));
  }

  @Transactional
  public void eliminar(Long id) {
    obtener(id);
    deportes.eliminar(id);
  }

  private void validar(DeporteRequest r) {
    if (r.minJugadores() <= 0 || r.maxJugadores() < r.minJugadores())
      throw new ReglaNegocioException(
          "El maximo debe ser mayor o igual al minimo y el minimo positivo");
  }

  private Deporte convertir(Long id, DeporteRequest r) {
    return new Deporte(id, r.nombre().strip(), r.minJugadores(), r.maxJugadores());
  }
}

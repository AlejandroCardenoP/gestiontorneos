package edu.itm.gestiontorneos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.itm.gestiontorneos.domain.*;
import edu.itm.gestiontorneos.repository.*;
import edu.itm.gestiontorneos.service.dto.*;
import edu.itm.gestiontorneos.service.impl.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.*;

class TorneoServiceTest {
  TorneoRepository repo;
  DeporteRepository deportes;
  InscripcionRepository inscripciones;
  DefaultTorneoService service;
  Torneo t = new Torneo(3L, "Copa", 1L, 2, LocalDate.of(2027, 1, 1), EstadoTorneo.INSCRIPCION);
  TorneoRequest valid = new TorneoRequest(" Copa ", 1L, 2, LocalDate.of(2027, 1, 1));

  @BeforeEach
  void setup() {
    repo = mock(TorneoRepository.class);
    deportes = mock(DeporteRepository.class);
    inscripciones = mock(InscripcionRepository.class);
    service = new DefaultTorneoService(repo, deportes, inscripciones);
  }

  Torneo estado(EstadoTorneo e) {
    return new Torneo(t.id(), t.nombre(), t.deporteId(), t.cupoEquipos(), t.fechaInicio(), e);
  }

  void deporte() {
    when(deportes.buscar(1L)).thenReturn(Optional.of(new Deporte(1L, "Futbol", 5, 11)));
  }

  @Test
  void listar() {
    when(repo.listar()).thenReturn(List.of(t));
    assertEquals(List.of(t), service.listar());
  }

  @Test
  void obtener() {
    when(repo.buscar(3L)).thenReturn(Optional.of(t));
    assertEquals(t, service.obtener(3L));
  }

  @Test
  void obtenerAusente() {
    assertThrows(NoEncontradoException.class, () -> service.obtener(3L));
  }

  @Test
  void crear() {
    deporte();
    when(repo.crear(any())).thenReturn(t);
    assertEquals(t, service.crear(valid));
    verify(repo).crear(new Torneo(null, "Copa", 1L, 2, t.fechaInicio(), EstadoTorneo.INSCRIPCION));
  }

  @Test
  void crearSinDeporte() {
    assertThrows(NoEncontradoException.class, () -> service.crear(valid));
    verifyNoInteractions(repo);
  }

  @Test
  void actualizar() {
    deporte();
    when(repo.bloquear(3L)).thenReturn(Optional.of(t));
    when(repo.actualizar(t)).thenReturn(t);
    assertEquals(t, service.actualizar(3L, valid));
  }

  @Test
  void actualizarAusente() {
    assertThrows(NoEncontradoException.class, () -> service.actualizar(3L, valid));
  }

  @Test
  void actualizarCerrado() {
    when(repo.bloquear(3L)).thenReturn(Optional.of(estado(EstadoTorneo.EN_CURSO)));
    assertThrows(ReglaNegocioException.class, () -> service.actualizar(3L, valid));
  }

  @Test
  void actualizarCupoInsuficiente() {
    when(repo.bloquear(3L)).thenReturn(Optional.of(t));
    when(inscripciones.contar(3L)).thenReturn(3L);
    assertThrows(ReglaNegocioException.class, () -> service.actualizar(3L, valid));
    verify(repo, never()).actualizar(any());
  }

  @Test
  void actualizarDeporteConInscritos() {
    when(repo.bloquear(3L)).thenReturn(Optional.of(t));
    when(inscripciones.contar(3L)).thenReturn(1L);
    assertThrows(
        ReglaNegocioException.class,
        () -> service.actualizar(3L, new TorneoRequest("C", 2L, 2, t.fechaInicio())));
  }

  @Test
  void eliminar() {
    when(repo.bloquear(3L)).thenReturn(Optional.of(t));
    service.eliminar(3L);
    verify(repo).eliminar(3L);
  }

  @Test
  void eliminarAusente() {
    assertThrows(NoEncontradoException.class, () -> service.eliminar(3L));
  }

  @Test
  void eliminarCerrado() {
    when(repo.bloquear(3L)).thenReturn(Optional.of(estado(EstadoTorneo.FINALIZADO)));
    assertThrows(ReglaNegocioException.class, () -> service.eliminar(3L));
    verify(repo, never()).eliminar(any());
  }

  @Test
  void iniciar() {
    when(repo.bloquear(3L)).thenReturn(Optional.of(t));
    when(inscripciones.contar(3L)).thenReturn(2L);
    when(repo.buscar(3L)).thenReturn(Optional.of(estado(EstadoTorneo.EN_CURSO)));
    assertEquals(EstadoTorneo.EN_CURSO, service.iniciar(3L).estado());
    verify(repo).cambiarEstado(3L, EstadoTorneo.EN_CURSO);
  }

  @Test
  void iniciarInsuficientes() {
    when(repo.bloquear(3L)).thenReturn(Optional.of(t));
    assertThrows(ReglaNegocioException.class, () -> service.iniciar(3L));
    verify(repo, never()).cambiarEstado(any(), any());
  }

  @Test
  void iniciarAusente() {
    assertThrows(NoEncontradoException.class, () -> service.iniciar(3L));
  }

  @Test
  void iniciarCerrado() {
    when(repo.bloquear(3L)).thenReturn(Optional.of(estado(EstadoTorneo.EN_CURSO)));
    assertThrows(ReglaNegocioException.class, () -> service.iniciar(3L));
  }

  @Test
  void finalizar() {
    when(repo.bloquear(3L)).thenReturn(Optional.of(estado(EstadoTorneo.EN_CURSO)));
    when(repo.buscar(3L)).thenReturn(Optional.of(estado(EstadoTorneo.FINALIZADO)));
    assertEquals(EstadoTorneo.FINALIZADO, service.finalizar(3L).estado());
    verify(repo).cambiarEstado(3L, EstadoTorneo.FINALIZADO);
  }

  @Test
  void finalizarNoIniciado() {
    when(repo.bloquear(3L)).thenReturn(Optional.of(t));
    assertThrows(ReglaNegocioException.class, () -> service.finalizar(3L));
    verify(repo, never()).cambiarEstado(any(), any());
  }

  @Test
  void finalizarAusente() {
    assertThrows(NoEncontradoException.class, () -> service.finalizar(3L));
  }
}

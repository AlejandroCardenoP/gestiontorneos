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

class InscripcionServiceTest {
  TorneoRepository torneos;
  EquipoRepository equipos;
  InscripcionRepository repo;
  DefaultInscripcionService service;
  Torneo t = new Torneo(3L, "Copa", 1L, 2, LocalDate.of(2027, 1, 1), EstadoTorneo.INSCRIPCION);
  Inscripcion i = new Inscripcion(4L, 3L, 2L);

  @BeforeEach
  void setup() {
    torneos = mock(TorneoRepository.class);
    equipos = mock(EquipoRepository.class);
    repo = mock(InscripcionRepository.class);
    service = new DefaultInscripcionService(torneos, equipos, repo);
  }

  void abierto() {
    when(torneos.bloquear(3L)).thenReturn(Optional.of(t));
  }

  void equipo() {
    when(equipos.bloquear(2L)).thenReturn(Optional.of(new Equipo(2L, "A", 1L, 7)));
  }

  @Test
  void listar() {
    when(torneos.buscar(3L)).thenReturn(Optional.of(t));
    when(repo.listar(3L)).thenReturn(List.of(i));
    assertEquals(List.of(i), service.listar(3L));
  }

  @Test
  void listarAusente() {
    assertThrows(NoEncontradoException.class, () -> service.listar(3L));
  }

  @Test
  void inscribir() {
    abierto();
    equipo();
    when(repo.crear(3L, 2L)).thenReturn(i);
    assertEquals(i, service.inscribir(3L, 2L));
    var order = inOrder(torneos, repo);
    order.verify(torneos).bloquear(3L);
    order.verify(repo).existe(3L, 2L);
    order.verify(repo).contar(3L);
    order.verify(repo).crear(3L, 2L);
  }

  @Test
  void inscribirTorneoAusente() {
    assertThrows(NoEncontradoException.class, () -> service.inscribir(3L, 2L));
  }

  @Test
  void inscribirEquipoAusente() {
    abierto();
    assertThrows(NoEncontradoException.class, () -> service.inscribir(3L, 2L));
  }

  @Test
  void inscribirCerrado() {
    when(torneos.bloquear(3L))
        .thenReturn(
            Optional.of(new Torneo(3L, "C", 1L, 2, t.fechaInicio(), EstadoTorneo.EN_CURSO)));
    assertThrows(ReglaNegocioException.class, () -> service.inscribir(3L, 2L));
    verifyNoInteractions(equipos, repo);
  }

  @Test
  void inscribirOtroDeporte() {
    abierto();
    when(equipos.bloquear(2L)).thenReturn(Optional.of(new Equipo(2L, "A", 9L, 7)));
    assertThrows(ReglaNegocioException.class, () -> service.inscribir(3L, 2L));
    verify(repo, never()).crear(any(), any());
  }

  @Test
  void inscribirDuplicado() {
    abierto();
    equipo();
    when(repo.existe(3L, 2L)).thenReturn(true);
    assertThrows(ReglaNegocioException.class, () -> service.inscribir(3L, 2L));
    verify(repo, never()).crear(any(), any());
  }

  @Test
  void inscribirSinCupo() {
    abierto();
    equipo();
    when(repo.contar(3L)).thenReturn(2L);
    assertThrows(ReglaNegocioException.class, () -> service.inscribir(3L, 2L));
    verify(repo, never()).crear(any(), any());
  }

  @Test
  void cancelar() {
    abierto();
    when(repo.eliminar(3L, 2L)).thenReturn(1);
    service.cancelar(3L, 2L);
    verify(repo).eliminar(3L, 2L);
  }

  @Test
  void cancelarAusente() {
    abierto();
    assertThrows(NoEncontradoException.class, () -> service.cancelar(3L, 2L));
  }

  @Test
  void cancelarCerrado() {
    when(torneos.bloquear(3L))
        .thenReturn(
            Optional.of(new Torneo(3L, "C", 1L, 2, t.fechaInicio(), EstadoTorneo.FINALIZADO)));
    assertThrows(ReglaNegocioException.class, () -> service.cancelar(3L, 2L));
    verifyNoInteractions(repo);
  }

  @Test
  void obtenerComprobante() {
    when(torneos.buscar(3L)).thenReturn(Optional.of(t));
    var c = new Comprobante(5L, 4L, "codigo");
    when(repo.comprobante(3L, 2L)).thenReturn(Optional.of(c));
    assertEquals(c, service.obtenerComprobante(3L, 2L));
  }

  @Test
  void comprobanteAusente() {
    when(torneos.buscar(3L)).thenReturn(Optional.of(t));
    assertThrows(NoEncontradoException.class, () -> service.obtenerComprobante(3L, 2L));
  }

  @Test
  void comprobanteTorneoAusente() {
    assertThrows(NoEncontradoException.class, () -> service.obtenerComprobante(3L, 2L));
  }
}

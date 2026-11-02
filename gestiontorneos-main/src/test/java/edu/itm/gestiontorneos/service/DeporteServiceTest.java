package edu.itm.gestiontorneos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.itm.gestiontorneos.domain.*;
import edu.itm.gestiontorneos.repository.*;
import edu.itm.gestiontorneos.service.dto.*;
import edu.itm.gestiontorneos.service.impl.*;
import java.util.*;
import org.junit.jupiter.api.*;

class DeporteServiceTest {
  DeporteRepository repo;
  DefaultDeporteService service;
  Deporte d = new Deporte(1L, "Futbol", 5, 11);
  DeporteRequest valid = new DeporteRequest(" Futbol ", 5, 11);

  @BeforeEach
  void setup() {
    repo = mock(DeporteRepository.class);
    service = new DefaultDeporteService(repo);
  }

  @Test
  void listar() {
    when(repo.listar()).thenReturn(List.of(d));
    assertEquals(List.of(d), service.listar());
  }

  @Test
  void obtener() {
    when(repo.buscar(1L)).thenReturn(Optional.of(d));
    assertEquals(d, service.obtener(1L));
  }

  @Test
  void obtenerAusente() {
    assertThrows(NoEncontradoException.class, () -> service.obtener(1L));
  }

  @Test
  void crear() {
    when(repo.crear(new Deporte(null, "Futbol", 5, 11))).thenReturn(d);
    assertEquals(d, service.crear(valid));
  }

  @Test
  void crearRangoInvalido() {
    assertThrows(ReglaNegocioException.class, () -> service.crear(new DeporteRequest("D", 8, 2)));
    verifyNoInteractions(repo);
  }

  @Test
  void crearMinimoInvalido() {
    assertThrows(ReglaNegocioException.class, () -> service.crear(new DeporteRequest("D", 0, 2)));
  }

  @Test
  void actualizar() {
    when(repo.bloquear(1L)).thenReturn(Optional.of(d));
    when(repo.actualizar(d)).thenReturn(d);
    assertEquals(d, service.actualizar(1L, valid));
  }

  @Test
  void actualizarAusente() {
    assertThrows(NoEncontradoException.class, () -> service.actualizar(1L, valid));
    verify(repo, never()).actualizar(any());
  }

  @Test
  void actualizarNoInvalidaEquipos() {
    when(repo.bloquear(1L)).thenReturn(Optional.of(d));
    when(repo.existenEquiposFueraDeRango(1L, 5, 11)).thenReturn(true);
    assertThrows(ReglaNegocioException.class, () -> service.actualizar(1L, valid));
    verify(repo, never()).actualizar(any());
  }

  @Test
  void eliminar() {
    when(repo.buscar(1L)).thenReturn(Optional.of(d));
    service.eliminar(1L);
    verify(repo).eliminar(1L);
  }

  @Test
  void eliminarAusente() {
    assertThrows(NoEncontradoException.class, () -> service.eliminar(1L));
    verify(repo, never()).eliminar(any());
  }
}

package edu.itm.gestiontorneos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.itm.gestiontorneos.domain.*;
import edu.itm.gestiontorneos.repository.*;
import edu.itm.gestiontorneos.service.dto.*;
import edu.itm.gestiontorneos.service.impl.*;
import java.util.*;
import org.junit.jupiter.api.*;

class EquipoServiceTest {
  EquipoRepository repo;
  DeporteRepository deportes;
  DefaultEquipoService service;
  Equipo e = new Equipo(2L, "A", 1L, 7);
  EquipoRequest valid = new EquipoRequest(" A ", 1L, 7);

  @BeforeEach
  void setup() {
    repo = mock(EquipoRepository.class);
    deportes = mock(DeporteRepository.class);
    service = new DefaultEquipoService(repo, deportes);
  }

  void deporte() {
    when(deportes.bloquear(1L)).thenReturn(Optional.of(new Deporte(1L, "Futbol", 5, 11)));
  }

  @Test
  void listar() {
    when(repo.listar()).thenReturn(List.of(e));
    assertEquals(List.of(e), service.listar());
  }

  @Test
  void obtener() {
    when(repo.buscar(2L)).thenReturn(Optional.of(e));
    assertEquals(e, service.obtener(2L));
  }

  @Test
  void obtenerAusente() {
    assertThrows(NoEncontradoException.class, () -> service.obtener(2L));
  }

  @Test
  void crear() {
    deporte();
    when(repo.crear(new Equipo(null, "A", 1L, 7))).thenReturn(e);
    assertEquals(e, service.crear(valid));
  }

  @Test
  void crearSinDeporte() {
    assertThrows(NoEncontradoException.class, () -> service.crear(valid));
    verifyNoInteractions(repo);
  }

  @Test
  void crearFueraDeRango() {
    deporte();
    for (int cantidad : List.of(1, 12))
      assertThrows(
          ReglaNegocioException.class, () -> service.crear(new EquipoRequest("A", 1L, cantidad)));
    verifyNoInteractions(repo);
  }

  @Test
  void actualizar() {
    deporte();
    when(repo.bloquear(2L)).thenReturn(Optional.of(e));
    when(repo.actualizar(e)).thenReturn(e);
    assertEquals(e, service.actualizar(2L, valid));
  }

  @Test
  void actualizarAusente() {
    assertThrows(NoEncontradoException.class, () -> service.actualizar(2L, valid));
  }

  @Test
  void actualizarInscrito() {
    when(repo.bloquear(2L)).thenReturn(Optional.of(e));
    when(repo.tieneInscripciones(2L)).thenReturn(true);
    assertThrows(ReglaNegocioException.class, () -> service.actualizar(2L, valid));
    verify(repo, never()).actualizar(any());
  }

  @Test
  void eliminar() {
    when(repo.buscar(2L)).thenReturn(Optional.of(e));
    service.eliminar(2L);
    verify(repo).eliminar(2L);
  }

  @Test
  void eliminarAusente() {
    assertThrows(NoEncontradoException.class, () -> service.eliminar(2L));
    verify(repo, never()).eliminar(any());
  }
}

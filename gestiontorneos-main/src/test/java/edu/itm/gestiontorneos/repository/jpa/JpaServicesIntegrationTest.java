package edu.itm.gestiontorneos.repository.jpa;

import static org.junit.jupiter.api.Assertions.*;

import com.zaxxer.hikari.HikariDataSource;
import edu.itm.gestiontorneos.domain.*;
import edu.itm.gestiontorneos.service.*;
import edu.itm.gestiontorneos.service.dto.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import javax.sql.DataSource;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:jpa_contract;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000",
      "spring.datasource.hikari.maximum-pool-size=2"
    })
@ActiveProfiles("test")
class JpaServicesIntegrationTest {
  @Autowired DeporteService deportes;
  @Autowired EquipoService equipos;
  @Autowired TorneoService torneos;
  @Autowired InscripcionService inscripciones;
  @Autowired JdbcTemplate jdbc;
  @Autowired PlatformTransactionManager manager;
  @Autowired DataSource dataSource;
  @PersistenceContext EntityManager em;

  @BeforeEach
  void clean() {
    for (String table :
        new String[] {"comprobante_inscripcion", "inscripcion", "torneo", "equipo", "deporte"})
      jdbc.update("DELETE FROM " + table);
  }

  Deporte deporte() {
    return deportes.crear(new DeporteRequest("Futbol", 5, 11));
  }

  Equipo equipo(long d, String nombre) {
    return equipos.crear(new EquipoRequest(nombre, d, 7));
  }

  Torneo torneo(long d) {
    return torneos.crear(new TorneoRequest("Copa", d, 2, LocalDate.of(2027, 1, 1)));
  }

  @Test
  void todosLosMetodosDeDeportesEnTransaccionJpa() {
    var d = deporte();
    assertEquals(1, deportes.listar().size());
    assertEquals(d, deportes.obtener(d.id()));
    assertEquals("Sala", deportes.actualizar(d.id(), new DeporteRequest("Sala", 5, 10)).nombre());
    deportes.eliminar(d.id());
    assertThrows(NoEncontradoException.class, () -> deportes.obtener(d.id()));
  }

  @Test
  void todosLosMetodosDeEquiposEnTransaccionJpa() {
    var d = deporte();
    var e = equipo(d.id(), "A");
    assertEquals(1, equipos.listar().size());
    assertEquals(e, equipos.obtener(e.id()));
    assertEquals("B", equipos.actualizar(e.id(), new EquipoRequest("B", d.id(), 8)).nombre());
    equipos.eliminar(e.id());
    assertThrows(NoEncontradoException.class, () -> equipos.obtener(e.id()));
  }

  @Test
  void todosLosMetodosDeTorneosEInscripcionesConJpa() {
    var d = deporte();
    var a = equipo(d.id(), "A");
    var b = equipo(d.id(), "B");
    var t = torneo(d.id());
    assertEquals(1, torneos.listar().size());
    assertEquals(t, torneos.obtener(t.id()));
    assertEquals(
        "Copa nueva",
        torneos
            .actualizar(t.id(), new TorneoRequest("Copa nueva", d.id(), 2, t.fechaInicio()))
            .nombre());
    var i = inscripciones.inscribir(t.id(), a.id());
    var c = inscripciones.obtenerComprobante(t.id(), a.id());
    assertEquals(i.id(), c.inscripcionId());
    assertEquals(c, inscripciones.obtenerComprobante(t.id(), a.id()));
    assertEquals(1, inscripciones.listar(t.id()).size());
    new TransactionTemplate(manager)
        .executeWithoutResult(
            status -> {
              var entity = em.find(InscripcionEntity.class, i.id());
              var torneoEntity = org.hibernate.Hibernate.unproxy(entity.torneo, TorneoEntity.class);
              var equipoEntity = org.hibernate.Hibernate.unproxy(entity.equipo, EquipoEntity.class);
              var deporteEntity =
                  org.hibernate.Hibernate.unproxy(equipoEntity.deporte, DeporteEntity.class);
              assertEquals(t.id(), torneoEntity.id);
              assertEquals(a.id(), equipoEntity.id);
              assertEquals(c.codigo(), entity.comprobante.codigo);
              assertEquals(1, torneoEntity.inscripciones.size());
              assertEquals(1, equipoEntity.inscripciones.size());
              assertEquals(2, deporteEntity.equipos.size());
              assertEquals(1, deporteEntity.torneos.size());
            });
    inscripciones.cancelar(t.id(), a.id());
    assertEquals(
        0, jdbc.queryForObject("SELECT COUNT(id) FROM comprobante_inscripcion", Integer.class));
    assertThrows(
        NoEncontradoException.class, () -> inscripciones.obtenerComprobante(t.id(), a.id()));
    inscripciones.inscribir(t.id(), a.id());
    inscripciones.inscribir(t.id(), b.id());
    assertEquals(EstadoTorneo.EN_CURSO, torneos.iniciar(t.id()).estado());
    assertEquals(EstadoTorneo.FINALIZADO, torneos.finalizar(t.id()).estado());
    var vacio = torneo(d.id());
    torneos.eliminar(vacio.id());
    assertThrows(NoEncontradoException.class, () -> torneos.obtener(vacio.id()));
  }

  @Test
  void rollbackAtomicoEntreSqlYJpa() {
    assertTrue(manager instanceof org.springframework.orm.jpa.JpaTransactionManager);
    assertThrows(
        IllegalStateException.class,
        () ->
            new TransactionTemplate(manager)
                .executeWithoutResult(
                    status -> {
                      var d = deporte();
                      torneo(d.id());
                      throw new IllegalStateException("Forzar rollback");
                    }));
    assertTrue(deportes.listar().isEmpty());
    assertTrue(torneos.listar().isEmpty());
  }

  @Test
  void conexionesVuelvenAlPoolTrasExitoYError() {
    for (int n = 0; n < 12; n++) {
      assertTrue(deportes.listar().isEmpty());
      assertTrue(torneos.listar().isEmpty());
      assertThrows(NoEncontradoException.class, () -> torneos.iniciar(999L));
      assertThrows(
          org.springframework.dao.DataAccessException.class,
          () -> jdbc.queryForObject("SELECT columna_inexistente FROM deporte", String.class));
    }
    assertEquals(0, ((HikariDataSource) dataSource).getHikariPoolMXBean().getActiveConnections());
  }
}

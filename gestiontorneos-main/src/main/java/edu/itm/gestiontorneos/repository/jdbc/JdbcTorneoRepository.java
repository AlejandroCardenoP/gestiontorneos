package edu.itm.gestiontorneos.repository.jdbc;

import edu.itm.gestiontorneos.domain.*;
import edu.itm.gestiontorneos.repository.TorneoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTorneoRepository implements TorneoRepository {
  private static final String SELECT =
      "SELECT id, nombre, deporte_id, cupo_equipos, fecha_inicio, estado FROM torneo";
  private static final RowMapper<Torneo> MAPPER =
      (rs, row) ->
          new Torneo(
              rs.getLong("id"),
              rs.getString("nombre"),
              rs.getLong("deporte_id"),
              rs.getInt("cupo_equipos"),
              rs.getDate("fecha_inicio").toLocalDate(),
              EstadoTorneo.valueOf(rs.getString("estado")));
  private final JdbcTemplate jdbc;

  public JdbcTorneoRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public List<Torneo> listar() {
    return jdbc.query(SELECT + " ORDER BY id", MAPPER);
  }

  public Optional<Torneo> buscar(Long id) {
    return jdbc.query(SELECT + " WHERE id=?", MAPPER, id).stream().findFirst();
  }

  public Optional<Torneo> bloquear(Long id) {
    return jdbc.query(SELECT + " WHERE id=? FOR UPDATE", MAPPER, id).stream().findFirst();
  }

  public Torneo crear(Torneo d) {
    Long id =
        JdbcInsert.ejecutar(
            jdbc,
            "INSERT INTO torneo (nombre, deporte_id, cupo_equipos, fecha_inicio, estado) VALUES (?,"
                + " ?, ?, ?, ?)",
            d.nombre(),
            d.deporteId(),
            d.cupoEquipos(),
            java.sql.Date.valueOf(d.fechaInicio()),
            d.estado().name());
    return new Torneo(id, d.nombre(), d.deporteId(), d.cupoEquipos(), d.fechaInicio(), d.estado());
  }

  public Torneo actualizar(Torneo d) {
    jdbc.update(
        "UPDATE torneo SET nombre=?, deporte_id=?, cupo_equipos=?, fecha_inicio=?, estado=? WHERE"
            + " id=?",
        d.nombre(),
        d.deporteId(),
        d.cupoEquipos(),
        java.sql.Date.valueOf(d.fechaInicio()),
        d.estado().name(),
        d.id());
    return d;
  }

  public void eliminar(Long id) {
    jdbc.update("DELETE FROM torneo WHERE id=?", id);
  }

  public void cambiarEstado(Long id, EstadoTorneo estado) {
    jdbc.update("UPDATE torneo SET estado=? WHERE id=?", estado.name(), id);
  }
}

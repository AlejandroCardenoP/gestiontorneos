package edu.itm.gestiontorneos.repository.jdbc;

import edu.itm.gestiontorneos.domain.*;
import edu.itm.gestiontorneos.repository.EquipoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcEquipoRepository implements EquipoRepository {
  private static final String SELECT =
      "SELECT id, nombre, deporte_id, cantidad_jugadores FROM equipo";
  private static final RowMapper<Equipo> MAPPER =
      (rs, row) ->
          new Equipo(
              rs.getLong("id"),
              rs.getString("nombre"),
              rs.getLong("deporte_id"),
              rs.getInt("cantidad_jugadores"));
  private final JdbcTemplate jdbc;

  public JdbcEquipoRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public List<Equipo> listar() {
    return jdbc.query(SELECT + " ORDER BY id", MAPPER);
  }

  public Optional<Equipo> buscar(Long id) {
    return jdbc.query(SELECT + " WHERE id=?", MAPPER, id).stream().findFirst();
  }

  public Optional<Equipo> bloquear(Long id) {
    return jdbc.query(SELECT + " WHERE id=? FOR UPDATE", MAPPER, id).stream().findFirst();
  }

  public Equipo crear(Equipo d) {
    Long id =
        JdbcInsert.ejecutar(
            jdbc,
            "INSERT INTO equipo (nombre, deporte_id, cantidad_jugadores) VALUES (?, ?, ?)",
            d.nombre(),
            d.deporteId(),
            d.cantidadJugadores());
    return new Equipo(id, d.nombre(), d.deporteId(), d.cantidadJugadores());
  }

  public Equipo actualizar(Equipo d) {
    jdbc.update(
        "UPDATE equipo SET nombre=?, deporte_id=?, cantidad_jugadores=? WHERE id=?",
        d.nombre(),
        d.deporteId(),
        d.cantidadJugadores(),
        d.id());
    return d;
  }

  public void eliminar(Long id) {
    jdbc.update("DELETE FROM equipo WHERE id=?", id);
  }

  public boolean tieneInscripciones(Long id) {
    return jdbc.queryForObject("SELECT COUNT(*) FROM inscripcion WHERE equipo_id=?", Long.class, id)
        > 0;
  }
}

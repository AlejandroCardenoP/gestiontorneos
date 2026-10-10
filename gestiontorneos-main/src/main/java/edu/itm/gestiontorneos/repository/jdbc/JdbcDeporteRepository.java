package edu.itm.gestiontorneos.repository.jdbc;

import edu.itm.gestiontorneos.domain.*;
import edu.itm.gestiontorneos.repository.DeporteRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcDeporteRepository implements DeporteRepository {
  private static final String SELECT =
      "SELECT id, nombre, min_jugadores, max_jugadores FROM deporte";
  private static final RowMapper<Deporte> MAPPER =
      (rs, row) ->
          new Deporte(
              rs.getLong("id"),
              rs.getString("nombre"),
              rs.getInt("min_jugadores"),
              rs.getInt("max_jugadores"));
  private final JdbcTemplate jdbc;

  public JdbcDeporteRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public List<Deporte> listar() {
    return jdbc.query(SELECT + " ORDER BY id", MAPPER);
  }

  public Optional<Deporte> buscar(Long id) {
    return jdbc.query(SELECT + " WHERE id=?", MAPPER, id).stream().findFirst();
  }

  public Optional<Deporte> bloquear(Long id) {
    return jdbc.query(SELECT + " WHERE id=? FOR UPDATE", MAPPER, id).stream().findFirst();
  }

  public Deporte crear(Deporte d) {
    Long id =
        JdbcInsert.ejecutar(
            jdbc,
            "INSERT INTO deporte (nombre, min_jugadores, max_jugadores) VALUES (?, ?, ?)",
            d.nombre(),
            d.minJugadores(),
            d.maxJugadores());
    return new Deporte(id, d.nombre(), d.minJugadores(), d.maxJugadores());
  }

  public Deporte actualizar(Deporte d) {
    jdbc.update(
        "UPDATE deporte SET nombre=?, min_jugadores=?, max_jugadores=? WHERE id=?",
        d.nombre(),
        d.minJugadores(),
        d.maxJugadores(),
        d.id());
    return d;
  }

  public void eliminar(Long id) {
    jdbc.update("DELETE FROM deporte WHERE id=?", id);
  }

  public boolean existenEquiposFueraDeRango(Long id, int minimo, int maximo) {
    return jdbc.queryForObject(
            "SELECT COUNT(*) FROM equipo WHERE deporte_id=? AND (cantidad_jugadores<? OR"
                + " cantidad_jugadores>?)",
            Long.class,
            id,
            minimo,
            maximo)
        > 0;
  }
}

package edu.itm.gestiontorneos.repository.jdbc;

import edu.itm.gestiontorneos.domain.Inscripcion;
import edu.itm.gestiontorneos.repository.InscripcionRepository;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcInscripcionRepository implements InscripcionRepository {
  private final JdbcTemplate jdbc;

  public JdbcInscripcionRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public List<Inscripcion> listar(Long torneoId) {
    return jdbc.query(
        "SELECT id, torneo_id, equipo_id FROM inscripcion WHERE torneo_id=? ORDER BY id",
        (rs, row) ->
            new Inscripcion(rs.getLong("id"), rs.getLong("torneo_id"), rs.getLong("equipo_id")),
        torneoId);
  }

  public long contar(Long torneoId) {
    return jdbc.queryForObject(
        "SELECT COUNT(*) FROM inscripcion WHERE torneo_id=?", Long.class, torneoId);
  }

  public boolean existe(Long torneoId, Long equipoId) {
    return jdbc.queryForObject(
            "SELECT COUNT(*) FROM inscripcion WHERE torneo_id=? AND equipo_id=?",
            Long.class,
            torneoId,
            equipoId)
        > 0;
  }

  public Inscripcion crear(Long torneoId, Long equipoId) {
    Long id =
        JdbcInsert.ejecutar(
            jdbc,
            "INSERT INTO inscripcion (torneo_id, equipo_id) VALUES (?, ?)",
            torneoId,
            equipoId);
    return new Inscripcion(id, torneoId, equipoId);
  }

  public int eliminar(Long torneoId, Long equipoId) {
    return jdbc.update(
        "DELETE FROM inscripcion WHERE torneo_id=? AND equipo_id=?", torneoId, equipoId);
  }
}

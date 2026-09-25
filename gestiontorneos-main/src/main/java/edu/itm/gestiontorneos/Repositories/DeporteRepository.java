package edu.itm.gestiontorneos.Repositories;

import edu.itm.gestiontorneos.Identities.Deporte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class DeporteRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Deporte> obtenerDeportes() {
        String sql = """
                SELECT id, nombre, min_jugadores, max_jugadores
                FROM deporte
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, (resultado, fila) ->
                convertirDeporte(
                        resultado.getLong("id"),
                        resultado.getString("nombre"),
                        resultado.getInt("min_jugadores"),
                        resultado.getInt("max_jugadores")
                )
        );
    }

    public Optional<Deporte> obtenerPorId(Long id) {
        String sql = """
                SELECT id, nombre, min_jugadores, max_jugadores
                FROM deporte
                WHERE id = ?
                """;

        try {
            Deporte deporte = jdbcTemplate.queryForObject(
                    sql,
                    (resultado, fila) -> convertirDeporte(
                            resultado.getLong("id"),
                            resultado.getString("nombre"),
                            resultado.getInt("min_jugadores"),
                            resultado.getInt("max_jugadores")
                    ),
                    id
            );

            return Optional.ofNullable(deporte);
        } catch (EmptyResultDataAccessException error) {
            return Optional.empty();
        }
    }

    public Deporte guardar(Deporte deporte) {
        String sql = """
                INSERT INTO deporte (nombre, min_jugadores, max_jugadores)
                VALUES (?, ?, ?)
                """;

        KeyHolder claveGenerada = new GeneratedKeyHolder();

        jdbcTemplate.update(conexion -> {
            PreparedStatement sentencia = conexion.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            sentencia.setString(1, deporte.getNombre());
            sentencia.setInt(2, deporte.getMinJugadores());
            sentencia.setInt(3, deporte.getMaxJugadores());

            return sentencia;
        }, claveGenerada);

        deporte.setId(claveGenerada.getKey().longValue());
        return deporte;
    }

    public Deporte actualizar(Deporte deporte) {
        String sql = """
                UPDATE deporte
                SET nombre = ?, min_jugadores = ?, max_jugadores = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                deporte.getNombre(),
                deporte.getMinJugadores(),
                deporte.getMaxJugadores(),
                deporte.getId()
        );

        return deporte;
    }

    public void eliminar(Long id) {
        String sql = "DELETE FROM deporte WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private Deporte convertirDeporte(
            Long id,
            String nombre,
            int minJugadores,
            int maxJugadores
    ) {
        return Deporte.builder()
                .id(id)
                .nombre(nombre)
                .minJugadores(minJugadores)
                .maxJugadores(maxJugadores)
                .build();
    }
}
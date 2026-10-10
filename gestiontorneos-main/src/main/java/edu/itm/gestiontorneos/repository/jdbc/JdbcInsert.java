package edu.itm.gestiontorneos.repository.jdbc;

import java.sql.Statement;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

final class JdbcInsert {
  private JdbcInsert() {}

  static Long ejecutar(JdbcTemplate jdbc, String sql, Object... parametros) {
    var clave = new GeneratedKeyHolder();
    jdbc.update(
        conexion -> {
          var sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          for (int i = 0; i < parametros.length; i++) sentencia.setObject(i + 1, parametros[i]);
          return sentencia;
        },
        clave);
    return Objects.requireNonNull(clave.getKey(), "La base no genero un ID").longValue();
  }
}

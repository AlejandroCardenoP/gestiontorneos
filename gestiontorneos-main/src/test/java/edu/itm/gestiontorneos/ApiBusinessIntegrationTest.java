package edu.itm.gestiontorneos;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ApiBusinessIntegrationTest {
  @LocalServerPort int port;
  @Autowired JdbcTemplate jdbc;
  private final HttpClient client = HttpClient.newHttpClient();
  private final JsonMapper json = JsonMapper.builder().build();

  @BeforeEach
  void limpiarSoloBaseDePruebas() {
    for (String tabla : new String[] {"inscripcion", "torneo", "equipo", "deporte"})
      jdbc.update("DELETE FROM " + tabla);
  }

  private HttpResponse<String> request(String method, String path, String body) throws Exception {
    return client.send(
        HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/apiinicial" + path))
            .header("Content-Type", "application/json")
            .method(
                method,
                body == null
                    ? HttpRequest.BodyPublishers.noBody()
                    : HttpRequest.BodyPublishers.ofString(body))
            .build(),
        HttpResponse.BodyHandlers.ofString());
  }

  private JsonNode expect(int status, String method, String path, String body) throws Exception {
    var response = request(method, path, body);
    assertEquals(status, response.statusCode(), response.body());
    return response.body().isBlank() ? null : json.readTree(response.body());
  }

  private long deporte(String name) throws Exception {
    return expect(
            201,
            "POST",
            "/deportes",
            "{\"nombre\":\"" + name + "\",\"minJugadores\":5,\"maxJugadores\":11}")
        .get("id")
        .asLong();
  }

  private long equipo(long deporte, String name) throws Exception {
    return expect(
            201,
            "POST",
            "/equipos",
            "{\"nombre\":\"" + name + "\",\"deporteId\":" + deporte + ",\"cantidadJugadores\":7}")
        .get("id")
        .asLong();
  }

  private long torneo(long deporte) throws Exception {
    return expect(
            201,
            "POST",
            "/torneos",
            "{\"nombre\":\"Copa\",\"deporteId\":"
                + deporte
                + ",\"cupoEquipos\":2,\"fechaInicio\":\"2026-12-01\"}")
        .get("id")
        .asLong();
  }

  private String inscripciones(long torneo) {
    return "/torneos/" + torneo + "/inscripciones";
  }

  private String inscribir(long equipo) {
    return "{\"equipoId\":" + equipo + "}";
  }

  @Test
  void crudDeportesPersisteYValida() throws Exception {
    long id = deporte("Futbol");
    assertEquals(1, expect(200, "GET", "/deportes", null).size());
    expect(
        200,
        "PUT",
        "/deportes/" + id,
        "{\"nombre\":\"Futbol sala\",\"minJugadores\":5,\"maxJugadores\":10}");
    assertEquals(
        "Futbol sala",
        jdbc.queryForObject("SELECT nombre FROM deporte WHERE id=?", String.class, id));
    assertEquals("Futbol sala", expect(200, "GET", "/deportes/" + id, null).get("nombre").asText());
    expect(400, "POST", "/deportes", "{\"nombre\":\" \",\"minJugadores\":1,\"maxJugadores\":2}");
    expect(
        409,
        "POST",
        "/deportes",
        "{\"nombre\":\"Futbol sala\",\"minJugadores\":5,\"maxJugadores\":10}");
    expect(
        409,
        "PUT",
        "/deportes/" + id,
        "{\"nombre\":\"Futbol\",\"minJugadores\":12,\"maxJugadores\":10}");
    expect(204, "DELETE", "/deportes/" + id, null);
    expect(404, "GET", "/deportes/" + id, null);
    assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM deporte", Integer.class));
  }

  @Test
  void inscripcionesAplicanReglasYTransiciones() throws Exception {
    long d = deporte("Futbol"), other = deporte("Baloncesto");
    long a = equipo(d, "A"), b = equipo(d, "B"), c = equipo(d, "C"), wrong = equipo(other, "Otro");
    long t = torneo(d);
    expect(409, "POST", "/torneos/" + t + "/inicio", null);
    expect(409, "POST", inscripciones(t), inscribir(wrong));
    expect(201, "POST", inscripciones(t), inscribir(a));
    expect(409, "POST", inscripciones(t), inscribir(a));
    expect(201, "POST", inscripciones(t), inscribir(b));
    expect(409, "POST", inscripciones(t), inscribir(c));
    assertEquals(2, expect(200, "GET", inscripciones(t), null).size());
    expect(409, "DELETE", "/equipos/" + a, null);
    expect(409, "DELETE", "/torneos/" + t, null);
    expect(204, "DELETE", inscripciones(t) + "/" + b, null);
    expect(201, "POST", inscripciones(t), inscribir(c));
    assertEquals(
        "EN_CURSO", expect(200, "POST", "/torneos/" + t + "/inicio", null).get("estado").asText());
    expect(409, "POST", inscripciones(t), inscribir(b));
    expect(409, "DELETE", inscripciones(t) + "/" + a, null);
    assertEquals(
        "FINALIZADO",
        expect(200, "POST", "/torneos/" + t + "/finalizacion", null).get("estado").asText());
    expect(409, "POST", "/torneos/" + t + "/inicio", null);
  }

  @Test
  void protegeConsistenciaDeEquiposYDeportes() throws Exception {
    long d = deporte("Futbol"), t = torneo(d), a = equipo(d, "Equipo");
    expect(
        409,
        "POST",
        "/equipos",
        "{\"nombre\":\"Invalido\",\"deporteId\":" + d + ",\"cantidadJugadores\":99}");
    expect(
        404,
        "POST",
        "/equipos",
        "{\"nombre\":\"Invalido\",\"deporteId\":999999,\"cantidadJugadores\":7}");
    expect(
        409,
        "PUT",
        "/deportes/" + d,
        "{\"nombre\":\"Futbol\",\"minJugadores\":8,\"maxJugadores\":11}");
    expect(409, "DELETE", "/deportes/" + d, null);
    expect(201, "POST", inscripciones(t), inscribir(a));
    expect(
        409,
        "PUT",
        "/equipos/" + a,
        "{\"nombre\":\"Cambio\",\"deporteId\":" + d + ",\"cantidadJugadores\":8}");
    long other = deporte("Otro");
    expect(
        409,
        "PUT",
        "/torneos/" + t,
        "{\"nombre\":\"Copa\",\"deporteId\":"
            + other
            + ",\"cupoEquipos\":2,\"fechaInicio\":\"2026-12-01\"}");
  }

  @Test
  void crudEquiposYTorneosVacios() throws Exception {
    long d = deporte("Futbol"), a = equipo(d, "A"), t = torneo(d);
    assertEquals(1, expect(200, "GET", "/equipos", null).size());
    assertEquals(1, expect(200, "GET", "/torneos", null).size());
    expect(
        200,
        "PUT",
        "/equipos/" + a,
        "{\"nombre\":\"B\",\"deporteId\":" + d + ",\"cantidadJugadores\":8}");
    assertEquals("B", expect(200, "GET", "/equipos/" + a, null).get("nombre").asText());
    expect(
        200,
        "PUT",
        "/torneos/" + t,
        "{\"nombre\":\"Copa nueva\",\"deporteId\":"
            + d
            + ",\"cupoEquipos\":3,\"fechaInicio\":\"2026-12-02\"}");
    assertEquals(3, expect(200, "GET", "/torneos/" + t, null).get("cupoEquipos").asInt());
    expect(409, "POST", "/torneos/" + t + "/finalizacion", null);
    expect(404, "DELETE", inscripciones(t) + "/" + a, null);
    expect(204, "DELETE", "/equipos/" + a, null);
    expect(204, "DELETE", "/torneos/" + t, null);
  }

  @Test
  void dosSolicitudesNoSobrevendenUltimoCupo() throws Exception {
    long d = deporte("Futbol"),
        a = equipo(d, "A"),
        b = equipo(d, "B"),
        c = equipo(d, "C"),
        t = torneo(d);
    expect(201, "POST", inscripciones(t), inscribir(a));
    var salida = new CountDownLatch(1);
    try (var executor = Executors.newFixedThreadPool(2)) {
      var first =
          executor.submit(
              () -> {
                salida.await();
                return request("POST", inscripciones(t), inscribir(b)).statusCode();
              });
      var second =
          executor.submit(
              () -> {
                salida.await();
                return request("POST", inscripciones(t), inscribir(c)).statusCode();
              });
      salida.countDown();
      var codes =
          new java.util.ArrayList<>(
              java.util.List.of(first.get(15, TimeUnit.SECONDS), second.get(15, TimeUnit.SECONDS)));
      java.util.Collections.sort(codes);
      assertEquals(java.util.List.of(201, 409), codes);
    }
    assertEquals(
        2,
        jdbc.queryForObject(
            "SELECT COUNT(*) FROM inscripcion WHERE torneo_id=?", Integer.class, t));
  }

  @Test
  void documentacionYErroresDeFormato() throws Exception {
    var spec = expect(200, "GET", "/v3/api-docs", null);
    assertTrue(spec.get("paths").has("/deportes"));
    assertTrue(spec.get("paths").has("/equipos"));
    assertTrue(spec.get("paths").has("/torneos/{torneoId}/inscripciones"));
    assertEquals(200, request("GET", "/swagger-ui/index.html", null).statusCode());
    expect(400, "GET", "/deportes/abc", null);
    expect(400, "POST", "/deportes", "{mal-json}");
    expect(
        400,
        "POST",
        "/torneos",
        "{\"nombre\":\"Copa\",\"deporteId\":1,\"cupoEquipos\":1,\"fechaInicio\":\"2026-12-01\"}");
    expect(404, "GET", "/torneos/999999/inscripciones", null);
  }
}

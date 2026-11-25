package edu.itm.gestiontorneos;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FrontendCorsIntegrationTest {
  @LocalServerPort int port;
  private final HttpClient client = HttpClient.newHttpClient();

  private HttpResponse<String> preflight(String origin, String method, String path) throws Exception {
    return client.send(HttpRequest.newBuilder(
        URI.create("http://127.0.0.1:" + port + "/apiinicial" + path))
        .header("Origin", origin)
        .header("Access-Control-Request-Method", method)
        .header("Access-Control-Request-Headers", "content-type")
        .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build(),
        HttpResponse.BodyHandlers.ofString());
  }

  @Test
  void permiteOperacionesDelFrontend() throws Exception {
    for (String path : new String[] {"/deportes", "/equipos/1", "/torneos/1/inscripciones"}) {
      for (String method : new String[] {"GET", "POST", "PUT", "DELETE"}) {
        var response = preflight("http://localhost:5500", method, path);
        assertEquals(200, response.statusCode(), response.body());
        assertEquals("http://localhost:5500",
            response.headers().firstValue("Access-Control-Allow-Origin").orElseThrow());
      }
    }
  }

  @Test
  void rechazaOrigenNoAutorizado() throws Exception {
    assertEquals(403, preflight("https://otro.example", "POST", "/equipos").statusCode());
  }
}

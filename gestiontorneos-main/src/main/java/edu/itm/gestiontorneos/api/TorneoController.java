package edu.itm.gestiontorneos.api;

import edu.itm.gestiontorneos.domain.Torneo;
import edu.itm.gestiontorneos.service.TorneoService;
import edu.itm.gestiontorneos.service.dto.TorneoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/torneos")
@Tag(name = "Torneo")
public class TorneoController {
  private final TorneoService service;

  public TorneoController(TorneoService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "Listar torneos")
  public List<Torneo> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consultar torneo por ID")
  public Torneo obtener(@PathVariable Long id) {
    return service.obtener(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Crear torneo")
  public Torneo crear(@Valid @RequestBody TorneoRequest request) {
    return service.crear(request);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Actualizar torneo")
  public Torneo actualizar(@PathVariable Long id, @Valid @RequestBody TorneoRequest request) {
    return service.actualizar(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Eliminar torneo",
      description = "Devuelve 409 si existen relaciones que impiden borrar")
  public void eliminar(@PathVariable Long id) {
    service.eliminar(id);
  }

  @PostMapping("/{id}/inicio")
  @Operation(
      summary = "Iniciar torneo",
      description = "Requiere al menos dos equipos inscritos; cierra las inscripciones")
  public Torneo iniciar(@PathVariable Long id) {
    return service.iniciar(id);
  }

  @PostMapping("/{id}/finalizacion")
  @Operation(
      summary = "Finalizar torneo",
      description = "Solo permite la transicion EN_CURSO a FINALIZADO")
  public Torneo finalizar(@PathVariable Long id) {
    return service.finalizar(id);
  }
}

package edu.itm.gestiontorneos.api;

import edu.itm.gestiontorneos.domain.Inscripcion;
import edu.itm.gestiontorneos.service.InscripcionService;
import edu.itm.gestiontorneos.service.dto.InscripcionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/torneos/{torneoId}/inscripciones")
@Tag(name = "Inscripciones")
public class InscripcionController {
  private final InscripcionService service;

  public InscripcionController(InscripcionService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "Listar equipos inscritos en un torneo")
  public List<Inscripcion> listar(@PathVariable Long torneoId) {
    return service.listar(torneoId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Inscribir equipo",
      description = "Valida estado, deporte, duplicados y cupos dentro de una transaccion")
  public Inscripcion inscribir(
      @PathVariable Long torneoId, @Valid @RequestBody InscripcionRequest request) {
    return service.inscribir(torneoId, request.equipoId());
  }

  @DeleteMapping("/{equipoId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Cancelar inscripcion",
      description =
          "Solo mientras el torneo esta en INSCRIPCION. El ID de la ruta es el del equipo")
  public void cancelar(@PathVariable Long torneoId, @PathVariable Long equipoId) {
    service.cancelar(torneoId, equipoId);
  }
}

package edu.itm.gestiontorneos.api;

import edu.itm.gestiontorneos.domain.Inscripcion;
import edu.itm.gestiontorneos.service.InscripcionService;
import edu.itm.gestiontorneos.service.dto.InscripcionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ErroresApi
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
  @ApiResponse(
      responseCode = "200",
      description = "Consulta u operacion completada",
      useReturnTypeSchema = true)
  public List<Inscripcion> listar(
      @Parameter(description = "Identificador de torneo", example = "1", required = true)
          @PathVariable
          Long torneoId) {
    return service.listar(torneoId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Inscribir equipo",
      description = "Valida estado, deporte, duplicados y cupos dentro de una transaccion")
  @ApiResponse(responseCode = "201", description = "Registro creado", useReturnTypeSchema = true)
  public Inscripcion inscribir(
      @Parameter(description = "Identificador de torneo", example = "1", required = true)
          @PathVariable
          Long torneoId,
      @Valid @RequestBody InscripcionRequest request) {
    return service.inscribir(torneoId, request.equipoId());
  }

  @GetMapping("/{equipoId}/comprobante")
  @Operation(
      summary = "Consultar comprobante unico de una inscripcion",
      description =
          "Devuelve el codigo persistido. El ID de equipo pertenece al torneo indicado; 404 si no"
              + " existe la inscripcion o su comprobante.")
  @ApiResponse(
      responseCode = "200",
      description = "Consulta u operacion completada",
      useReturnTypeSchema = true)
  public edu.itm.gestiontorneos.domain.Comprobante comprobante(
      @Parameter(description = "Identificador de torneo", example = "1", required = true)
          @PathVariable
          Long torneoId,
      @Parameter(description = "Identificador de equipo", example = "1", required = true)
          @PathVariable
          Long equipoId) {
    return service.obtenerComprobante(torneoId, equipoId);
  }

  @DeleteMapping("/{equipoId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Cancelar inscripcion",
      description =
          "Solo mientras el torneo esta en INSCRIPCION. El ID de la ruta es el del equipo")
  @ApiResponse(
      responseCode = "204",
      description = "Operacion completada sin cuerpo",
      content = @Content)
  public void cancelar(
      @Parameter(description = "Identificador de torneo", example = "1", required = true)
          @PathVariable
          Long torneoId,
      @Parameter(description = "Identificador de equipo", example = "1", required = true)
          @PathVariable
          Long equipoId) {
    service.cancelar(torneoId, equipoId);
  }
}

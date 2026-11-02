package edu.itm.gestiontorneos.api;

import edu.itm.gestiontorneos.domain.Deporte;
import edu.itm.gestiontorneos.service.DeporteService;
import edu.itm.gestiontorneos.service.dto.DeporteRequest;
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
@RequestMapping("/deportes")
@Tag(name = "Deporte")
public class DeporteController {
  private final DeporteService service;

  public DeporteController(DeporteService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "Listar deportes")
  @ApiResponse(
      responseCode = "200",
      description = "Consulta u operacion completada",
      useReturnTypeSchema = true)
  public List<Deporte> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consultar deporte por ID")
  @ApiResponse(
      responseCode = "200",
      description = "Consulta u operacion completada",
      useReturnTypeSchema = true)
  public Deporte obtener(
      @Parameter(description = "Identificador de registro", example = "1", required = true)
          @PathVariable
          Long id) {
    return service.obtener(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Crear deporte")
  @ApiResponse(responseCode = "201", description = "Registro creado", useReturnTypeSchema = true)
  public Deporte crear(@Valid @RequestBody DeporteRequest request) {
    return service.crear(request);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Actualizar deporte")
  @ApiResponse(
      responseCode = "200",
      description = "Consulta u operacion completada",
      useReturnTypeSchema = true)
  public Deporte actualizar(
      @Parameter(description = "Identificador de registro", example = "1", required = true)
          @PathVariable
          Long id,
      @Valid @RequestBody DeporteRequest request) {
    return service.actualizar(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Eliminar deporte",
      description = "Devuelve 409 si existen relaciones que impiden borrar")
  @ApiResponse(
      responseCode = "204",
      description = "Operacion completada sin cuerpo",
      content = @Content)
  public void eliminar(
      @Parameter(description = "Identificador de registro", example = "1", required = true)
          @PathVariable
          Long id) {
    service.eliminar(id);
  }
}

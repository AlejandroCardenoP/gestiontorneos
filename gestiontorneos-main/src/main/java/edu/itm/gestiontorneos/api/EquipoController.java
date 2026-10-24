package edu.itm.gestiontorneos.api;

import edu.itm.gestiontorneos.domain.Equipo;
import edu.itm.gestiontorneos.service.EquipoService;
import edu.itm.gestiontorneos.service.dto.EquipoRequest;
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
@RequestMapping("/equipos")
@Tag(name = "Equipo")
public class EquipoController {
  private final EquipoService service;

  public EquipoController(EquipoService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "Listar equipos")
  @ApiResponse(
      responseCode = "200",
      description = "Consulta u operacion completada",
      useReturnTypeSchema = true)
  public List<Equipo> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consultar equipo por ID")
  @ApiResponse(
      responseCode = "200",
      description = "Consulta u operacion completada",
      useReturnTypeSchema = true)
  public Equipo obtener(
      @Parameter(description = "Identificador de registro", example = "1", required = true)
          @PathVariable
          Long id) {
    return service.obtener(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Crear equipo")
  @ApiResponse(responseCode = "201", description = "Registro creado", useReturnTypeSchema = true)
  public Equipo crear(@Valid @RequestBody EquipoRequest request) {
    return service.crear(request);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Actualizar equipo")
  @ApiResponse(
      responseCode = "200",
      description = "Consulta u operacion completada",
      useReturnTypeSchema = true)
  public Equipo actualizar(
      @Parameter(description = "Identificador de registro", example = "1", required = true)
          @PathVariable
          Long id,
      @Valid @RequestBody EquipoRequest request) {
    return service.actualizar(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Eliminar equipo",
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

package edu.itm.gestiontorneos.api;

import edu.itm.gestiontorneos.domain.Deporte;
import edu.itm.gestiontorneos.service.DeporteService;
import edu.itm.gestiontorneos.service.dto.DeporteRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
  public List<Deporte> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consultar deporte por ID")
  public Deporte obtener(@PathVariable Long id) {
    return service.obtener(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Crear deporte")
  public Deporte crear(@Valid @RequestBody DeporteRequest request) {
    return service.crear(request);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Actualizar deporte")
  public Deporte actualizar(@PathVariable Long id, @Valid @RequestBody DeporteRequest request) {
    return service.actualizar(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Eliminar deporte",
      description = "Devuelve 409 si existen relaciones que impiden borrar")
  public void eliminar(@PathVariable Long id) {
    service.eliminar(id);
  }
}

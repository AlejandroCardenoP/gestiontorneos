package edu.itm.gestiontorneos.api;

import edu.itm.gestiontorneos.domain.Equipo;
import edu.itm.gestiontorneos.service.EquipoService;
import edu.itm.gestiontorneos.service.dto.EquipoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
  public List<Equipo> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consultar equipo por ID")
  public Equipo obtener(@PathVariable Long id) {
    return service.obtener(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Crear equipo")
  public Equipo crear(@Valid @RequestBody EquipoRequest request) {
    return service.crear(request);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Actualizar equipo")
  public Equipo actualizar(@PathVariable Long id, @Valid @RequestBody EquipoRequest request) {
    return service.actualizar(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Eliminar equipo",
      description = "Devuelve 409 si existen relaciones que impiden borrar")
  public void eliminar(@PathVariable Long id) {
    service.eliminar(id);
  }
}

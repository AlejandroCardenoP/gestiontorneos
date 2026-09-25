package edu.itm.gestiontorneos.Controllers;

import edu.itm.gestiontorneos.Identities.Deporte;
import edu.itm.gestiontorneos.Services.DeporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deportes")
public class DeporteController {

    @Autowired
    private DeporteService deporteService;

    @GetMapping
    public List<Deporte> obtenerDeportes() {
        return deporteService.listarDeportes();
    }

    @GetMapping("/{id}")
    public Deporte obtenerDeportePorId(@PathVariable Long id) {
        return deporteService.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Deporte crearDeporte(@RequestBody Deporte deporte) {
        return deporteService.crearDeporte(deporte);
    }

    @PutMapping("/{id}")
    public Deporte actualizarDeporte(
            @PathVariable Long id,
            @RequestBody Deporte deporte
    ) {
        return deporteService.actualizarDeporte(id, deporte);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarDeporte(@PathVariable Long id) {
        deporteService.eliminarDeporte(id);
    }
}

package edu.itm.gestiontorneos.Controllers;

import edu.itm.gestiontorneos.Identities.Deporte;
import edu.itm.gestiontorneos.Services.DeporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
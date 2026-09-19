package edu.itm.gestiontorneos.Services;

import edu.itm.gestiontorneos.Identities.Deporte;
import edu.itm.gestiontorneos.Repositories.DeporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeporteService {

    @Autowired
    private DeporteRepository deporteRepository;

    public List<Deporte> listarDeportes() {
        return deporteRepository.obtenerDeportes();
    }
}
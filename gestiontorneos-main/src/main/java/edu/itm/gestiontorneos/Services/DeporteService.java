package edu.itm.gestiontorneos.Services;

import edu.itm.gestiontorneos.Identities.Deporte;
import edu.itm.gestiontorneos.Repositories.DeporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DeporteService {

    @Autowired
    private DeporteRepository deporteRepository;

    public List<Deporte> listarDeportes() {
        return deporteRepository.obtenerDeportes();
    }

    public Deporte obtenerPorId(Long id) {
        return deporteRepository.obtenerPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un deporte con el id " + id
                ));
    }

    public Deporte crearDeporte(Deporte deporte) {
        validarDeporte(deporte);

        boolean existe = deporteRepository.obtenerDeportes().stream()
                .anyMatch(item -> item.getNombre()
                        .equalsIgnoreCase(deporte.getNombre()));

        if (existe) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El deporte ya se encuentra registrado"
            );
        }

        return deporteRepository.guardar(deporte);
    }

    public Deporte actualizarDeporte(Long id, Deporte deporte) {
        obtenerPorId(id);
        validarDeporte(deporte);

        deporte.setId(id);
        return deporteRepository.actualizar(deporte);
    }

    public void eliminarDeporte(Long id) {
        obtenerPorId(id);
        deporteRepository.eliminar(id);
    }

    private void validarDeporte(Deporte deporte) {
        if (deporte.getNombre() == null || deporte.getNombre().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre del deporte es obligatorio"
            );
        }

        if (deporte.getMinJugadores() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El mínimo de jugadores debe ser mayor que cero"
            );
        }

        if (deporte.getMaxJugadores() < deporte.getMinJugadores()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El máximo de jugadores no puede ser menor al mínimo"
            );
        }
    }
}
package edu.itm.gestiontorneos.repository.jpa;

import edu.itm.gestiontorneos.domain.*;
import edu.itm.gestiontorneos.repository.TorneoRepository;
import jakarta.persistence.*;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class JpaTorneoRepository implements TorneoRepository {
  private final EntityManager em;

  public JpaTorneoRepository(EntityManager em) {
    this.em = em;
  }

  public List<Torneo> listar() {
    return em
        .createQuery(
            "select t from TorneoEntity t join fetch t.deporte order by t.id", TorneoEntity.class)
        .getResultList()
        .stream()
        .map(this::domain)
        .toList();
  }

  public Optional<Torneo> buscar(Long id) {
    return Optional.ofNullable(em.find(TorneoEntity.class, id)).map(this::domain);
  }

  public Optional<Torneo> bloquear(Long id) {
    var entity = em.find(TorneoEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
    if (entity != null) em.refresh(entity, LockModeType.PESSIMISTIC_WRITE);
    return Optional.ofNullable(entity).map(this::domain);
  }

  public Torneo crear(Torneo t) {
    var entity = new TorneoEntity();
    copy(t, entity);
    em.persist(entity);
    em.flush();
    return domain(entity);
  }

  public Torneo actualizar(Torneo t) {
    var entity = required(t.id());
    copy(t, entity);
    em.flush();
    return domain(entity);
  }

  public void eliminar(Long id) {
    em.remove(required(id));
    em.flush();
  }

  public void cambiarEstado(Long id, EstadoTorneo estado) {
    required(id).estado = estado;
    em.flush();
  }

  private TorneoEntity required(Long id) {
    return Optional.ofNullable(em.find(TorneoEntity.class, id))
        .orElseThrow(() -> new NoEncontradoException("Torneo", id));
  }

  private void copy(Torneo t, TorneoEntity e) {
    e.nombre = t.nombre();
    e.deporte = em.getReference(DeporteEntity.class, t.deporteId());
    e.cupoEquipos = t.cupoEquipos();
    e.fechaInicio = t.fechaInicio();
    e.estado = t.estado();
  }

  private Torneo domain(TorneoEntity e) {
    return new Torneo(
        e.id,
        e.nombre,
        (Long) em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(e.deporte),
        e.cupoEquipos,
        e.fechaInicio,
        e.estado);
  }
}

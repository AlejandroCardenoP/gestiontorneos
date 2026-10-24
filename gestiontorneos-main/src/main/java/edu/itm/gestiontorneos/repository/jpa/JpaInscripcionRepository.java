package edu.itm.gestiontorneos.repository.jpa;

import edu.itm.gestiontorneos.domain.*;
import edu.itm.gestiontorneos.repository.InscripcionRepository;
import jakarta.persistence.*;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class JpaInscripcionRepository implements InscripcionRepository {
  private final EntityManager em;

  public JpaInscripcionRepository(EntityManager em) {
    this.em = em;
  }

  public List<Inscripcion> listar(Long id) {
    return em
        .createQuery(
            "select i from InscripcionEntity i where i.torneo.id=:id order by i.id",
            InscripcionEntity.class)
        .setParameter("id", id)
        .getResultList()
        .stream()
        .map(this::domain)
        .toList();
  }

  public long contar(Long id) {
    return em.createQuery(
            "select count(i.id) from InscripcionEntity i where i.torneo.id=:id", Long.class)
        .setParameter("id", id)
        .getSingleResult();
  }

  public boolean existe(Long torneoId, Long equipoId) {
    return find(torneoId, equipoId).isPresent();
  }

  public Inscripcion crear(Long torneoId, Long equipoId) {
    var i = new InscripcionEntity();
    i.torneo = em.getReference(TorneoEntity.class, torneoId);
    i.equipo = em.getReference(EquipoEntity.class, equipoId);
    var c = new ComprobanteEntity();
    c.codigo = UUID.randomUUID().toString();
    c.inscripcion = i;
    i.comprobante = c;
    em.persist(i);
    em.flush();
    return domain(i);
  }

  public int eliminar(Long torneoId, Long equipoId) {
    var found = find(torneoId, equipoId);
    if (found.isEmpty()) return 0;
    em.remove(found.get());
    em.flush();
    return 1;
  }

  public Optional<Comprobante> comprobante(Long torneoId, Long equipoId) {
    return find(torneoId, equipoId)
        .map(i -> i.comprobante)
        .map(c -> new Comprobante(c.id, c.inscripcion.id, c.codigo));
  }

  private Optional<InscripcionEntity> find(Long torneoId, Long equipoId) {
    return em
        .createQuery(
            "select i from InscripcionEntity i where i.torneo.id=:torneo and i.equipo.id=:equipo",
            InscripcionEntity.class)
        .setParameter("torneo", torneoId)
        .setParameter("equipo", equipoId)
        .getResultList()
        .stream()
        .findFirst();
  }

  private Long id(Object entity) {
    return (Long) em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
  }

  private Inscripcion domain(InscripcionEntity i) {
    return new Inscripcion(i.id, id(i.torneo), id(i.equipo));
  }
}

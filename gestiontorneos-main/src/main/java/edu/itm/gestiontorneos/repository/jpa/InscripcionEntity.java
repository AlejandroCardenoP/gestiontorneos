package edu.itm.gestiontorneos.repository.jpa;

import jakarta.persistence.*;

@Entity
@Table(
    name = "inscripcion",
    uniqueConstraints = @UniqueConstraint(columnNames = {"torneo_id", "equipo_id"}))
public class InscripcionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "torneo_id", nullable = false)
  TorneoEntity torneo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "equipo_id", nullable = false)
  EquipoEntity equipo;

  @OneToOne(
      mappedBy = "inscripcion",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  ComprobanteEntity comprobante;

  protected InscripcionEntity() {}
}

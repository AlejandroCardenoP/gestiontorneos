package edu.itm.gestiontorneos.repository.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "torneo")
public class TorneoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false, length = 100)
  String nombre;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "deporte_id", nullable = false)
  DeporteEntity deporte;

  @Column(name = "cupo_equipos", nullable = false)
  int cupoEquipos;

  @Column(name = "fecha_inicio", nullable = false)
  java.time.LocalDate fechaInicio;

  @Enumerated(EnumType.STRING)
  @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR)
  @Column(nullable = false, length = 20)
  edu.itm.gestiontorneos.domain.EstadoTorneo estado;

  @OneToMany(mappedBy = "torneo")
  java.util.List<InscripcionEntity> inscripciones = new java.util.ArrayList<>();

  protected TorneoEntity() {}
}

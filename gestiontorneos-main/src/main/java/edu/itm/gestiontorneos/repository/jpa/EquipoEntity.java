package edu.itm.gestiontorneos.repository.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "equipo")
public class EquipoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false, length = 100)
  String nombre;

  @Column(name = "cantidad_jugadores", nullable = false)
  int cantidadJugadores;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "deporte_id", nullable = false)
  DeporteEntity deporte;

  @OneToMany(mappedBy = "equipo")
  java.util.List<InscripcionEntity> inscripciones = new java.util.ArrayList<>();

  protected EquipoEntity() {}
}

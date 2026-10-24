package edu.itm.gestiontorneos.repository.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "deporte")
public class DeporteEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false, length = 100)
  String nombre;

  @Column(name = "min_jugadores", nullable = false)
  int minJugadores;

  @Column(name = "max_jugadores", nullable = false)
  int maxJugadores;

  @OneToMany(mappedBy = "deporte")
  java.util.List<EquipoEntity> equipos = new java.util.ArrayList<>();

  @OneToMany(mappedBy = "deporte")
  java.util.List<TorneoEntity> torneos = new java.util.ArrayList<>();

  protected DeporteEntity() {}
}

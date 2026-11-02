package edu.itm.gestiontorneos.repository.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "comprobante_inscripcion")
public class ComprobanteEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "inscripcion_id", unique = true, nullable = false)
  InscripcionEntity inscripcion;

  @Column(nullable = false, unique = true, length = 36)
  String codigo;

  protected ComprobanteEntity() {}
}

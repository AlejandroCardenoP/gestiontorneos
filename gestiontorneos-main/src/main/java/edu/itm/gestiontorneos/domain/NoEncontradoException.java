package edu.itm.gestiontorneos.domain;

public class NoEncontradoException extends RuntimeException {
  public NoEncontradoException(String entidad, Long id) {
    super(entidad + " no encontrado: " + id);
  }
}

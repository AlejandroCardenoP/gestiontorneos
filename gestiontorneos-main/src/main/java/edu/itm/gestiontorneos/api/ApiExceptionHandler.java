package edu.itm.gestiontorneos.api;

import edu.itm.gestiontorneos.domain.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(NoEncontradoException.class)
  public ProblemDetail noEncontrado(NoEncontradoException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(ReglaNegocioException.class)
  public ProblemDetail conflicto(ReglaNegocioException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail integridad(DataIntegrityViolationException e) {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.CONFLICT, "El registro ya existe o tiene relaciones que impiden la operacion");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail validacion(MethodArgumentNotValidException e) {
    var problema =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Revise los campos enviados");
    problema.setProperty(
        "errores",
        e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .toList());
    return problema;
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MethodArgumentTypeMismatchException.class
  })
  public ProblemDetail formato(Exception e) {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST, "JSON, fecha o identificador invalido");
  }
}

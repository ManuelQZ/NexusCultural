package com.uniquindio.nexuscultural.infraestructure.rest.exception;

import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity> handleNoSuchElement(NoSuchElementException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, "El recurso solicitado no existe");
    }

    @ExceptionHandler(ReglaDominioException.class)
    public ResponseEntity> handleReglaDominio(ReglaDominioException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity> handleIllegalState(IllegalStateException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity> handleIllegalArgument(IllegalArgumentException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity> handleAutenticacion(Exception ex) {
        return construirRespuesta(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now().toString());
        cuerpo.put("status", HttpStatus.BAD_REQUEST.value());
        cuerpo.put("mensaje", "Error de validación en los campos enviados");
        cuerpo.put("errores", errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }

    private ResponseEntity> construirRespuesta(HttpStatus status, String mensaje) {
        Map<String, Object> cuerpo = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "mensaje", mensaje
        );
        return ResponseEntity.status(status).body(cuerpo);
    }
}
package com.dh.notesapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Manejo de errores para recursos no encontrados (por ejemplo, notas con ID inexistente)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNoSuchElementException(NoSuchElementException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "Recurso no encontrado");
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Manejo de errores cuando el tipo de argumento no es válido (por ejemplo, ID no numérico)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "Tipo de argumento incorrecto");
        errorResponse.put("message", "El valor proporcionado no es válido para este campo: " + ex.getName());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Manejo de errores de validación de entrada (ejemplo: campos vacíos o no válidos)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("error", "Error de validación");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage()));

        errors.put("details", fieldErrors);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Manejo genérico de excepciones inesperadas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "Error interno del servidor");
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

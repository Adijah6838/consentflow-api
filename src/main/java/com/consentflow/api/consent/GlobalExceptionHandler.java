package com.consentflow.api.consent;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle invalid state transitions (e.g., trying to revoke an already revoked consent)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Conflict");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409
    }

    // Handle not found or access denied
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Not Found");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404
    }

    // Handle request validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Bad Request");
        
        StringBuilder details = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            details.append(error.getDefaultMessage()).append("; ");
        });
        
        response.put("message", details.toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
    }
}

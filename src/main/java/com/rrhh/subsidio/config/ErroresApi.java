// Paquete donde vive la clase
package com.rrhh.subsidio.config;

import java.util.LinkedHashMap;                          // Respuesta HTTP: codigo + cuerpo
import java.util.Map;     // Error que lanza @Valid cuando falla

import org.springframework.http.ResponseEntity;         // Metodo que atiende una excepcion
import org.springframework.web.bind.MethodArgumentNotValidException;           // Para limitar el alcance a los @RestController
import org.springframework.web.bind.annotation.ExceptionHandler;     // "Asesor" global de los controllers REST
import org.springframework.web.bind.annotation.RestController; // Mapa que conserva el orden de insercion
import org.springframework.web.bind.annotation.RestControllerAdvice;           // { "campo": "mensaje" }

// =====================================================================
//  Manejo de errores de VALIDACION para la API REST (/api/...)
// =====================================================================

@RestControllerAdvice(annotations = RestController.class)
public class ErroresApi {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()                                  // Cada campo que fallo...
                .forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage())); // ...con su mensaje
        return ResponseEntity.badRequest().body(errores);                        // 400 Bad Request
    }
}

package com.rrhh.subsidio.empleado;

// Entidad relacionada
import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rrhh.subsidio.subsidio.Subsidio;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoRepository repo;

    // ===================== READ: listar todos =====================
    @GetMapping("/listar")
    public List<Empleado> listar() {
        return repo.findAll();
    }

    // ===================== READ: listar solo activos =====================
    @GetMapping("/activos")
    public List<Empleado> listarActivos() {
        return repo.findByActivoTrue();
    }

    // ===================== READ: obtener uno =====================
    @GetMapping("/obtener/{id}")
    public ResponseEntity<Empleado> obtener(@PathVariable Integer id) {

        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===================== CREATE: insertar =====================
    @PostMapping("/crear")
    public ResponseEntity<?> crear(@Valid @RequestBody Empleado empleado) {

        // Validar DNI duplicado
        if (repo.existsByDni(empleado.getDni())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un empleado con el DNI: " + empleado.getDni());
        }

        // El ID lo genera la BD
        empleado.setIdEmpleado(null);

        // Estado activo por defecto
        if (empleado.getActivo() == null) {
            empleado.setActivo(true);
        }

        Empleado guardado = repo.save(empleado);

        return ResponseEntity
                .created(URI.create("/api/empleados/obtener/" + guardado.getIdEmpleado()))
                .body(guardado);
    }

    // ===================== UPDATE: actualizar =====================
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Empleado> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody Empleado datos) {

        return repo.findById(id)
                .map(e -> {

                    // Validar DNI duplicado en otro empleado
                    if (repo.existsByDniAndIdEmpleadoNot(
                            datos.getDni(), id)) {
                        throw new IllegalArgumentException(
                                "Ya existe otro empleado con el DNI: " + datos.getDni());
                    }

                    e.setNombreEmpleado(datos.getNombreEmpleado());              
                    e.setDni(datos.getDni());
                    e.setCargo(datos.getCargo());
                    e.setTelefono(datos.getTelefono());

                    if (datos.getActivo() != null) {
                        e.setActivo(datos.getActivo());
                    }

                    return ResponseEntity.ok(repo.save(e));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ===================== DELETE: eliminar =====================
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {

        Empleado empleado = repo.findById(id).orElse(null);

        if (empleado == null) {
            return ResponseEntity.notFound().build();
        }

        // No permitir eliminar empleados que tienen subsidios registrados
        if (!empleado.getSubsidios().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar el empleado porque tiene subsidios registrados.");
        }

        repo.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // =====================================================================
    //                    RELACION: EMPLEADO -> SUBSIDIOS
    // =====================================================================

    // ===================== Consultar subsidios de un empleado =====================
    @GetMapping("/{id}/subsidios")
    public ResponseEntity<List<Subsidio>> listarSubsidios(
            @PathVariable Integer id) {

        return repo.findById(id)
                .map(e -> ResponseEntity.ok(e.getSubsidios()))
                .orElse(ResponseEntity.notFound().build());
    }
}

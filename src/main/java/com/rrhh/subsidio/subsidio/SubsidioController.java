
package com.rrhh.subsidio.subsidio;

// Entidades relacionadas
import com.rrhh.subsidio.empleado.Empleado;
import com.rrhh.subsidio.empleado.EmpleadoRepository;
import com.rrhh.subsidio.tiposubsidio.TipoSubsidio;
import com.rrhh.subsidio.tiposubsidio.TipoSubsidioRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/subsidios")
@RequiredArgsConstructor
public class SubsidioController {

    private final SubsidioRepository repo;
    private final EmpleadoRepository empleadoRepo;
    private final TipoSubsidioRepository tipoSubsidioRepo;

    // ===================== READ: listar todos =====================
    @GetMapping("/listar")
    public List<Subsidio> listar() {
        return repo.findAll();
    }

    // ===================== READ: obtener uno =====================
    @GetMapping("/obtener/{id}")
    public ResponseEntity<Subsidio> obtener(@PathVariable Integer id) {

        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===================== CREATE: insertar =====================
    // JSON de ejemplo:
    // {
    //   "tipoSubsidio": {"idTipoSubsidio": 1},
    //   "nombreEmpleado": {"idEmpleado": 2},
    //   "numeroOrden": 1001,
    //   "fechaDesde": "2026-11-01",
    //   "fechaHasta": "2026-11-15",
    //   "motivo": "Subsidio por enfermedad"
    // }

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@Valid @RequestBody Subsidio subsidio) {

        subsidio.setIdSubsidio(null);

        // Validar y obtener el tipo de subsidio
        if (subsidio.getTipoSubsidio() == null ||
                subsidio.getTipoSubsidio().getIdTipoSubsidio() == null) {

            return ResponseEntity.badRequest()
                    .body("Debe especificar el tipo de subsidio.");
        }

        Integer tipoId = subsidio.getTipoSubsidio().getIdTipoSubsidio();

        TipoSubsidio tipoSubsidio = tipoSubsidioRepo
                .findById(tipoId).orElse(null);

        if (tipoSubsidio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el tipo de subsidio con id: " + tipoId);
        }

        subsidio.setTipoSubsidio(tipoSubsidio);

        // Validar y obtener el empleado
        if (subsidio.getNombreEmpleado() == null ||
                subsidio.getNombreEmpleado().getIdEmpleado() == null) {

            return ResponseEntity.badRequest()
                    .body("Debe especificar el empleado.");
        }

        Integer empleadoId = subsidio.getNombreEmpleado().getIdEmpleado();

        Empleado empleado = empleadoRepo
                .findById(empleadoId).orElse(null);

        if (empleado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el empleado con id: " + empleadoId);
        }

        if (!Boolean.TRUE.equals(empleado.getActivo())) {
            return ResponseEntity.badRequest()
                    .body("No se puede registrar un subsidio para un empleado inactivo.");
        }

        subsidio.setNombreEmpleado(empleado);

        // Guardar subsidio
        Subsidio guardado = repo.save(subsidio);

        return ResponseEntity
                .created(URI.create("/api/subsidios/obtener/" + guardado.getIdSubsidio()))
                .body(guardado);
    }

    // ===================== UPDATE: actualizar =====================
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody Subsidio datos) {

        Subsidio subsidio = repo.findById(id).orElse(null);

        if (subsidio == null) {
            return ResponseEntity.notFound().build();
        }

        // Validar tipo de subsidio
        if (datos.getTipoSubsidio() == null ||
                datos.getTipoSubsidio().getIdTipoSubsidio() == null) {

            return ResponseEntity.badRequest()
                    .body("Debe especificar el tipo de subsidio.");
        }

        Integer tipoId = datos.getTipoSubsidio().getIdTipoSubsidio();

        TipoSubsidio tipoSubsidio = tipoSubsidioRepo
                .findById(tipoId).orElse(null);

        if (tipoSubsidio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el tipo de subsidio con id: " + tipoId);
        }

        // Validar empleado
        if (datos.getNombreEmpleado() == null ||
                datos.getNombreEmpleado().getIdEmpleado() == null) {

            return ResponseEntity.badRequest()
                    .body("Debe especificar el empleado.");
        }

        Integer empleadoId = datos.getNombreEmpleado().getIdEmpleado();

        Empleado empleado = empleadoRepo
                .findById(empleadoId).orElse(null);

        if (empleado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el empleado con id: " + empleadoId);
        }

        if (!Boolean.TRUE.equals(empleado.getActivo())) {
            return ResponseEntity.badRequest()
                    .body("No se puede asignar un subsidio a un empleado inactivo.");
        }

        // Actualizar datos
        subsidio.setTipoSubsidio(tipoSubsidio);
        subsidio.setNombreEmpleado(empleado);
        subsidio.setNumeroOrden(datos.getNumeroOrden());
        subsidio.setFechaDesde(datos.getFechaDesde());
        subsidio.setFechaHasta(datos.getFechaHasta());
        subsidio.setMotivo(datos.getMotivo());

        return ResponseEntity.ok(repo.save(subsidio));
    }

    // ===================== DELETE: eliminar =====================
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repo.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // =====================================================================
    //              RELACION: SUBSIDIO -> EMPLEADO
    // =====================================================================

    // ===================== Cambiar empleado de un subsidio =====================
    @PutMapping("/{id}/empleado/{empleadoId}")
    public ResponseEntity<?> asignarEmpleado(
            @PathVariable Integer id,
            @PathVariable Integer empleadoId) {

        Subsidio subsidio = repo.findById(id).orElse(null);

        if (subsidio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el subsidio con id: " + id);
        }

        Empleado empleado = empleadoRepo.findById(empleadoId).orElse(null);

        if (empleado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el empleado con id: " + empleadoId);
        }

        if (!Boolean.TRUE.equals(empleado.getActivo())) {
            return ResponseEntity.badRequest()
                    .body("No se puede asignar un subsidio a un empleado inactivo.");
        }

        subsidio.setNombreEmpleado(empleado);

        return ResponseEntity.ok(repo.save(subsidio));
    }

    // =====================================================================
    //              RELACION: SUBSIDIO -> TIPO SUBSIDIO
    // =====================================================================

    // ===================== Cambiar tipo de subsidio =====================
    @PutMapping("/{id}/tipo-subsidio/{tipoSubsidioId}")
    public ResponseEntity<?> asignarTipoSubsidio(
            @PathVariable Integer id,
            @PathVariable Integer tipoSubsidioId) {

        Subsidio subsidio = repo.findById(id).orElse(null);

        if (subsidio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el subsidio con id: " + id);
        }

        TipoSubsidio tipoSubsidio = tipoSubsidioRepo
                .findById(tipoSubsidioId).orElse(null);

        if (tipoSubsidio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el tipo de subsidio con id: " + tipoSubsidioId);
        }

        subsidio.setTipoSubsidio(tipoSubsidio);

        return ResponseEntity.ok(repo.save(subsidio));
    }

    // =====================================================================
    //                    CONSULTAS POR RELACION
    // =====================================================================

    // ===================== Subsidios de un empleado =====================
    @GetMapping("/por-empleado/{empleadoId}")
    public ResponseEntity<?> porEmpleado(@PathVariable Integer empleadoId) {

        if (!empleadoRepo.existsById(empleadoId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el empleado con id: " + empleadoId);
        }

        return ResponseEntity.ok(repo.findByNombreEmpleadoIdEmpleado(empleadoId));
    }

    // ===================== Subsidios por tipo =====================
    @GetMapping("/por-tipo/{tipoSubsidioId}")
    public ResponseEntity<?> porTipo(@PathVariable Integer tipoSubsidioId) {

        if (!tipoSubsidioRepo.existsById(tipoSubsidioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe el tipo de subsidio con id: " + tipoSubsidioId);
        }

        return ResponseEntity.ok(repo.findByTipoSubsidioIdTipoSubsidio(tipoSubsidioId));
    }
}

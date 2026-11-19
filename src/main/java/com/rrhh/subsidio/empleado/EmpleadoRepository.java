
package com.rrhh.subsidio.empleado;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// JpaRepository<Entidad, TipoDelId>
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

    // ===================== CONSULTAS DERIVADAS =====================

    // Verificar si existe un empleado con determinado DNI
    boolean existsByDni(String dni);

    // Buscar empleado por DNI
    Optional<Empleado> findByDni(String dni);

    // Listar solamente empleados activos
    List<Empleado> findByActivoTrue();

    // Verificar DNI duplicado excluyendo al empleado actual
    boolean existsByDniAndIdEmpleadoNot(String dni, Integer idEmpleado);

    // Buscar empleados por nombre
    List<Empleado> findByNombreEmpleadoContainingIgnoreCase(String nombreEmpleado);

    // ===================== RELACION: EMPLEADO -> SUBSIDIOS =====================

    // Buscar empleados que tienen subsidios registrados
    List<Empleado> findBySubsidiosIsNotEmpty();

    // Verificar si un empleado tiene subsidios registrados
    boolean existsBySubsidiosIsNotEmpty();

    // ===================== JPQL: JOIN =====================

    // Buscar empleados que tienen subsidios de un tipo determinado
    @Query("""
            SELECT DISTINCT e FROM Empleado e
            JOIN e.subsidios s
            JOIN s.tipoSubsidio ts
            WHERE ts.idTipoSubsidio = :tipoSubsidioId
            """)
    List<Empleado> buscarPorTipoSubsidio(
            @Param("tipoSubsidioId") Integer tipoSubsidioId);

    // ===================== JPQL: LEFT JOIN FETCH =====================

    // Listar empleados junto con sus subsidios y tipos de subsidio
    // LEFT JOIN: incluye empleados que todavía no tienen subsidios.
    // FETCH: carga las relaciones en la misma consulta.
    // DISTINCT: evita empleados duplicados en el resultado.
    @Query("""
            SELECT DISTINCT e FROM Empleado e
            LEFT JOIN FETCH e.subsidios s
            LEFT JOIN FETCH s.tipoSubsidio
            ORDER BY e.nombreEmpleado
            """)
    List<Empleado> listarConSubsidios();

}

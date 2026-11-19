package com.rrhh.subsidio.subsidio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubsidioRepository extends JpaRepository<Subsidio, Integer> {

    // Buscar subsidios asociados a un empleado
    List<Subsidio> findByNombreEmpleadoIdEmpleado(Integer empleadoId);

    // Buscar subsidios por tipo
    List<Subsidio> findByTipoSubsidioIdTipoSubsidio(Integer tipoSubsidioId);

}
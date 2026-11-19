package com.rrhh.subsidio.tiposubsidio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para consultar los tipos de subsidio.
 */
public interface TipoSubsidioRepository extends JpaRepository<TipoSubsidio, Integer> {

    /**
     * Obtiene todos los tipos de subsidio ordenados por descripción.
     */
    List<TipoSubsidio> findAllByOrderByTipoSubsidioAsc();

}
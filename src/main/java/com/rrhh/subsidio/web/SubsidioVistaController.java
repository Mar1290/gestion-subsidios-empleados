// Paquete de las VISTAS (paginas HTML con Thymeleaf)
package com.rrhh.subsidio.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.rrhh.subsidio.empleado.EmpleadoRepository;
import com.rrhh.subsidio.tiposubsidio.TipoSubsidioRepository;

import lombok.RequiredArgsConstructor;

// =====================================================================
//  SUBSIDIOS: Thymeleaf + los ENDPOINTS REST que ya tenemos
// =====================================================================
//  Este controller SOLO entrega la pagina HTML y carga los catalogos
//  necesarios para los <select> de empleados y tipos de subsidio.
//
//  Los datos de la tabla y las operaciones CRUD las realiza el navegador
//  llamando a la API mediante fetch():
//
//    GET    /api/subsidios/listar             -> llena la tabla (DataTables)
//    GET    /api/subsidios/obtener/{id}       -> obtiene un subsidio
//    POST   /api/subsidios/crear              -> registra un subsidio
//    PUT    /api/subsidios/actualizar/{id}    -> actualiza un subsidio
//    DELETE /api/subsidios/eliminar/{id}      -> elimina un subsidio
//
//  Catalogos REST utilizados por subsidio.js:
//
//    GET    /api/empleados/activos
//    GET    /api/tipos-subsidio/listar
//
//  El codigo JavaScript esta en static/js/subsidio.js
//
//  Vista:
//
//    GET    /subsidios -> templates/subsidios.html
// =====================================================================

@Controller
@RequiredArgsConstructor
public class SubsidioVistaController {

    private final EmpleadoRepository empleadoRepo;
    private final TipoSubsidioRepository tipoSubsidioRepo;

    @GetMapping("/subsidios")
    public String subsidios(Model model) {

        // Empleados activos para el <select> de empleados.
        model.addAttribute("empleados", empleadoRepo.findByActivoTrue());

        // Tipos de subsidio para el <select> correspondiente.
        model.addAttribute(
            "tiposSubsidio",
            tipoSubsidioRepo.findAllByOrderByTipoSubsidioAsc()
        );

        return "subsidio"; // templates/subsidios.html
    }
}

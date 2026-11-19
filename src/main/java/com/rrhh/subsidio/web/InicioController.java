package com.rrhh.subsidio.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador MVC para las páginas principales del sistema.
 */
@Controller
public class InicioController {

    /**
     * Página principal de la aplicación.
     *
     * URL: /
     * Vista: templates/index.html
     */
    @GetMapping("/")
    public String inicio() {

        return "index";
    }
}
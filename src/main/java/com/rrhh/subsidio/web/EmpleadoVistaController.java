package com.rrhh.subsidio.web;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmpleadoVistaController {

    @GetMapping("/empleados")
    public String empleados() {

        return "empleado";
    }
}
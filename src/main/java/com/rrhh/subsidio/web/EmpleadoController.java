
// // Paquete de las VISTAS (paginas HTML con Thymeleaf)
// package com.rrhh.subsidio.web;

// import lombok.RequiredArgsConstructor;

// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;

// // =====================================================================
// //  EMPLEADOS: Thymeleaf + los ENDPOINTS REST que ya tenemos
// // =====================================================================
// //  Este controller SOLO entrega la pagina HTML.
// //
// //  Los datos de la tabla y las operaciones CRUD las realiza el navegador
// //  llamando a la API mediante fetch():
// //
// //    GET    /api/empleados/listar          -> llena la tabla (DataTables)
// //    GET    /api/empleados/obtener/{id}    -> obtiene un empleado
// //    GET    /api/empleados/activos         -> lista empleados activos
// //    POST   /api/empleados/crear           -> registra un empleado
// //    PUT    /api/empleados/actualizar/{id} -> actualiza un empleado
// //    DELETE /api/empleados/eliminar/{id}   -> elimina un empleado
// //
// //  El codigo JavaScript esta en static/js/empleado.js
// //
// //  Vista:
// //
// //    GET    /empleados -> templates/empleados.html
// // =====================================================================

// @Controller
// @RequiredArgsConstructor
// public class EmpleadoVistaController {

//     @GetMapping("/empleados")
//     public String empleados() {

//         return "empleados"; // templates/empleados.html
//     }
// }

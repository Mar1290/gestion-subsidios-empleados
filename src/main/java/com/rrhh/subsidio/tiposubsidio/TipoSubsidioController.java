
package com.rrhh.subsidio.tiposubsidio;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/*
 Controlador REST para consultar los tipos de subsidio.
 Su finalidad es proporcionar los datos necesarios
 para cargar el selector de tipos de subsidio en el módulo.
 */
@RestController
@RequestMapping("/api/tipos-subsidio")
@RequiredArgsConstructor
public class TipoSubsidioController {

    private final TipoSubsidioRepository repo;

    /*
     GET: Lista todos los tipos de subsidio.
     Se utiliza para cargar el selector de tipos de subsidio
     en el formulario de registro y actualización de subsidios.
     */
    @GetMapping("/listar")
    public ResponseEntity<List<TipoSubsidio>> listar() {

        List<TipoSubsidio> lista = repo.findAllByOrderByTipoSubsidioAsc();

        return ResponseEntity.ok(lista);
    }

}
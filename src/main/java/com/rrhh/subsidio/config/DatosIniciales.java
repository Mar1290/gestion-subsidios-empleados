
// Paquete donde vive la clase.
package com.rrhh.subsidio.config;

// Entidad TipoSubsidio.
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rrhh.subsidio.tiposubsidio.TipoSubsidio;
import com.rrhh.subsidio.tiposubsidio.TipoSubsidioRepository;

/**
 * Clase encargada de cargar los datos iniciales del sistema.
 *
 * Spring ejecuta el método cargarDatos() automáticamente
 * cuando termina de iniciar la aplicación.
 */
@Configuration
public class DatosIniciales {

    /**
     * Un único CommandLineRunner para ejecutar la carga inicial.
     *
     * Actualmente se cargan los tipos de subsidio porque
     * representan el catálogo necesario para registrar subsidios.
     */
    @Bean
    CommandLineRunner cargarDatos(TipoSubsidioRepository tipoSubsidioRepo) {

        return args -> {

            // Primero se cargan los tipos de subsidio.
            cargarTiposSubsidio(tipoSubsidioRepo);

        };
    }

    // =============================================================
    // DATOS INICIALES: TIPOS DE SUBSIDIO
    // =============================================================
    /**
     * Inserta los tipos de subsidio de ejemplo.
     *
     * Solo se insertan cuando la tabla está vacía.
     * Si ya existen registros, no se modifica ni se duplica información.
     */
    private void cargarTiposSubsidio(TipoSubsidioRepository repo) {

        // Verificar si la tabla ya contiene registros.
        if (repo.count() > 0) {
            return;
        }

        // Insertar los tipos de subsidio iniciales.
        repo.saveAll(List.of(

                crearTipoSubsidio("Enfermedad común"),

                crearTipoSubsidio("Maternidad"),

                crearTipoSubsidio("Riesgo profesional")

        ));

        System.out.println(
                ">>> Tipos de subsidio iniciales insertados: " + repo.count()
        );
    }

    /**
     * Construye un objeto TipoSubsidio.
     *
     * Se utiliza este método para evitar repetir código
     * al crear los registros iniciales.
     */
    private TipoSubsidio crearTipoSubsidio(String descripcion) {

        TipoSubsidio tipo = new TipoSubsidio();

        // El identificador es null porque lo genera la base de datos.
        tipo.setIdTipoSubsidio(null);

        // Descripción del tipo de subsidio.
        tipo.setTipoSubsidio(descripcion);

        return tipo;
    }

}
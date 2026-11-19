
// =====================================================================
// app.js
// Funciones generales compartidas por todas las paginas.
// Sistema de Gestion de Subsidios y Empleados.
//
// Se carga desde:
// templates/fragments/layout.html
// =====================================================================


// =====================================================================
// 1. DATATABLES EN ESPANOL
// =====================================================================

const DT_ES = {
    search: 'Buscar:',
    lengthMenu: 'Mostrar _MENU_ registros',
    info: 'Mostrando _START_ a _END_ de _TOTAL_ registros',
    infoEmpty: 'Mostrando 0 a 0 de 0 registros',
    infoFiltered: '(filtrado de _MAX_ registros)',
    zeroRecords: 'No se encontraron resultados',
    emptyTable: 'No hay datos disponibles',
    loadingRecords: 'Cargando...',
    processing: 'Procesando...',
    paginate: {
        first: 'Primero',
        last: 'Ultimo',
        next: 'Siguiente',
        previous: 'Anterior'
    }
};


// =====================================================================
// 2. NOTIFICACIONES CON NOTIFY.JS
// =====================================================================

// Tipos: success | info | warn | error
function notificar(mensaje, tipo = 'success') {

    if (typeof $ === 'undefined' || !$.notify) {
        console.warn('Notify.js no esta disponible:', mensaje);
        return;
    }

    $.notify(mensaje, {
        className: tipo,
        globalPosition: 'top right',
        autoHideDelay: 3500,
        clickToHide: true
    });
}


// =====================================================================
// 3. CONFIRMACIONES CON SWEETALERT2
// =====================================================================

// Devuelve true cuando el usuario confirma.
async function confirmar(
    titulo,
    texto = 'Esta accion no se puede deshacer.'
) {

    const resultado = await Swal.fire({
        title: titulo,
        text: texto,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Si, continuar',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#B02A37',
        cancelButtonColor: '#6c757d',
        reverseButtons: true,
        focusCancel: true
    });

    return resultado.isConfirmed;
}


// =====================================================================
// 4. MENSAJES DE ERROR CON SWEETALERT2
// =====================================================================

function mostrarError(titulo, error) {

    let mensaje = 'Ocurrio un error inesperado.';

    if (error instanceof Error) {
        mensaje = error.message;
    } else if (error !== undefined && error !== null) {
        mensaje = String(error);
    }

    Swal.fire({
        icon: 'error',
        title: titulo,
        text: mensaje,
        confirmButtonText: 'Aceptar',
        confirmButtonColor: '#B02A37'
    });
}


// =====================================================================
// 5. MENSAJES DE EXITO CON SWEETALERT2
// =====================================================================

function mostrarExito(
    titulo,
    mensaje = 'La operacion se realizo correctamente.'
) {

    return Swal.fire({
        icon: 'success',
        title: titulo,
        text: mensaje,
        confirmButtonText: 'Aceptar',
        confirmButtonColor: '#198754'
    });
}


// =====================================================================
// 6. CONSUMIR ENDPOINTS REST CON FETCH
// =====================================================================

// Metodos soportados:
// GET, POST, PUT, PATCH, DELETE.
//
// cuerpo: objeto JavaScript que se envia como JSON.
//
// Retorna:
// - Objeto JSON cuando existe respuesta.
// - null cuando el servidor responde sin contenido.
//
// Lanza Error cuando el servidor devuelve 400, 404, 409, 500, etc.

async function api(url, metodo = 'GET', cuerpo) {

    const opciones = {
        method: metodo,
        headers: {
            'Accept': 'application/json'
        }
    };

    if (cuerpo !== undefined) {

        opciones.headers['Content-Type'] = 'application/json';

        // Convertir objeto JavaScript a JSON.
        opciones.body = JSON.stringify(cuerpo);
    }

    let respuesta;

    try {

        respuesta = await fetch(url, opciones);

    } catch (error) {

        throw new Error(
            'No fue posible conectar con el servidor. Verifique que la aplicacion este en funcionamiento.'
        );
    }

    // Leer respuesta del servidor.
    const texto = await respuesta.text();

    // Si la respuesta es exitosa (200-299).
    if (respuesta.ok) {

        if (!texto) {
            return null;
        }

        try {
            return JSON.parse(texto);
        } catch {
            return texto;
        }
    }

    // Procesar respuestas de error.
    let mensaje = texto || `Error HTTP ${respuesta.status}`;

    if (texto) {

        try {

            const json = JSON.parse(texto);

            if (json.message) {
                mensaje = json.message;
            } else if (json.detail) {
                mensaje = json.detail;
            } else if (json.error && json.status) {
                mensaje = `${json.status} ${json.error}`;
            } else if (typeof json === 'object' && !Array.isArray(json)) {

                const errores = Object.values(json);

                mensaje = errores
                    .map(valor => {
                        if (Array.isArray(valor)) {
                            return valor.join(', ');
                        }
                        return String(valor);
                    })
                    .join('\n');
            }

        } catch {
            // La respuesta no es JSON.
            // Se conserva el mensaje original del servidor.
        }
    }

    throw new Error(mensaje);
}


// =====================================================================
// 7. ESCAPAR TEXTO ANTES DE INSERTARLO EN HTML
// =====================================================================

// Evita que texto proveniente de la base de datos
// pueda interpretarse como etiquetas HTML o scripts.

function escapar(texto) {

    const div = document.createElement('div');

    div.textContent = texto ?? '';

    return div.innerHTML;
}


// =====================================================================
// 8. FORMATO DE FECHAS
// =====================================================================

// Convierte una fecha yyyy-MM-dd a dd/MM/yyyy.
// Evita problemas de zona horaria al trabajar con fechas ISO.

function fechaFormato(fecha) {

    if (!fecha) {
        return '';
    }

    const partes = String(fecha).substring(0, 10).split('-');

    if (partes.length !== 3) {
        return fecha;
    }

    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}


// =====================================================================
// 9. FORMATO DE DINERO
// =====================================================================

function dinero(valor) {

    return Number(valor ?? 0).toLocaleString('es-NI', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}


// =====================================================================
// 10. RECARGAR PAGINA Y MOSTRAR NOTIFICACION
// =====================================================================

// Guarda temporalmente el mensaje en sessionStorage
// para mostrarlo despues de recargar la pagina.

function recargarConAviso(
    mensaje,
    tipo = 'success'
) {

    sessionStorage.setItem('aviso', mensaje);
    sessionStorage.setItem('avisoTipo', tipo);

    location.reload();
}


// =====================================================================
// 11. PROCESAR NOTIFICACIONES DESPUES DE CARGAR LA PAGINA
// =====================================================================

document.addEventListener('DOMContentLoaded', () => {

    const aviso = sessionStorage.getItem('aviso');
    const tipo = sessionStorage.getItem('avisoTipo') || 'success';

    if (aviso) {

        sessionStorage.removeItem('aviso');
        sessionStorage.removeItem('avisoTipo');

        notificar(aviso, tipo);
    }
});


// =====================================================================
// 12. CONFIRMACION DE FORMULARIOS CON data-confirmar
// =====================================================================

// Ejemplo:
//
// <form action="/empleados/eliminar/1" method="post">
//     <button type="submit"
//             data-confirmar="¿Desea eliminar este empleado?">
//         Eliminar
//     </button>
// </form>
//
// IMPORTANTE:
// Si el formulario utiliza campos ocultos o token CSRF,
// se conserva el envio original del formulario.

document.addEventListener('click', async (evento) => {

    const boton = evento.target.closest('[data-confirmar]');

    if (!boton) {
        return;
    }

    evento.preventDefault();

    // Evitar multiples confirmaciones.
    if (boton.dataset.procesando === 'true') {
        return;
    }

    boton.dataset.procesando = 'true';

    try {

        const acepta = await confirmar(
            boton.dataset.confirmar
        );

        if (acepta) {

            const formulario = boton.closest('form');

            if (formulario) {

                // Permite enviar el formulario sin volver
                // a ejecutar este evento click.
                formulario.dataset.confirmado = 'true';

                formulario.requestSubmit(boton);
            }
        }

    } finally {

        boton.dataset.procesando = 'false';
    }
});


// Evitar que el formulario confirmado vuelva a solicitar
// confirmacion cuando requestSubmit() lo envia.

document.addEventListener('submit', (evento) => {

    const formulario = evento.target;

    if (formulario.dataset.confirmado === 'true') {

        delete formulario.dataset.confirmado;

        return;
    }

    const boton = evento.submitter;

    if (boton && boton.hasAttribute('data-confirmar')) {
        evento.preventDefault();
    }
});


// =====================================================================
// 13. LIMPIAR FORMULARIOS
// =====================================================================

function limpiarFormulario(idFormulario) {

    const formulario = document.getElementById(idFormulario);

    if (!formulario) {
        return;
    }

    formulario.reset();

    // Limpiar errores de validacion visuales.
    formulario.querySelectorAll('.is-invalid').forEach(elemento => {
        elemento.classList.remove('is-invalid');
    });

    formulario.querySelectorAll('.invalid-feedback').forEach(elemento => {
        elemento.textContent = '';
    });

    // Limpiar Select2 si esta disponible.
    if (typeof $ !== 'undefined') {

        $(formulario).find('select.select2-hidden-accessible')
            .val(null)
            .trigger('change');
    }
}


// =====================================================================
// FIN DE app.js
// =====================================================================

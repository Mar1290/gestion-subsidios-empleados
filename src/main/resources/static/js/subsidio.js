
// =====================================================================
// subsidio.js
// Gestion de registros de subsidios.
// Sistema de Gestion de Subsidios y Empleados.
//
// Depende de:
// - app.js
// - jQuery
// - Bootstrap 5
// - DataTables
//
// Catalogos:
// - Empleados activos
// - Tipos de subsidio
// =====================================================================


// =====================================================================
// 1. CONFIGURACION
// =====================================================================

const SUBSIDIO_API = '/api/subsidios';
const EMPLEADO_API_SUBSIDIO = '/api/empleados';
const TIPO_SUBSIDIO_API = '/api/tipos-subsidio';

let tablaSubsidios = null;
let modalSubsidio = null;


// =====================================================================
// 2. INICIALIZACION
// =====================================================================

document.addEventListener('DOMContentLoaded', async () => {

    const tabla = document.getElementById('tablaSubsidios');

    // Inicializar solamente cuando existe la tabla en la pagina.
    if (!tabla) {
        return;
    }

    const modal = document.getElementById('modalSubsidio');

    if (modal) {
        modalSubsidio = bootstrap.Modal.getOrCreateInstance(modal);
    }

    inicializarTablaSubsidios();
    registrarEventosSubsidio();

    // Cargar catalogos antes de permitir registrar subsidios.
    await cargarCatalogosSubsidio();

    await cargarSubsidios();
});


// =====================================================================
// 3. INICIALIZAR DATATABLE
// =====================================================================

function inicializarTablaSubsidios() {

    if (tablaSubsidios) {
        tablaSubsidios.destroy();
    }

    tablaSubsidios = new DataTable('#tablaSubsidios', {
        data: [],
        columns: [
            {
                data: 'idSubsidio',
                title: 'ID'
            },
            {
                data: 'numeroOrden',
                title: 'Numero de orden'
            },
            {
                data: 'nombreEmpleado',
                title: 'Empleado',
                render: empleado => escapar(
                    empleado?.nombreEmpleado ?? ''
                )
            },
            {
                data: 'tipoSubsidio',
                title: 'Tipo de subsidio',
                render: tipo => escapar(
                    tipo?.tipoSubsidio ?? ''
                )
            },
            {
                data: 'fechaDesde',
                title: 'Desde',
                render: fecha => escapar(fechaFormato(fecha))
            },
            {
                data: 'fechaHasta',
                title: 'Hasta',
                render: fecha => escapar(fechaFormato(fecha))
            },
            {
                data: 'motivo',
                title: 'Motivo',
                render: motivo => escapar(motivo)
            },
            {
                data: null,
                title: 'Acciones',
                orderable: false,
                searchable: false,
                render: fila => `
                    <div class="btn-group btn-group-sm">
                        <button type="button"
                                class="btn btn-outline-primary"
                                data-accion="editar"
                                data-id="${Number(fila.idSubsidio)}"
                                title="Editar subsidio">
                            <i class="bi bi-pencil-square"></i>
                        </button>

                        <button type="button"
                                class="btn btn-outline-danger"
                                data-accion="eliminar"
                                data-id="${Number(fila.idSubsidio)}"
                                title="Eliminar subsidio">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                `
            }
        ],
        language: DT_ES,
        pageLength: 10,
        lengthMenu: [5, 10, 25, 50],
        responsive: true,
        order: [[0, 'desc']]
    });
}


// =====================================================================
// 4. CARGAR CATALOGOS
// =====================================================================

// Los empleados se cargan desde /activos.
// Los tipos se cargan desde /api/tipos-subsidio/listar.

async function cargarCatalogosSubsidio() {

    try {

        const [empleados, tipos] = await Promise.all([
            api(`${EMPLEADO_API_SUBSIDIO}/activos`),
            api(`${TIPO_SUBSIDIO_API}/listar`)
        ]);

        cargarSelectEmpleados(empleados);
        cargarSelectTiposSubsidio(tipos);

    } catch (error) {

        mostrarError(
            'Error al cargar los catalogos',
            error
        );
    }
}


// =====================================================================
// 5. CARGAR SELECTOR DE EMPLEADOS
// =====================================================================

function cargarSelectEmpleados(empleados) {

    const select = document.getElementById('idEmpleado');

    if (!select) {
        return;
    }

    select.innerHTML = '<option value="">Seleccione un empleado</option>';

    empleados.forEach(empleado => {

        const opcion = document.createElement('option');

        opcion.value = empleado.idEmpleado;
        opcion.textContent = empleado.nombreEmpleado;

        select.appendChild(opcion);
    });

    actualizarSelect2(select);
}


// =====================================================================
// 6. CARGAR SELECTOR DE TIPOS DE SUBSIDIO
// =====================================================================

function cargarSelectTiposSubsidio(tipos) {

    const select = document.getElementById('idTipoSubsidio');

    if (!select) {
        return;
    }

    select.innerHTML = '<option value="">Seleccione un tipo de subsidio</option>';

    tipos.forEach(tipo => {

        const opcion = document.createElement('option');

        opcion.value = tipo.idTipoSubsidio;
        opcion.textContent = tipo.tipoSubsidio;

        select.appendChild(opcion);
    });

    actualizarSelect2(select);
}


// =====================================================================
// 7. ACTUALIZAR SELECT2 SI ESTA INSTALADO
// =====================================================================

function actualizarSelect2(select) {

    if (
        typeof $ !== 'undefined' &&
        $.fn.select2 &&
        $(select).hasClass('select2-hidden-accessible')
    ) {
        $(select).trigger('change.select2');
    }
}


// =====================================================================
// 8. CARGAR SUBSIDIOS
// =====================================================================

async function cargarSubsidios() {

    try {

        const subsidios = await api(`${SUBSIDIO_API}/listar`);

        tablaSubsidios.clear();
        tablaSubsidios.rows.add(subsidios);
        tablaSubsidios.draw();

    } catch (error) {

        mostrarError('Error al cargar subsidios', error);
    }
}


// =====================================================================
// 9. REGISTRAR EVENTOS
// =====================================================================

function registrarEventosSubsidio() {

    // Envio del formulario.
    const formulario = document.getElementById('formSubsidio');

    if (formulario) {

        formulario.addEventListener('submit', async evento => {

            evento.preventDefault();

            await guardarSubsidio();
        });
    }

    // Boton para nuevo subsidio.
    const btnNuevo = document.getElementById('btnNuevoSubsidio');

    if (btnNuevo) {

        btnNuevo.addEventListener('click', () => {

            prepararNuevoSubsidio();
        });
    }

    // Acciones de la tabla.
    document.getElementById('tablaSubsidios')
        .addEventListener('click', async evento => {

            const boton = evento.target.closest('[data-accion]');

            if (!boton) {
                return;
            }

            const id = Number(boton.dataset.id);
            const accion = boton.dataset.accion;

            if (!id) {
                return;
            }

            if (accion === 'editar') {
                await editarSubsidio(id);
            }

            if (accion === 'eliminar') {
                await eliminarSubsidio(id);
            }
        });

    // Validar fechas al modificarlas.
    const fechaDesde = document.getElementById('fechaDesde');
    const fechaHasta = document.getElementById('fechaHasta');

    fechaDesde?.addEventListener('change', validarFechasSubsidio);
    fechaHasta?.addEventListener('change', validarFechasSubsidio);
}

// =====================================================================
// 10. PREPARAR FORMULARIO PARA NUEVO SUBSIDIO
// =====================================================================
function prepararNuevoSubsidio() {

    limpiarFormulario('formSubsidio');

    document.getElementById('idSubsidio').value = '';

    document.getElementById('tituloModalSubsidio').textContent =
        'Registrar subsidio';

    // Los catalogos deben estar cargados.
    modalSubsidio?.show();
}

// =====================================================================
// 11. VALIDAR FECHAS
// =====================================================================

function validarFechasSubsidio() {

    const fechaDesde = document.getElementById('fechaDesde');
    const fechaHasta = document.getElementById('fechaHasta');

    if (!fechaDesde || !fechaHasta) {
        return true;
    }

    fechaHasta.setCustomValidity('');

    if (
        fechaDesde.value &&
        fechaHasta.value &&
        fechaHasta.value < fechaDesde.value
    ) {

        fechaHasta.setCustomValidity(
            'La fecha hasta no puede ser anterior a la fecha desde.'
        );

        fechaHasta.reportValidity();

        return false;
    }

    return true;
}

// =====================================================================
// 12. GUARDAR SUBSIDIO: CREAR O ACTUALIZAR
// =====================================================================

async function guardarSubsidio() {

    const formulario = document.getElementById('formSubsidio');

    if (!validarFechasSubsidio()) {
        return;
    }

    if (!formulario.reportValidity()) {
        return;
    }

    const id = document.getElementById('idSubsidio').value;

    const empleadoId = document.getElementById('idEmpleado').value;
    const tipoId = document.getElementById('idTipoSubsidio').value;

    // Validacion explicita de relaciones obligatorias.
    if (!empleadoId) {

        mostrarError(
            'Empleado obligatorio',
            'Debe seleccionar un empleado para registrar el subsidio.'
        );

        return;
    }

    if (!tipoId) {

        mostrarError(
            'Tipo de subsidio obligatorio',
            'Debe seleccionar un tipo de subsidio.'
        );

        return;
    }

    const subsidio = {
        tipoSubsidio: {
            idTipoSubsidio: Number(tipoId)
        },
        nombreEmpleado: {
            idEmpleado: Number(empleadoId)
        },
        numeroOrden: Number(
            document.getElementById('numeroOrden').value
        ),
        fechaDesde: document.getElementById('fechaDesde').value,
        fechaHasta: document.getElementById('fechaHasta').value,
        motivo: document.getElementById('motivo').value.trim()
    };

    try {

        if (id) {

            await api(
                `${SUBSIDIO_API}/actualizar/${id}`,
                'PUT',
                subsidio
            );

            notificar('Subsidio actualizado correctamente.');

        } else {

            await api(
                `${SUBSIDIO_API}/crear`,
                'POST',
                subsidio
            );

            notificar('Subsidio registrado correctamente.');
        }

        modalSubsidio?.hide();

        limpiarFormulario('formSubsidio');

        await cargarSubsidios();

    } catch (error) {

        mostrarError(
            'No fue posible guardar el subsidio',
            error
        );
    }
}


// =====================================================================
// 13. CONSULTAR SUBSIDIO PARA EDITAR
// =====================================================================

async function editarSubsidio(id) {

    try {

        const subsidio = await api(
            `${SUBSIDIO_API}/obtener/${id}`
        );

        document.getElementById('idSubsidio').value =
            subsidio.idSubsidio;

        document.getElementById('idEmpleado').value =
            subsidio.nombreEmpleado?.idEmpleado ?? '';

        document.getElementById('idTipoSubsidio').value =
            subsidio.tipoSubsidio?.idTipoSubsidio ?? '';

        document.getElementById('numeroOrden').value =
            subsidio.numeroOrden ?? '';

        document.getElementById('fechaDesde').value =
            subsidio.fechaDesde ?? '';

        document.getElementById('fechaHasta').value =
            subsidio.fechaHasta ?? '';

        document.getElementById('motivo').value =
            subsidio.motivo ?? '';

        // Actualizar visualmente los selectores Select2.
        actualizarSelect2(document.getElementById('idEmpleado'));
        actualizarSelect2(document.getElementById('idTipoSubsidio'));

        document.getElementById('tituloModalSubsidio').textContent =
            'Actualizar subsidio';

        modalSubsidio?.show();

    } catch (error) {

        mostrarError('Error al consultar subsidio', error);
    }
}

// =====================================================================
// 14. ELIMINAR SUBSIDIO
// =====================================================================
async function eliminarSubsidio(id) {

    const acepta = await confirmar(
        '¿Eliminar subsidio?',
        'Confirme si desea eliminar este registro de subsidio.'
    );

    if (!acepta) {
        return;
    }

    try {

        await api(
            `${SUBSIDIO_API}/eliminar/${id}`,
            'DELETE'
        );

        notificar('Subsidio eliminado correctamente.');

        await cargarSubsidios();

    } catch (error) {

        mostrarError(
            'No fue posible eliminar el subsidio',
            error
        );
    }
}
// =====================================================================
// FIN DE subsidio.js
// =====================================================================
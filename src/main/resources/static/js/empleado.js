
// =====================================================================
// empleado.js
// Gestion del catalogo de empleados.
// Sistema de Gestion de Subsidios y Empleados.
//
// Depende de:
// - app.js
// - jQuery
// - Bootstrap 5
// - DataTables
// =====================================================================

// =====================================================================
// 1. CONFIGURACION
// =====================================================================
const EMPLEADO_API = '/api/empleados';

let tablaEmpleados = null;
let modalEmpleado = null;

// =====================================================================
// 2. INICIALIZACION
// =====================================================================
document.addEventListener('DOMContentLoaded', () => {

    const tabla = document.getElementById('tablaEmpleados');

    // Inicializar solamente cuando existe la tabla en la pagina.
    if (!tabla) {
        return;
    }

    const modal = document.getElementById('modalEmpleado');

    if (modal) {
        modalEmpleado = bootstrap.Modal.getOrCreateInstance(modal);
    }

    inicializarTablaEmpleados();
    registrarEventosEmpleado();
    cargarEmpleados();
});

// =====================================================================
// 3. INICIALIZAR DATATABLE
// =====================================================================
function inicializarTablaEmpleados() {

    if (tablaEmpleados) {
        tablaEmpleados.destroy();
    }

    tablaEmpleados = new DataTable('#tablaEmpleados', {
        data: [],
        columns: [
            {
                data: 'idEmpleado',
                title: 'ID'
            },
            {
                data: 'nombreEmpleado',
                title: 'Nombre del empleado',
                render: dato => escapar(dato)
            },
            {
                data: 'dni',
                title: 'DNI',
                render: dato => escapar(dato)
            },
            {
                data: 'cargo',
                title: 'Cargo',
                render: dato => escapar(dato)
            },
            {
                data: 'telefono',
                title: 'Telefono',
                render: dato => escapar(dato)
            },
            {
                data: 'activo',
                title: 'Estado',
                render: dato => dato
                    ? '<span class="badge bg-success">Activo</span>'
                    : '<span class="badge bg-secondary">Inactivo</span>'
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
                                data-id="${Number(fila.idEmpleado)}"
                                title="Editar empleado">
                            <i class="bi bi-pencil-square"></i>
                        </button>

                        <button type="button"
                                class="btn btn-outline-danger"
                                data-accion="eliminar"
                                data-id="${Number(fila.idEmpleado)}"
                                title="Eliminar empleado">
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
        order: [[1, 'asc']]
    });
}
// =====================================================================
// 4. CARGAR EMPLEADOS
// =====================================================================
async function cargarEmpleados() {

    try {

        const empleados = await api(`${EMPLEADO_API}/listar`);

        tablaEmpleados.clear();
        tablaEmpleados.rows.add(empleados);
        tablaEmpleados.draw();

    } catch (error) {

        mostrarError('Error al cargar empleados', error);
    }
}
// =====================================================================
// 5. REGISTRAR EVENTOS
// =====================================================================
function registrarEventosEmpleado() {

    // Envio del formulario.
    const formulario = document.getElementById('formEmpleado');

    if (formulario) {

        formulario.addEventListener('submit', async evento => {

            evento.preventDefault();

            await guardarEmpleado();
        });
    }

    // Boton para nuevo empleado.
    const btnNuevo = document.getElementById('btnNuevoEmpleado');

    if (btnNuevo) {

        btnNuevo.addEventListener('click', () => {

            prepararNuevoEmpleado();
        });
    }

    // Acciones de la tabla.
    document.getElementById('tablaEmpleados')
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
                await editarEmpleado(id);
            }

            if (accion === 'eliminar') {
                await eliminarEmpleado(id);
            }
        });
}
// =====================================================================
// 6. PREPARAR FORMULARIO PARA NUEVO EMPLEADO
// =====================================================================
function prepararNuevoEmpleado() {

    limpiarFormulario('formEmpleado');

    document.getElementById('idEmpleado').value = '';

    document.getElementById('tituloModalEmpleado').textContent =
        'Registrar empleado';

    document.getElementById('activo').checked = true;

    modalEmpleado?.show();
}
// =====================================================================
// 7. GUARDAR EMPLEADO: CREAR O ACTUALIZAR
// =====================================================================
async function guardarEmpleado() {

    const formulario = document.getElementById('formEmpleado');

    if (!formulario.reportValidity()) {
        return;
    }

    const id = document.getElementById('idEmpleado').value;

    const empleado = {
        nombreEmpleado: document.getElementById('nombreEmpleado').value.trim(),
        dni: document.getElementById('dni').value.trim(),
        cargo: document.getElementById('cargo').value.trim(),
        telefono: document.getElementById('telefono').value.trim(),
        activo: document.getElementById('activo').checked
    };

    try {

        if (id) {

            await api(
                `${EMPLEADO_API}/actualizar/${id}`,
                'PUT',
                empleado
            );

            notificar('Empleado actualizado correctamente.');

        } else {

            await api(
                `${EMPLEADO_API}/crear`,
                'POST',
                empleado
            );

            notificar('Empleado registrado correctamente.');
        }

        modalEmpleado?.hide();

        limpiarFormulario('formEmpleado');

        await cargarEmpleados();

    } catch (error) {

        mostrarError('No fue posible guardar el empleado', error);
    }
}

// =====================================================================
// 8. CONSULTAR EMPLEADO PARA EDITAR
// =====================================================================
async function editarEmpleado(id) {

    try {

        const empleado = await api(
            `${EMPLEADO_API}/obtener/${id}`
        );

        document.getElementById('idEmpleado').value =
            empleado.idEmpleado;

        document.getElementById('nombreEmpleado').value =
            empleado.nombreEmpleado ?? '';

        document.getElementById('dni').value =
            empleado.dni ?? '';

        document.getElementById('cargo').value =
            empleado.cargo ?? '';

        document.getElementById('telefono').value =
            empleado.telefono ?? '';

        document.getElementById('activo').checked =
            Boolean(empleado.activo);

        document.getElementById('tituloModalEmpleado').textContent =
            'Actualizar empleado';

        modalEmpleado?.show();

    } catch (error) {

        mostrarError('Error al consultar empleado', error);
    }
}
// =====================================================================
// 9. ELIMINAR EMPLEADO
// =====================================================================
async function eliminarEmpleado(id) {

    const acepta = await confirmar(
        '¿Eliminar empleado?',
        'Se verificara que el empleado no tenga subsidios asociados.'
    );

    if (!acepta) {
        return;
    }

    try {

        await api(
            `${EMPLEADO_API}/eliminar/${id}`,
            'DELETE'
        );

        notificar('Empleado eliminado correctamente.');

        await cargarEmpleados();

    } catch (error) {

        mostrarError(
            'No fue posible eliminar el empleado',
            error
        );
    }
}
// =====================================================================
// FIN DE empleado.js
// =====================================================================
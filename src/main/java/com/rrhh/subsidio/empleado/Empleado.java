
package com.rrhh.subsidio.empleado;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rrhh.subsidio.subsidio.Subsidio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "empleados")
@Data
@NoArgsConstructor
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEmpleado;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    @Column(nullable = false, length = 200)
    private String nombreEmpleado;



    @NotBlank(message = "El DNI es obligatorio")
    @Size(max = 14, message = "El DNI no puede superar 14 caracteres")
    @Column(nullable = false, length = 14)
    private String dni;

    @Size(max = 200, message = "El cargo no puede superar 200 caracteres")
    @Column(length = 200)
    private String cargo;

    @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
    @Column(length = 20)
    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;

    // ===================== RELACION: un empleado -> muchos subsidios =====================

    @OneToMany(mappedBy = "nombreEmpleado")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Subsidio> subsidios = new ArrayList<>();

    // Constructor
    public Empleado(Integer idEmpleado, String nombreEmpleado,
                    String dni, String cargo, String telefono, Boolean activo) {

        this.idEmpleado = idEmpleado;
        this.nombreEmpleado = nombreEmpleado;       
        this.dni = dni;
        this.cargo = cargo;
        this.telefono = telefono;
        this.activo = activo;
    }
}


package com.rrhh.subsidio.subsidio;

// Entidades relacionadas
import java.time.LocalDate;

import com.rrhh.subsidio.empleado.Empleado;
import com.rrhh.subsidio.tiposubsidio.TipoSubsidio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "subsidios")
@Data
@NoArgsConstructor
public class Subsidio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSubsidio;

    @NotNull(message = "El tipo de subsidio es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdTiposubsidio", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TipoSubsidio tipoSubsidio;

    @NotNull(message = "El empleado es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdEmpleado", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Empleado nombreEmpleado;

    @NotNull(message = "El número de orden es obligatorio")
    @Column(name = "Numero orden", nullable = false)
    private Integer numeroOrden;

    @NotNull(message = "La fecha desde es obligatoria")
    @Column(name = "FechaDesde", nullable = false)
    private LocalDate fechaDesde;

    @NotNull(message = "La fecha hasta es obligatoria")
    @Column(name = "FechaHasta", nullable = false)
    private LocalDate fechaHasta;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 2000, message = "El motivo no puede superar 2000 caracteres")
    @Column(nullable = false, length = 2000)
    private String motivo;

    // Constructor
    public Subsidio(Integer idSubsidio, TipoSubsidio tipoSubsidio,
                    Empleado nombreEmpleado, Integer numeroOrden,
                    LocalDate fechaDesde, LocalDate fechaHasta,
                    String motivo) {

        this.idSubsidio = idSubsidio;
        this.tipoSubsidio = tipoSubsidio;
        this.nombreEmpleado = nombreEmpleado;
        this.numeroOrden = numeroOrden;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.motivo = motivo;
    }
}

package com.rrhh.subsidio.tiposubsidio;



// Entidad relacionada
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
@Table(name = "tipos_subsidio")
@Data
@NoArgsConstructor
public class TipoSubsidio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipoSubsidio;

    @NotBlank(message = "El tipo de subsidio es obligatorio")
    @Size(max = 100, message = "El tipo de subsidio no puede superar 100 caracteres")
    @Column(nullable = false, length = 100)
    private String tipoSubsidio;

    // ===================== RELACION: un tipo de subsidio -> muchos subsidios =====================

    @OneToMany(mappedBy = "tipoSubsidio")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Subsidio> subsidios = new ArrayList<>();

    // Constructor
    public TipoSubsidio(Integer idTipoSubsidio, String tipoSubsidio) {
        this.idTipoSubsidio = idTipoSubsidio;
        this.tipoSubsidio = tipoSubsidio;
    }
}

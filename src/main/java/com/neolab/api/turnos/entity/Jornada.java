package com.neolab.api.turnos.entity;

import com.neolab.api.turnos.enums.JornadaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "jornadas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Jornada {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    protected LocalDateTime entrada;
    protected LocalDateTime salida;
    private Enum<JornadaEnum> tipo;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "empleado_id", insertable = false, updatable = false)
    protected Empleado empleado;
    @Column(name="empleado_id", nullable = false)
    private Long empleadoId;

    

}

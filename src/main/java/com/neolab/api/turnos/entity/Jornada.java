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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jornadas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Jornada {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "jornada_id")
    private Long id;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private Enum<JornadaEnum> tipo;
//
//    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
//    @JoinColumn(name = "empleado_id", insertable = false, updatable = false)
//    @Column(name="empleado_id", nullable = false)
//    private Long empleadoId;
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "jornadas_empleados",
            joinColumns = { @JoinColumn(name = "jornada_id") },
            inverseJoinColumns = { @JoinColumn(name = "empleado_id") })

    private List<Empleado> empleados = new ArrayList<>();


    public void addEmpleado(Empleado empleado) {
        this.empleados.add(empleado);
    }

    public void removeEmpleado(long id) {
        Empleado empleado= this.empleados.stream().filter(item -> item.getId() == id).findFirst().orElse(null);
        if (empleado != null) {
            this.empleados.remove(empleado);
//            empleado.getJornadas().remove(this);
        }
    }
}

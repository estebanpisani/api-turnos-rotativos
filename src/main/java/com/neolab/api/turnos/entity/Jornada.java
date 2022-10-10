package com.neolab.api.turnos.entity;

import com.neolab.api.turnos.enums.JornadaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    protected LocalDateTime entrada;
    protected LocalDateTime salida;
//    private Enum<JornadaEnum> tipo;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tipo_id", referencedColumnName = "tipo_id")
    private Tipo tipo;
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "jornadas_empleados",
            joinColumns = { @JoinColumn(name = "jornada_id") },
            inverseJoinColumns = { @JoinColumn(name = "empleado_id") })

    private Set<Empleado> empleados = new HashSet<>();

    public void addEmpleado(Empleado empleado) {
        this.empleados.add(empleado);
    }

    public void removeEmpleado(long id) {
        Empleado empleado = this.empleados.stream().filter(item -> item.getId() == id).findFirst().orElse(null);
        if (empleado != null) {
            this.empleados.remove(empleado);
        }
    }


}

package com.neolab.api.turnos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "empleados")
@Getter
@Setter
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "empleado_id")
    private Long id;
    private String nombre;
    private String apellido;
    private LocalDate fechaDeNacimiento;
    private String email;
    private LocalDate fechaAlta;
    private LocalDate fechaBaja;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "empleados")
    Set<Jornada> jornadas = new HashSet<>();

    public Empleado() {
    }

}

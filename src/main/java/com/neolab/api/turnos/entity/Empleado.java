package com.neolab.api.turnos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

//    @OneToMany(mappedBy = "empleado")
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "empleados")
    List<Jornada> jornadas = new ArrayList<>();

    public Empleado() {
    }

    public Empleado(Long id, String nombre, String apellido, LocalDate fechaDeNacimiento, String email, LocalDate fechaAlta, LocalDate fechaBaja) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaDeNacimiento = fechaDeNacimiento;
        this.email = email;
        this.fechaAlta = fechaAlta;
        this.fechaBaja = fechaBaja;
    }

}

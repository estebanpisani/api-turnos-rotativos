package com.neolab.api.turnos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @NotBlank
    private String nombre;
    @NotNull
    @NotBlank
    private String apellido;
    @NotNull
    @NotBlank
    private LocalDate fechaDeNacimiento;
    @NotNull
    @NotBlank
    private String email;
    @NotNull
    @NotBlank
    private String password;
    private LocalDate fechaAlta;
    private LocalDate fechaBaja;

    public Empleado() {
    }

    public Empleado(Long id, String nombre, String apellido, LocalDate fechaDeNacimiento, String email, String password, LocalDate fechaAlta, LocalDate fechaBaja) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaDeNacimiento = fechaDeNacimiento;
        this.email = email;
        this.password = password;
        this.fechaAlta = fechaAlta;
        this.fechaBaja = fechaBaja;
    }

}

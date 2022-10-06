package com.neolab.api.turnos.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class EmpleadoDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String fechaDeNacimiento;
    private String email;
    private String fechaAlta;
    private String fechaBaja;
}

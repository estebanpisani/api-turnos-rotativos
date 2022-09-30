package com.neolab.api.turnos.dto;

import lombok.Data;

@Data
public class EmpleadoDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String fechaDeNacimiento;
    private String email;
    private String password;
    private String telefono;
    private String fechaAlta;
    private String fechaBaja;
}

package com.neolab.api.turnos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class JornadaDTO {
    private Long id;
    private String entrada;
    private String salida;
    private String tipo;
    private Set<Long> empleadosId = new HashSet<>();
    private Set<EmpleadoDTO> empleados = new HashSet<>();
    public JornadaDTO() {
    }

}

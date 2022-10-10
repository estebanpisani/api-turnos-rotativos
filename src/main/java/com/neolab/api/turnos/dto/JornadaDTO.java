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

    public JornadaDTO() {
    }

    public JornadaDTO(Long id, String entrada, String salida, String tipo, Set<Long> empleadosId) {
        this.id = id;
        this.entrada = entrada;
        this.salida = salida;
        this.tipo = tipo;
        this.empleadosId = empleadosId;
    }
}

package com.neolab.api.turnos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class JornadaDTO {
    private Long id;
    private String entrada;
    private String salida;
    private String tipo;
    private List<Long> empleadosId = new ArrayList<>();

    public JornadaDTO() {
    }

    public JornadaDTO(Long id, String entrada, String salida, String tipo, List<Long> empleadosId) {
        this.id = id;
        this.entrada = entrada;
        this.salida = salida;
        this.tipo = tipo;
        this.empleadosId = empleadosId;
    }
}

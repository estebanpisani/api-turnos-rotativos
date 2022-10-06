package com.neolab.api.turnos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JornadaDTO {
    private Long id;
//    private String fecha;
    private String entrada;
    private String salida;
    private String tipo;
    private Long empleadoId;

    public JornadaDTO() {
    }

    public JornadaDTO(Long id, String entrada, String salida, String tipo, Long empleadoId) {
        this.id = id;
//        this.fecha = fecha;
        this.entrada = entrada;
        this.salida = salida;
        this.tipo = tipo;
        this.empleadoId = empleadoId;
    }
}

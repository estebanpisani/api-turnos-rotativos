package com.neolab.api.turnos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JornadaDTO {
    private Long id;
    private String fecha;
    private String horaEntrada;
    private String horaSalida;
    private String tipo;
    private Long empleadoId;

    public JornadaDTO() {
    }

    public JornadaDTO(Long id, String fecha, String horaEntrada, String horaSalida, String tipo, Long empleadoId) {
        this.id = id;
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.tipo = tipo;
        this.empleadoId = empleadoId;
    }
}

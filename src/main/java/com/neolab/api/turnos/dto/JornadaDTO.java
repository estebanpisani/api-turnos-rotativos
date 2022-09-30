package com.neolab.api.turnos.dto;

import com.neolab.api.turnos.enums.JornadaEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JornadaDTO {
    private String fecha;
    private String horaEntrada;
    private String horaSalida;
    private String tipo;
    private Long empleadoId;

    public JornadaDTO() {
    }

    public JornadaDTO(String horaEntrada, String horaSalida, String tipo) {
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.tipo = tipo;
    }
}

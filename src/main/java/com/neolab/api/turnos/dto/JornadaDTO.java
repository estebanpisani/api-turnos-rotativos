package com.neolab.api.turnos.dto;

import com.neolab.api.turnos.enums.JornadaEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
public class JornadaDTO {
    private LocalDate fecha;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private Enum<JornadaEnum> tipo;

    public JornadaDTO() {
    }

    public JornadaDTO(LocalDateTime horaEntrada, LocalDateTime horaSalida, Enum tipo) {
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.tipo = tipo;
    }
}

package com.neolab.api.turnos.dto;

import com.neolab.api.turnos.enums.JornadaEnum;

import java.time.LocalDateTime;

public class JornadaDTO {
    private LocalDateTime dateTimeIn;
    private LocalDateTime dateTimeOut;
    private Enum<JornadaEnum> type;

    public JornadaDTO() {
    }

    public JornadaDTO(LocalDateTime dateTimeIn, LocalDateTime dateTimeOut, Enum type) {
        this.dateTimeIn = dateTimeIn;
        this.dateTimeOut = dateTimeOut;
        this.type = type;
    }
}

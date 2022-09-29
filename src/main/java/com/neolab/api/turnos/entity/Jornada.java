package com.neolab.api.turnos.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public abstract class Jornada {
    protected LocalDateTime timeIn;
    protected LocalDateTime timeOut;
}

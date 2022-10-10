package com.neolab.api.turnos.entity;

public class Normal extends Tipo {
    public Normal(){
        super.nombre = "normal";
        super.horasDiariasMin = 6;
        super.horasDiariasMax = 8;
        super.horasSemanalesMax = 48;
    }
}

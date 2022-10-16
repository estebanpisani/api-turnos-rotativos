package com.neolab.api.turnos.entity;

public class DiaLibre extends Tipo {
    public DiaLibre(){
        super.nombre = "dia libre";
        super.horasDiariasMin = 24;
        super.horasDiariasMax = 24;
        super.horasSemanalesMax = 48;
    }
}

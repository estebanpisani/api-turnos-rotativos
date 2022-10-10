package com.neolab.api.turnos.entity;

public class Extra extends Tipo {
    public Extra(){
        super.nombre = "extra";
        super.horasDiariasMin = 2;
        super.horasDiariasMax = 6;
        super.horasSemanalesMax = 48;
    }
}

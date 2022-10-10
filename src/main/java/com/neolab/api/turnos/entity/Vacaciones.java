package com.neolab.api.turnos.entity;

public class Vacaciones extends Tipo{
    public Vacaciones(){
        super.nombre = "vacaciones";
        super.horasDiariasMin = 24;
        super.horasDiariasMax = 24;
        super.horasSemanalesMax = 168;
    }
}

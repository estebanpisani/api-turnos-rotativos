package com.neolab.api.turnos.service.impl;

import com.neolab.api.turnos.entity.Jornada;
import com.neolab.api.turnos.entity.JornadaNormal;
import com.neolab.api.turnos.service.JornadaService;

import java.util.List;

public class JornadaNormalServiceImpl implements JornadaService {

    @Override
    public Jornada createJornada(Jornada jornada) {
        JornadaNormal jornadaNormal = new JornadaNormal();
        jornadaNormal.setTimeIn(jornada.getTimeIn());
        jornadaNormal.setTimeOut(jornada.getTimeOut());
        return null;
    }

    @Override
    public Jornada updateJornada(Long id, Jornada empleado) {
        return null;
    }

    @Override
    public List<Jornada> getJornadas() {
        return null;
    }

    @Override
    public Jornada getJornadaById(Long id) {
        return null;
    }

    @Override
    public void deleteJornada(Long id) {

    }
}

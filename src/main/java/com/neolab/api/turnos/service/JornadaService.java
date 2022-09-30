package com.neolab.api.turnos.service;
import com.neolab.api.turnos.entity.Jornada;

import java.util.List;

public interface JornadaService {
    Jornada createJornada(Jornada jornada);
    Jornada updateJornada(Long id, Jornada jornada);
    List<Jornada> getJornadasByEmpleado(Long id);
    Jornada getJornadaById(Long id);
    void deleteJornada(Long id);
}

package com.neolab.api.turnos.service;

import com.neolab.api.turnos.dto.JornadaDTO;
import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.entity.Jornada;

import java.util.List;

public interface JornadaService {
    JornadaDTO createJornada(JornadaDTO dto);
    JornadaDTO updateJornada(Long id, JornadaDTO dto);
    List<JornadaDTO> getJornadas();
    JornadaDTO getJornadaById(Long id);
    void deleteJornada(Long id);
}

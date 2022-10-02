package com.neolab.api.turnos.service;
import com.neolab.api.turnos.dto.JornadaDTO;

import java.util.List;

public interface JornadaService {
    JornadaDTO createJornada(JornadaDTO dto);
    JornadaDTO updateJornada(Long id, JornadaDTO dto);
    List<JornadaDTO> getAllJornadas();
    List<JornadaDTO> getJornadasByEmpleado(Long id);
    JornadaDTO getJornadaById(Long id);
    void deleteJornada(Long id);
}

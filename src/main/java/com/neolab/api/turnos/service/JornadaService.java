package com.neolab.api.turnos.service;
import com.neolab.api.turnos.dto.JornadaDTO;

import java.util.List;

public interface JornadaService {
    JornadaDTO createJornada(JornadaDTO dto) throws Exception;
    JornadaDTO updateJornada(Long id, JornadaDTO dto) throws Exception;
    List<JornadaDTO> getAllJornadas(String tipo);
    List<JornadaDTO> getJornadasByEmpleado(Long id, String tipo);
    JornadaDTO getJornadaById(Long id);
    Boolean deleteJornada(Long id);
}

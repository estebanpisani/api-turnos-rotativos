package com.neolab.api.turnos.service;

import com.neolab.api.turnos.dto.EmpleadoDTO;

import java.util.List;

public interface EmpleadoService {

    EmpleadoDTO createEmpleado(EmpleadoDTO dto);
    EmpleadoDTO updateEmpleado(Long id, EmpleadoDTO dto);
    List<EmpleadoDTO> getEmpleados();
    EmpleadoDTO getEmpleadoById(Long id);
    void deleteEmpleado(Long id);
}

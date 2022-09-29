package com.neolab.api.turnos.service;

import com.neolab.api.turnos.entity.Empleado;

import java.util.List;

public interface EmpleadoService {

    Empleado createEmpleado(Empleado empleado);
    Empleado updateEmpleado(Long id, Empleado empleado);
    List<Empleado> getEmpleados();
    Empleado getEmpleadoById(Long id);
    void deleteEmpleado(Long id);
}

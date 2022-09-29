package com.neolab.api.turnos.service.impl;

import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.repository.EmpleadoRepository;
import com.neolab.api.turnos.service.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EmpleadoServiceImpl implements EmpleadoService {
    @Autowired
    EmpleadoRepository empleadoRepository;

    @Override
    public Empleado createEmpleado(Empleado empleado) {
        return null;
    }

    @Override
    public Empleado updateEmpleado(Long id, Empleado empleado) {
        return null;
    }

    @Override
    public List<Empleado> getEmpleados() {
        return null;
    }

    @Override
    public Empleado getEmpleadoById(Long id) {
        return null;
    }

    @Override
    public void deleteEmpleado(Long id) {

    }
}

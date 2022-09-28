package com.neolab.api.turnos.service.impl;

import com.neolab.api.turnos.dto.EmpleadoDTO;
import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.mappers.EmpleadoMapper;
import com.neolab.api.turnos.repository.EmpleadoRepository;
import com.neolab.api.turnos.service.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {
    @Autowired
    EmpleadoRepository empleadoRepository;
    @Autowired
    EmpleadoMapper empleadoMapper;

    @Override
    public EmpleadoDTO createEmpleado(EmpleadoDTO dto) {
        Empleado newEmpleado = empleadoRepository.save(empleadoMapper.dtoToEntity(dto));
        EmpleadoDTO response = empleadoMapper.entityToDTO(newEmpleado);
        return response;
    }

    @Override
    public EmpleadoDTO updateEmpleado(Long id, EmpleadoDTO empleado) {
        return null;
    }

    @Override
    public List<EmpleadoDTO> getEmpleados() {
        List<Empleado> empleados = empleadoRepository.findAll();
        List<EmpleadoDTO> dtos = empleadoMapper.entityListToDTOList(empleados);
        return dtos;
    }

    @Override
    public EmpleadoDTO getEmpleadoById(Long id) {
        Optional<Empleado> opt = empleadoRepository.findById(id);
        if(opt.isPresent()){
            EmpleadoDTO response = empleadoMapper.entityToDTO(opt.get());
            return response;
        }
        return null;
    }

    @Override
    public void deleteEmpleado(Long id) {

    }
}

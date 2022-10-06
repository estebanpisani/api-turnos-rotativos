package com.neolab.api.turnos.service.impl;

import com.neolab.api.turnos.dto.EmpleadoDTO;
import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.mappers.EmpleadoMapper;
import com.neolab.api.turnos.repository.EmpleadoRepository;
import com.neolab.api.turnos.service.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
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
        //Crea una entidad Empleado con lo datos del DTO y se guarda en la base de datos.
        //La clase EmpleadoMapper se encarga de hacer la conversión de tipos de datos según se requiera.
        Empleado newEmpleado = empleadoRepository.save(empleadoMapper.dtoToEntity(dto));
        return empleadoMapper.entityToDTO(newEmpleado);
    }

    @Override
    public EmpleadoDTO updateEmpleado(Long id, EmpleadoDTO dto) throws Exception {
        try {
            // Se verifica si el empleado a modificar existe
            Optional<Empleado> opt = empleadoRepository.findById(id);
            if (opt.isPresent()) {
                // Se obtiene el empleado de la base de datos y se modifican sólo los datos del DTO que no son nulos.
                Empleado empleado = opt.get();
                Empleado newEmpleado = empleadoMapper.dtoToEntity(dto);
                if (newEmpleado.getNombre() != null) {
                    empleado.setNombre(newEmpleado.getNombre());
                }
                if (newEmpleado.getApellido() != null) {
                    empleado.setApellido(newEmpleado.getApellido());
                }
                if (newEmpleado.getEmail() != null) {
                    empleado.setEmail(newEmpleado.getEmail());
                }
                if (newEmpleado.getFechaDeNacimiento() != null) {
                    empleado.setFechaDeNacimiento(newEmpleado.getFechaDeNacimiento());
                }
                //Se guarda en la base de datos y se retorna el empleado actualizado.
                return empleadoMapper.entityToDTO(empleadoRepository.save(empleado));
            } else {
                //En caso de no encontrarse el empleado, ahorra una excepción.
                throw new Exception("Usuario no existe.");
            }
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
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
        Optional<Empleado> opt = empleadoRepository.findById(id);
        if(opt.isPresent()){
            empleadoRepository.deleteById(id);
        }
    }
}

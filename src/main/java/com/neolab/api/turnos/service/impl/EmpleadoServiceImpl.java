package com.neolab.api.turnos.service.impl;

import com.neolab.api.turnos.dto.EmpleadoDTO;
import com.neolab.api.turnos.entity.Empleado;
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

    @Override
    public EmpleadoDTO createEmpleado(EmpleadoDTO dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Empleado empleado = new Empleado();

        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setEmail(dto.getEmail());
        empleado.setPassword(dto.getPassword());
        empleado.setTelefono(dto.getTelefono());
        LocalDate fechaDeNacimiento = LocalDate.parse(dto.getFechaDeNacimiento(), formatter );
        LocalDate fechaDeAlta = LocalDate.parse(dto.getFechaAlta(), formatter );
        empleado.setFechaDeNacimiento(fechaDeNacimiento);
        empleado.setFechaAlta(fechaDeAlta);

        Empleado newEmpleado = empleadoRepository.save(empleado);

        EmpleadoDTO response = new EmpleadoDTO();
        response.setId(newEmpleado.getId());
        response.setNombre(newEmpleado.getNombre());
        response.setApellido(newEmpleado.getApellido());
        response.setEmail(newEmpleado.getEmail());
        response.setPassword(newEmpleado.getPassword());
        response.setTelefono(newEmpleado.getTelefono());
        response.setFechaDeNacimiento(newEmpleado.getFechaDeNacimiento().format(formatter));
        response.setFechaAlta(newEmpleado.getFechaAlta().format(formatter));

        return response;
    }

    @Override
    public EmpleadoDTO updateEmpleado(Long id, EmpleadoDTO empleado) {
        return null;
    }

    @Override
    public List<EmpleadoDTO> getEmpleados() {
        List<Empleado> empleados = empleadoRepository.findAll();
        List<EmpleadoDTO> dtos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        for (Empleado empleado: empleados) {
            EmpleadoDTO dto = new EmpleadoDTO();
            dto.setId(empleado.getId());
            dto.setNombre(empleado.getNombre());
            dto.setApellido(empleado.getApellido());
            dto.setEmail(empleado.getEmail());
            dto.setPassword(empleado.getPassword());
            dto.setTelefono(empleado.getTelefono());
            dto.setFechaDeNacimiento(empleado.getFechaDeNacimiento().format(formatter));
            dto.setFechaAlta(empleado.getFechaAlta().format(formatter));
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public EmpleadoDTO getEmpleadoById(Long id) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Optional<Empleado> opt = empleadoRepository.findById(id);
        if(opt.isPresent()){
            EmpleadoDTO response = new EmpleadoDTO();
            Empleado newEmpleado = opt.get();

            response.setId(newEmpleado.getId());
            response.setNombre(newEmpleado.getNombre());
            response.setNombre(newEmpleado.getApellido());
            response.setEmail(newEmpleado.getEmail());
            response.setPassword(newEmpleado.getPassword());
            response.setTelefono(newEmpleado.getTelefono());
            response.setFechaDeNacimiento(newEmpleado.getFechaDeNacimiento().format(formatter));
            response.setFechaAlta(newEmpleado.getFechaAlta().format(formatter));

            return response;
        }

        return null;
    }

    @Override
    public void deleteEmpleado(Long id) {

    }
}

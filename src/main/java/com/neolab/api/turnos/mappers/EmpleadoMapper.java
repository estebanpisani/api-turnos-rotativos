package com.neolab.api.turnos.mappers;

import com.neolab.api.turnos.dto.EmpleadoDTO;
import com.neolab.api.turnos.entity.Empleado;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
@Component
public class EmpleadoMapper {

    public EmpleadoDTO entityToDTO(Empleado empleado){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        EmpleadoDTO dto = new EmpleadoDTO();

        dto.setId(empleado.getId());
        dto.setNombre(empleado.getNombre());
        dto.setApellido(empleado.getApellido());
        dto.setEmail(empleado.getEmail());
        dto.setFechaDeNacimiento(empleado.getFechaDeNacimiento().format(formatter));
        dto.setFechaAlta(empleado.getFechaAlta().format(formatter));
        if(empleado.getFechaBaja()!=null) {
            dto.setFechaBaja(empleado.getFechaBaja().format(formatter));
        }

        return dto;
    }
    public Empleado dtoToEntity(EmpleadoDTO dto) throws Exception{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        //Se crea una entidad Empleado vacía.
        Empleado empleado = new Empleado();
        //Se validan los campos requeridos
        if(dto.getNombre() == null || dto.getNombre().isEmpty() || dto.getApellido() == null || dto.getApellido().isEmpty() || dto.getEmail() == null || dto.getEmail().isEmpty()|| dto.getFechaDeNacimiento() == null || dto.getFechaDeNacimiento().isEmpty()){
            throw new Exception("Campos requeridos.");
        }
        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setEmail(dto.getEmail());
        //Se setean las fechas con los formatos requeridos
        LocalDate fechaDeNacimiento = LocalDate.parse(dto.getFechaDeNacimiento(), formatter);
        empleado.setFechaDeNacimiento(fechaDeNacimiento);
        if(dto.getFechaAlta() != null) {
            LocalDate fechaDeAlta = LocalDate.parse(dto.getFechaAlta(), formatter);
            empleado.setFechaAlta(fechaDeAlta);
        }
        if(dto.getFechaBaja() != null) {
            LocalDate fechaBaja = LocalDate.parse(dto.getFechaBaja(), formatter);
            empleado.setFechaBaja(fechaBaja);
        }
        return empleado;
    }
    public List<EmpleadoDTO> entityListToDTOList(List<Empleado> empleados){
        List<EmpleadoDTO> dtos = new ArrayList<>();
        for (Empleado empleado: empleados) {
            EmpleadoDTO dto = this.entityToDTO(empleado);
            dtos.add(dto);
        }
        return dtos;
    }
    public List<Empleado> dtoListToEntityList(List<EmpleadoDTO> dtos){
        List<Empleado> empleados = new ArrayList<>();
        for (EmpleadoDTO dto: dtos) {
            Empleado empleado = null;
            try {
                empleado = this.dtoToEntity(dto);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            empleados.add(empleado);
        }
        return empleados;
    }
}

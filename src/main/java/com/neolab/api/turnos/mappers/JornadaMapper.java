package com.neolab.api.turnos.mappers;

import com.neolab.api.turnos.dto.JornadaDTO;
import com.neolab.api.turnos.entity.*;
import com.neolab.api.turnos.enums.JornadaEnum;
import com.neolab.api.turnos.repository.EmpleadoRepository;
import com.neolab.api.turnos.repository.TipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class JornadaMapper {
    @Autowired
    TipoRepository tipoRepository;
    @Autowired
    EmpleadoRepository empleadoRepository;

    public JornadaDTO entityToDTO(Jornada jornada){
        DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        JornadaDTO dto = new JornadaDTO();

        dto.setId(jornada.getId());
        dto.setEntrada(jornada.getEntrada().format(formatterHour));
        dto.setSalida(jornada.getSalida().format(formatterHour));
        dto.setTipo(jornada.getTipo().getNombre());
        for (Empleado empleado : jornada.getEmpleados()) {
            dto.getEmpleadosId().add(empleado.getId());
        }

        return dto;
    }
    public Jornada dtoToEntity(JornadaDTO dto) throws Exception{

        DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        //Se crea una entidad Jornada vacía.
        Jornada jornada = new Jornada();

        //Se validan los campos requeridos
        if(dto.getTipo() == null || dto.getTipo().isEmpty() || dto.getEntrada() == null || dto.getEntrada().isEmpty()){
            throw new Exception("Campos requeridos.");
        }
//Se verifica si el tipo de jornada ingresado ya se encuentra en la base de datos. En caso de que sí, lo asigna como tipo de jornada.
        if(tipoRepository.findByNombre(dto.getTipo().trim().toLowerCase().replace(" ", "_")).orElse(null) != null){
            System.out.println("Encontro Tipo.");
            System.out.println(tipoRepository.findByNombre(dto.getTipo().trim().toLowerCase().replace(" ", "_")).get().getNombre());
            jornada.setTipo(tipoRepository.findByNombre(dto.getTipo().trim().toLowerCase().replace(" ", "_")).get());
        }
        //En caso de que no, se crea una jornada de los tipos predefinidos.
        else if(dto.getTipo().trim().toLowerCase().equals("normal")){
            Normal normal = new Normal();
            jornada.setTipo(normal);
        }
        else if(dto.getTipo().trim().toLowerCase().equals("extra")){
            jornada.setTipo(new Extra());
        }
        else if(dto.getTipo().trim().toLowerCase().equals("vacaciones")){
            jornada.setTipo(new Vacaciones());
        }
        else if(dto.getTipo().trim().toLowerCase().replace(" ", "_").equals("dia_libre")){
            jornada.setTipo(new DiaLibre());
        }
        else{
            throw new Exception("El tipo de jornada ingresado no existe.");
        }
//Verifica que los usuarios ingresados existan en la base de datos.
        if(dto.getEmpleadosId().size()>0) {
            if(dto.getEmpleadosId().size()<=2) {
                for (Long id : dto.getEmpleadosId()) {
                    if (empleadoRepository.existsById(id)) {
                        jornada.addEmpleado(empleadoRepository.getReferenceById(id));
                    }
                    else{
                        throw new Exception("Empleado no existe.");
                    }
                }
            }
            else{
                throw new Exception("Solo se permiten 2 empleados máximo.");
            }
        }

        //Si es Día Libre se le asigna el horario de entrada y salida para que siempre sea de 24hs
        if (jornada.getTipo().getNombre().equals("dia_libre")) {;
            jornada.setEntrada(LocalDateTime.of(LocalDate.parse(dto.getEntrada(), formatterDate), LocalTime.of(0, 0)));
            jornada.setSalida(LocalDateTime.of(LocalDate.parse(dto.getEntrada(), formatterDate), LocalTime.of(23, 59)));
            return jornada;
        }
        //En caso de jornada normal, extra o vacaciones, se requiere el dato de la fecha/hora de salida.
        if (dto.getSalida() == null || dto.getSalida().isEmpty()) {
            throw new Exception("La Fecha/hora de salida es requerida.");
        }
        //En caso de Vacaciones se le asigna el horario de entrada y salida para que siempre sea de 24hs, solo se tiene en cuenta la fecha.
        if(jornada.getTipo().getNombre().equals("vacaciones")){
            jornada.setEntrada(LocalDateTime.of(LocalDate.parse(dto.getEntrada(), formatterDate), LocalTime.of(0, 0)));
            jornada.setSalida(LocalDateTime.of(LocalDate.parse(dto.getSalida(), formatterDate), LocalTime.of(23, 59)));
            return jornada;
        }
        if (jornada.getTipo().getNombre().equals("normal") || jornada.getTipo().getNombre().equals("extra")) {
            LocalDateTime horaEntrada = LocalDateTime.parse(dto.getEntrada(), formatterHour);
            LocalDateTime horaSalida = LocalDateTime.parse(dto.getSalida(), formatterHour);
            jornada.setEntrada(horaEntrada);
            jornada.setSalida(horaSalida);
        }
        return jornada;
    }
    public List<JornadaDTO> entityListToDTOList(List<Jornada> jornadas){
        List<JornadaDTO> dtos = new ArrayList<>();
        for (Jornada jornada: jornadas) {
            JornadaDTO dto = this.entityToDTO(jornada);
            dtos.add(dto);
        }
        return dtos;
    }
    public List<Jornada> dtoListToEntityList(List<JornadaDTO> dtos) throws Exception {
        List<Jornada> jornadas = new ArrayList<>();
        for (JornadaDTO dto: dtos) {
            Jornada jornada = null;
            try {
                jornada = this.dtoToEntity(dto);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
            jornadas.add(jornada);
        }
        return jornadas;
    }
}

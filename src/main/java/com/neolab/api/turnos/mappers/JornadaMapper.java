package com.neolab.api.turnos.mappers;

import com.neolab.api.turnos.dto.JornadaDTO;
import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.entity.Jornada;
import com.neolab.api.turnos.enums.JornadaEnum;
import com.neolab.api.turnos.repository.EmpleadoRepository;
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
    EmpleadoRepository empleadoRepository;
    public JornadaDTO entityToDTO(Jornada jornada){
        DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        JornadaDTO dto = new JornadaDTO();

        dto.setId(jornada.getId());
        dto.setEntrada(jornada.getEntrada().format(formatterHour));
        dto.setSalida(jornada.getSalida().format(formatterHour));
        dto.setTipo(jornada.getTipo().toString());
        for (Empleado empleado : jornada.getEmpleados()) {
            dto.getEmpleadosId().add(empleado.getId());
        }
        return dto;
    }
    public Jornada dtoToEntity(JornadaDTO dto) throws Exception{

        DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Jornada jornada = new Jornada();

        if(dto.getTipo() == null || dto.getTipo().isEmpty() || dto.getEntrada() == null || dto.getEntrada().isEmpty()){
            throw new Exception("Campos requeridos.");
        }

        if (dto.getTipo().toUpperCase().trim().replace(" ", "_").equals(JornadaEnum.DIA_LIBRE.toString())) {
            jornada.setTipo(JornadaEnum.DIA_LIBRE);
            //Si es Día Libre se le setea el horario de entrada y salida para que siempre sea de 24hs
            jornada.setEntrada(LocalDateTime.of(LocalDate.parse(dto.getEntrada(), formatterDate), LocalTime.of(0, 0)));
            jornada.setSalida(LocalDateTime.of(LocalDate.parse(dto.getEntrada(), formatterDate), LocalTime.of(23, 59)));
            return jornada;
        }
        //En caso de jornada normal, extra o vacaciones, se requiere el dato de la fecha/hora de salida.
        if (dto.getSalida() == null || dto.getSalida().isEmpty()) {
            throw new Exception("La Fecha/hora de salida es requerida.");
        }
        if(dto.getTipo().toUpperCase().trim().equals(JornadaEnum.VACACIONES.toString())){
            jornada.setTipo(JornadaEnum.VACACIONES);
            jornada.setEntrada(LocalDateTime.of(LocalDate.parse(dto.getEntrada(), formatterDate), LocalTime.of(0, 0)));
            jornada.setSalida(LocalDateTime.of(LocalDate.parse(dto.getSalida(), formatterDate), LocalTime.of(23, 59)));
            return jornada;
        }
        if (dto.getTipo().toUpperCase().trim().equals(JornadaEnum.NORMAL.toString())) {
            jornada.setTipo(JornadaEnum.NORMAL);
        }
        if (dto.getTipo().toUpperCase().trim().equals(JornadaEnum.EXTRA.toString())) {
            jornada.setTipo(JornadaEnum.EXTRA);
        }
        LocalDateTime horaEntrada = LocalDateTime.parse(dto.getEntrada(), formatterHour);
        jornada.setEntrada(horaEntrada);
        LocalDateTime horaSalida = LocalDateTime.parse(dto.getSalida(), formatterHour);
        jornada.setSalida(horaSalida);
        //Se agregan los empleados a la jornada laboral solo si existen y no superan el máximo permitido.
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

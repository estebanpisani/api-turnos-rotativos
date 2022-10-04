package com.neolab.api.turnos.mappers;

import com.neolab.api.turnos.dto.JornadaDTO;
import com.neolab.api.turnos.entity.Jornada;
import com.neolab.api.turnos.enums.JornadaEnum;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class JornadaMapper {
    public JornadaDTO entityToDTO(Jornada jornada){
        DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        JornadaDTO dto = new JornadaDTO();

        dto.setId(jornada.getId());
        dto.setEmpleadoId(jornada.getEmpleadoId());
        dto.setFecha(jornada.getFecha().format(formatterDate));
        dto.setHoraEntrada(jornada.getHoraEntrada().format(formatterHour));
        dto.setHoraSalida(jornada.getHoraSalida().format(formatterHour));
        dto.setTipo(jornada.getTipo().toString());

        return dto;
    }
    public Jornada dtoToEntity(JornadaDTO dto){
        DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Jornada jornada = new Jornada();

        jornada.setEmpleadoId(dto.getEmpleadoId());

        LocalDate fecha = LocalDate.parse(dto.getFecha(), formatterDate );
        jornada.setFecha(fecha);

        if(dto.getHoraEntrada()!=null && !dto.getHoraEntrada().isEmpty()){
            LocalDateTime horaEntrada = LocalDateTime.parse(dto.getHoraEntrada(), formatterHour);
            jornada.setHoraEntrada(horaEntrada);
        }
        if(dto.getHoraSalida()!=null && !dto.getHoraSalida().isEmpty()) {
            LocalDateTime horaSalida = LocalDateTime.parse(dto.getHoraSalida(), formatterHour);
            jornada.setHoraSalida(horaSalida);
        }

        if(dto.getTipo().toUpperCase().trim().equals(JornadaEnum.NORMAL.toString())){
            jornada.setTipo(JornadaEnum.NORMAL);
        } else if (dto.getTipo().toUpperCase().trim().equals(JornadaEnum.EXTRA.toString())){
            jornada.setTipo(JornadaEnum.EXTRA);
        } else if (dto.getTipo().toUpperCase().trim().replace(" ", "_").equals(JornadaEnum.DIA_LIBRE.toString())){
            jornada.setTipo(JornadaEnum.DIA_LIBRE);
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
    public List<Jornada> dtoListToEntityList(List<JornadaDTO> dtos){
        List<Jornada> jornadas = new ArrayList<>();
        for (JornadaDTO dto: dtos) {
            Jornada jornada = this.dtoToEntity(dto);
            jornadas.add(jornada);
        }
        return jornadas;
    }
}

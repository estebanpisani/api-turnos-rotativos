package com.neolab.api.turnos.service.impl;

import com.neolab.api.turnos.dto.EmpleadoDTO;
import com.neolab.api.turnos.dto.JornadaDTO;
import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.entity.Jornada;
import com.neolab.api.turnos.enums.JornadaEnum;
import com.neolab.api.turnos.repository.JornadaRepository;
import com.neolab.api.turnos.service.JornadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class JornadaServiceImpl implements JornadaService {

    @Autowired
    JornadaRepository jornadaRepository;
    @Override
    public JornadaDTO createJornada(JornadaDTO dto) {
        DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Jornada jornada = new Jornada();

        LocalDate fecha = LocalDate.parse(dto.getFecha(), formatterDate );
        LocalDateTime horaEntrada = LocalDateTime.parse(dto.getHoraEntrada(), formatterHour);
        LocalDateTime horaSalida = LocalDateTime.parse(dto.getHoraSalida(), formatterHour);

        jornada.setEmpleadoId(dto.getEmpleadoId());

        if(dto.getTipo().toUpperCase().trim().equals(JornadaEnum.NORMAL.toString())){
            jornada.setTipo(JornadaEnum.NORMAL);
        } else if (dto.getTipo().toUpperCase().trim().equals(JornadaEnum.EXTRA.toString())){
            jornada.setTipo(JornadaEnum.EXTRA);
        } else if (dto.getTipo().toUpperCase().trim().equals(JornadaEnum.DIA_LIBRE.toString())){
            jornada.setTipo(JornadaEnum.DIA_LIBRE);
        }
        jornada.setFecha(fecha);
        jornada.setHoraEntrada(horaEntrada);
        jornada.setHoraSalida(horaSalida);
        Jornada newJornada = jornadaRepository.save(jornada);

        JornadaDTO response = new JornadaDTO();
        response.setEmpleadoId(newJornada.getEmpleadoId());
        response.setFecha(newJornada.getFecha().format(formatterDate));
        response.setHoraEntrada(newJornada.getHoraEntrada().format(formatterHour));
        response.setHoraSalida(newJornada.getHoraSalida().format(formatterHour));
        response.setTipo(newJornada.getTipo().toString());

        return response;
    }

    @Override
    public JornadaDTO updateJornada(Long id, JornadaDTO dto) {
        return null;
    }

    @Override
    public List<JornadaDTO> getJornadas() {

        List<Jornada> jornadas = jornadaRepository.findAll();
        List<JornadaDTO> dtos = new ArrayList<>();
        DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        for (Jornada jornada: jornadas) {
            JornadaDTO dto = new JornadaDTO();
            dto.setTipo(jornada.getTipo().toString());
            dto.setEmpleadoId(jornada.getEmpleadoId());
            dto.setFecha(jornada.getFecha().format(formatterDate));
            dto.setHoraEntrada(jornada.getHoraEntrada().format(formatterHour));
            dto.setHoraSalida(jornada.getHoraSalida().format(formatterHour));
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public List<JornadaDTO> getJornadasByEmpleado(Long id) {
        List<Jornada> jornadas = jornadaRepository.findByEmpleadoId(id);
        List<JornadaDTO> dtos = new ArrayList<>();
        DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        for (Jornada jornada: jornadas) {
            JornadaDTO dto = new JornadaDTO();
            dto.setTipo(jornada.getTipo().toString());
            dto.setEmpleadoId(jornada.getEmpleadoId());
            dto.setFecha(jornada.getFecha().format(formatterDate));
            dto.setHoraEntrada(jornada.getHoraEntrada().format(formatterHour));
            dto.setHoraSalida(jornada.getHoraSalida().format(formatterHour));
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public JornadaDTO getJornadaById(Long id) {
        return null;
    }

    @Override
    public void deleteJornada(Long id) {

    }
}

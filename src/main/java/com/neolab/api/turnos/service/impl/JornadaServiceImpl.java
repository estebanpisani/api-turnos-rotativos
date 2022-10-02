package com.neolab.api.turnos.service.impl;

import com.neolab.api.turnos.dto.EmpleadoDTO;
import com.neolab.api.turnos.dto.JornadaDTO;
import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.entity.Jornada;
import com.neolab.api.turnos.enums.JornadaEnum;
import com.neolab.api.turnos.mappers.JornadaMapper;
import com.neolab.api.turnos.repository.JornadaRepository;
import com.neolab.api.turnos.service.JornadaService;
import com.neolab.api.turnos.validators.JornadaValidator;
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
    @Autowired
    JornadaMapper jornadaMapper;
    @Autowired
    JornadaValidator jornadaValidator;
    @Override
    public JornadaDTO createJornada(JornadaDTO dto) {
        Jornada jornada = jornadaMapper.dtoToEntity(dto);
        if(jornadaValidator.jornadaNormalValidator(jornada)){
        Jornada newJornada = jornadaRepository.save(jornada);
        JornadaDTO response = jornadaMapper.entityToDTO(newJornada);
        return response;
        }
        return null;
    }

    @Override
    public JornadaDTO updateJornada(Long id, JornadaDTO dto) {
        return null;
    }

    @Override
    public List<JornadaDTO> getAllJornadas() {
        List<Jornada> jornadas = jornadaRepository.findAll();
        List<JornadaDTO> dtos = jornadaMapper.entityListToDTOList(jornadas);
        return dtos;
    }

    @Override
    public List<JornadaDTO> getJornadasByEmpleado(Long id) {
        List<Jornada> jornadas = jornadaRepository.findByEmpleadoId(id);
        List<JornadaDTO> dtos = jornadaMapper.entityListToDTOList(jornadas);
        return dtos;
    }

    @Override
    public JornadaDTO getJornadaById(Long id) {
        Optional<Jornada> opt = jornadaRepository.findById(id);
        if(opt.isPresent()){
            JornadaDTO response = jornadaMapper.entityToDTO(opt.get());
            return response;
        }
        return null;
    }
    public String getEmpleadoByJornadaId(Long id) {
        Optional<Jornada> opt = jornadaRepository.findById(id);
        if(opt.isPresent()){
            String response = opt.get().getEmpleado().getNombre();
            if(response != null){
                return response;
            }else{
                System.out.println("Empleado nulo.");
            }
        }
        return null;
    }

    @Override
    public void deleteJornada(Long id) {
        Optional<Jornada> opt = jornadaRepository.findById(id);
        if(opt.isPresent()){
            jornadaRepository.deleteById(id);
        }
    }
}

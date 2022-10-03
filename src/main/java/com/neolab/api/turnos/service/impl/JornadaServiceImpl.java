package com.neolab.api.turnos.service.impl;

import com.neolab.api.turnos.dto.JornadaDTO;
import com.neolab.api.turnos.entity.Jornada;
import com.neolab.api.turnos.enums.JornadaEnum;
import com.neolab.api.turnos.mappers.JornadaMapper;
import com.neolab.api.turnos.repository.JornadaRepository;
import com.neolab.api.turnos.service.JornadaService;
import com.neolab.api.turnos.validators.JornadaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        //Se verifica si la jornada tiene un formato válido
        if(jornadaValidator.esJornadaValida(jornada)) {
            if (jornada.getTipo().equals(JornadaEnum.NORMAL)) {
                if (jornadaValidator.jornadaNormalValidator(jornada)) {
                    Jornada newJornada = jornadaRepository.save(jornada);
                    return jornadaMapper.entityToDTO(newJornada);
                }
            }
            else if (jornada.getTipo().equals(JornadaEnum.EXTRA)) {
                if (jornadaValidator.jornadaExtraValidator(jornada)) {
                    Jornada newJornada = jornadaRepository.save(jornada);
                    return jornadaMapper.entityToDTO(newJornada);
                }
            }
            else if (jornada.getTipo().equals(JornadaEnum.DIA_LIBRE)) {
                Jornada newJornada = jornadaRepository.save(jornada);
                return jornadaMapper.entityToDTO(newJornada);
            }
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
        return jornadaMapper.entityListToDTOList(jornadas);
    }

    @Override
    public List<JornadaDTO> getJornadasByEmpleado(Long id, String tipo) {
        List<Jornada> jornadas = jornadaRepository.findByEmpleadoId(id);
        List<JornadaDTO> dtos = new ArrayList<>();
        if(tipo != null){
            List<Jornada> jornadasFiltradas = jornadas.stream().filter(item-> item.getTipo().toString().equalsIgnoreCase(tipo.trim().replace(" ","_"))).collect(Collectors.toList());
            if(jornadasFiltradas.size()>0){
            dtos = jornadaMapper.entityListToDTOList(jornadasFiltradas);
            }
            return dtos;
        }
        dtos = jornadaMapper.entityListToDTOList(jornadas);
        return dtos;
    }

    @Override
    public JornadaDTO getJornadaById(Long id) {
        Optional<Jornada> opt = jornadaRepository.findById(id);
        if(opt.isPresent()){
            return jornadaMapper.entityToDTO(opt.get());
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

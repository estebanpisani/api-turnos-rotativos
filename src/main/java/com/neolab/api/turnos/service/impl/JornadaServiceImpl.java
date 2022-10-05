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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        //Se crea una entidad con los datos que llegan desde el DTO.
        Jornada jornada = jornadaMapper.dtoToEntity(dto);
        //Se validan los datos según el tipo de jornada.
        if (jornada.getTipo().equals(JornadaEnum.DIA_LIBRE)) {
            if(jornadaValidator.usuarioExiste(jornada) && jornadaValidator.diaLibreValidator(jornada)) {
                Jornada newJornada = jornadaRepository.save(jornada);
                return jornadaMapper.entityToDTO(newJornada);
            }
        }
        if(jornada.getTipo().equals(JornadaEnum.VACACIONES)){
            if(jornadaValidator.usuarioExiste(jornada) && jornadaValidator.horarioValido(jornada)) {
                Jornada newJornada = jornadaRepository.save(jornada);
                return jornadaMapper.entityToDTO(newJornada);
            }
        }
        else if(jornadaValidator.usuarioExiste(jornada) && jornadaValidator.horarioValido(jornada)) {
        //Si es jornada normal o extra, se verifica si la jornada tiene un formato válido
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
        }
        return null;
    }

    @Override
    public JornadaDTO updateJornada(Long id, JornadaDTO dto) {
        //Se busca la entidad en la base de datos por su id.
        Optional<Jornada> opt = jornadaRepository.findById(id);
        if(opt.isPresent()){
            // Se obtiene la jornada de la base de datos y se modifican sólo los datos del DTO que no son nulos y distintos.
            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            Jornada jornadaDB = opt.get();

//            Jornada jornadaUpd = jornadaMapper.dtoToEntity(dto);
            if(dto.getFecha() != null){
                LocalDate fecha = LocalDate.parse(dto.getFecha(), formatterDate);
                jornadaDB.setFecha(fecha);
            }
            if(dto.getHoraEntrada() != null){
                LocalDateTime horaEntrada = LocalDateTime.parse(dto.getHoraEntrada(), formatterHour);
                jornadaDB.setHoraEntrada(horaEntrada);
            }
            if(dto.getHoraSalida() != null){
                LocalDateTime horaSalida = LocalDateTime.parse(dto.getHoraSalida(), formatterHour);
                jornadaDB.setHoraSalida(horaSalida);
            }
            if (jornadaDB.getTipo().equals(JornadaEnum.DIA_LIBRE)) {
                if(jornadaValidator.diaLibreValidator(jornadaDB)) {
                    return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }
            }
            else if(jornadaDB.getTipo().equals(JornadaEnum.VACACIONES)){
                if(jornadaValidator.horarioValido(jornadaDB)) {
                    return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }
            }
            if(jornadaValidator.horarioValido(jornadaDB)){
                if(jornadaDB.getTipo().equals(JornadaEnum.NORMAL) && jornadaValidator.jornadaNormalValidator(jornadaDB)) {

                    return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }
                else if (jornadaDB.getTipo().equals(JornadaEnum.EXTRA) && jornadaValidator.jornadaExtraValidator(jornadaDB)){
                    return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }
                else {
                    return null;
                }
            }
            return null;
        }
        else{
            return null;
        }
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

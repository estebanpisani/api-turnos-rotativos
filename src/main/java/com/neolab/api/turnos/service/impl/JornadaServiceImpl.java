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
    public JornadaDTO createJornada(JornadaDTO dto) throws Exception {
        try{
        //Se crea una entidad con los datos que llegan desde el DTO.
            Jornada jornada = jornadaMapper.dtoToEntity(dto);

            jornadaValidator.usuarioExiste(jornada);
            //Se validan los datos según el tipo de jornada.
            if (jornada.getTipo().equals(JornadaEnum.DIA_LIBRE)) {
                jornadaValidator.diaLibreValidator(jornada);
                Jornada newJornada = jornadaRepository.save(jornada);
                return jornadaMapper.entityToDTO(newJornada);
            }
            //Si es vacaciones, jornada normal o extra, se verifica si la jornada tiene un formato válido.
            jornadaValidator.horarioValido(jornada);

            if(jornada.getTipo().equals(JornadaEnum.VACACIONES)){
                Jornada newJornada = jornadaRepository.save(jornada);
                return jornadaMapper.entityToDTO(newJornada);
            }
            if (jornada.getTipo().equals(JornadaEnum.NORMAL)) {
                jornadaValidator.jornadaNormalValidator(jornada);
                Jornada newJornada = jornadaRepository.save(jornada);
                return jornadaMapper.entityToDTO(newJornada);
            }
            else if (jornada.getTipo().equals(JornadaEnum.EXTRA)) {
                jornadaValidator.jornadaExtraValidator(jornada);
                Jornada newJornada = jornadaRepository.save(jornada);
                return jornadaMapper.entityToDTO(newJornada);
            }
        }
        catch(Exception e){
                throw new Exception(e.getMessage());
        }
        return null;
    }

    @Override
    public JornadaDTO updateJornada(Long id, JornadaDTO dto) throws Exception {
        try {
            //Se busca la entidad en la base de datos por su id.
            Optional<Jornada> opt = jornadaRepository.findById(id);
            if (opt.isPresent()) {
                // Se obtiene la jornada de la base de datos y se modifican sólo los datos del DTO que no son nulos y distintos.
                DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                Jornada jornadaDB = opt.get();
//            Jornada jornadaUpd = jornadaMapper.dtoToEntity(dto);
                if (dto.getEntrada() != null) {
                    LocalDateTime horaEntrada = LocalDateTime.parse(dto.getEntrada(), formatterHour);
                    jornadaDB.setEntrada(horaEntrada);
                }
                if (dto.getSalida() != null) {
                    LocalDateTime horaSalida = LocalDateTime.parse(dto.getSalida(), formatterHour);
                    jornadaDB.setSalida(horaSalida);
                }
                if (jornadaDB.getTipo().equals(JornadaEnum.DIA_LIBRE)) {
                        jornadaValidator.diaLibreValidator(jornadaDB);
                        return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }

                jornadaValidator.horarioValido(jornadaDB);

                if (jornadaDB.getTipo().equals(JornadaEnum.VACACIONES)) {
                    return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }
                if (jornadaDB.getTipo().equals(JornadaEnum.NORMAL)) {
                    jornadaValidator.jornadaNormalValidator(jornadaDB);
                    return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }
                if (jornadaDB.getTipo().equals(JornadaEnum.EXTRA)) {
                    jornadaValidator.jornadaExtraValidator(jornadaDB);
                    return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }
                else {
                    throw new Exception("Tipo de jornada inexistente.");
                }
            }
            else {
                throw new Exception("Usuario no existe.");
            }
        }
        catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public List<JornadaDTO> getAllJornadas(String tipo) {
        List<Jornada> jornadas = jornadaRepository.findAll();
        List<JornadaDTO> dtos = new ArrayList<>();
        if(tipo != null){
            List<Jornada> jornadasFiltradas = jornadas.stream().filter(item-> item.getTipo().toString().equalsIgnoreCase(tipo.trim().replace(" ","_"))).collect(Collectors.toList());
            if(jornadasFiltradas.size()>0){
                dtos = jornadaMapper.entityListToDTOList(jornadasFiltradas);
            }
            return dtos;
        }
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
    public Boolean deleteJornada(Long id) {
        Optional<Jornada> opt = jornadaRepository.findById(id);
        if(opt.isPresent()){
            jornadaRepository.deleteById(id);
            return true;
        }else{
            return false;
        }
    }
}

package com.neolab.api.turnos.service.impl;

import com.neolab.api.turnos.dto.JornadaDTO;
import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.entity.Jornada;
import com.neolab.api.turnos.enums.JornadaEnum;
import com.neolab.api.turnos.mappers.JornadaMapper;
import com.neolab.api.turnos.repository.EmpleadoRepository;
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
    @Autowired
    EmpleadoRepository empleadoRepository;
    @Override
    public JornadaDTO createJornada(JornadaDTO dto) throws Exception {
        try{
        //Se crea una entidad con los datos que llegan desde el DTO.
            Jornada jornada = jornadaMapper.dtoToEntity(dto);

            //Se validan los datos según el tipo de jornada.
            if (jornada.getTipo().equals(JornadaEnum.DIA_LIBRE)) {
//                jornadaValidator.diaLibreValidator(jornada);
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
                if(jornadaValidator.obtenerHoras(jornada)>=6 && jornadaValidator.obtenerHoras(jornada)<=8) {
                    //Verifica si no hay otra jornada en ese horario
                    jornadaValidator.horarioDisponible(jornada);
                    if(jornada.getEmpleados().size()>0){
                        for (Empleado empleado: jornada.getEmpleados()) {
                            jornadaValidator.jornadaNormalValidator(jornada, empleado);
                        }
                    }
                    Jornada newJornada = jornadaRepository.save(jornada);
                    return jornadaMapper.entityToDTO(newJornada);
                }
                else{
                    throw new Exception("No tiene entre 6 y 8hs");
                }
            }
            else if (jornada.getTipo().equals(JornadaEnum.EXTRA)) {
                if (jornadaValidator.obtenerHoras(jornada) >= 2 && jornadaValidator.obtenerHoras(jornada) <= 6) {
                    jornadaValidator.horarioDisponible(jornada);
                    if(jornada.getEmpleados().size()>0){
                        jornada.getEmpleados().stream().forEach(empleado -> {
                            try {
                                jornadaValidator.jornadaExtraValidator(jornada, empleado);
                            } catch (Exception e) {
                                throw new RuntimeException(e.getMessage());
                            }
                        });
    //                        for (Empleado empleado: jornada.getEmpleados()) {
    //                            jornadaValidator.jornadaExtraValidator(jornada, empleado);
    //                        }
                    }
                    Jornada newJornada = jornadaRepository.save(jornada);
                    return jornadaMapper.entityToDTO(newJornada);
                }
                else {
                    throw new Exception("No tiene entre 2 y 6hs");
                }
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
                // Se obtiene la jornada de la base de datos y se modifican solo los datos del DTO que no son nulos.
                DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                Jornada jornadaDB = opt.get();
                jornadaValidator.horarioDisponible(jornadaDB);
                if(dto.getEmpleadosId().size()>0 && dto.getEmpleadosId().size()<=2) {
                    List<Empleado> empleados = new ArrayList<>();
                    for (Long empleadosId : dto.getEmpleadosId()) {
                        if(empleadoRepository.existsById(empleadosId)){
                            jornadaValidator.jornadaNormalValidator(jornadaDB, empleadoRepository.getReferenceById(empleadosId));
                            empleados.add(empleadoRepository.getReferenceById(empleadosId));
                        }
                        else{
                            throw new Exception("Empleado no existe.");
                        }
                    }
                    jornadaDB.setEmpleados(empleados);
                }else{
                    throw new Exception("Solo se permiten 2 empleados máximo.");
                }
                if (dto.getEntrada() != null) {
                    LocalDateTime horaEntrada = LocalDateTime.parse(dto.getEntrada(), formatterHour);
                    jornadaDB.setEntrada(horaEntrada);
                }
                if (dto.getSalida() != null) {
                    LocalDateTime horaSalida = LocalDateTime.parse(dto.getSalida(), formatterHour);
                    jornadaDB.setSalida(horaSalida);
                }
                if (jornadaDB.getTipo().equals(JornadaEnum.DIA_LIBRE)) {
//                        jornadaValidator.diaLibreValidator(jornadaDB);
                        return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }

                jornadaValidator.horarioValido(jornadaDB);

                if (jornadaDB.getTipo().equals(JornadaEnum.VACACIONES)) {
                    return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }
                if (jornadaDB.getTipo().equals(JornadaEnum.NORMAL)) {
                    if(jornadaValidator.obtenerHoras(jornadaDB)>=6 && jornadaValidator.obtenerHoras(jornadaDB)<=8) {
                        return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                    }
                    else{
                        throw new Exception("No tiene entre 6 y 8hs");
                    }
                }
                if (jornadaDB.getTipo().equals(JornadaEnum.EXTRA)) {
//                    jornadaValidator.jornadaExtraValidator(jornadaDB);
                    return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }
                else {
                    throw new Exception("Tipo de jornada inexistente.");
                }
            }
            else {
                throw new Exception("Jornada no existe.");
            }
        }
        catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public List<JornadaDTO> getAllJornadas() {
        List<Jornada> jornadas = jornadaRepository.findAll();
        return jornadaMapper.entityListToDTOList(jornadas);
    }

    @Override
    public List<JornadaDTO> getJornadasByEmpleado(Long id, String tipo) {
        List<Jornada> jornadas = jornadaRepository.findJornadasByEmpleadosId(id);
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

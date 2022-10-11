package com.neolab.api.turnos.service.impl;

import com.neolab.api.turnos.dto.JornadaDTO;
import com.neolab.api.turnos.entity.*;
import com.neolab.api.turnos.mappers.JornadaMapper;
import com.neolab.api.turnos.repository.EmpleadoRepository;
import com.neolab.api.turnos.repository.JornadaRepository;
import com.neolab.api.turnos.service.JornadaService;
import com.neolab.api.turnos.validators.JornadaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JornadaServiceImpl implements JornadaService {
    @Autowired
    JornadaRepository jornadaRepository;
    @Autowired
    EmpleadoRepository empleadoRepository;
    @Autowired
    JornadaMapper jornadaMapper;
    @Autowired
    JornadaValidator jornadaValidator;
    @Override
    public JornadaDTO createJornada(JornadaDTO dto) throws Exception {
        //Se crea una entidad con los datos que llegan desde el DTO.
            try {
                Jornada jornada = jornadaMapper.dtoToEntity(dto);
                if(jornada != null) {
                    //Se validan los datos según el tipo de jornada.
                    if (jornada.getTipo().getNombre().equalsIgnoreCase("dia_libre")) {
                        if(jornada.getEmpleados().size()>0){
                            for (Empleado empleado: jornada.getEmpleados()) {
                                jornadaValidator.diaLibreValidator(jornada, empleado);
                            }
                        }
                        Jornada newJornada = jornadaRepository.save(jornada);
                        return jornadaMapper.entityToDTO(newJornada);
                    }
                    //Si es vacaciones, jornada normal o extra, se verifica si la jornada tiene un formato válido.
                    jornadaValidator.horarioValido(jornada);
                    //Antes de crear la jornada, pasa por todos los validadores para cada caso.
                    if(jornada.getTipo().getNombre().equalsIgnoreCase("vacaciones")){
                        Jornada newJornada = jornadaRepository.save(jornada);
                        return jornadaMapper.entityToDTO(newJornada);
                    }
                    if (jornada.getTipo().getNombre().equalsIgnoreCase("normal")) {
                        if(jornada.getEmpleados().size()>0){
                            for (Empleado empleado: jornada.getEmpleados()) {
                                jornadaValidator.jornadaNormalValidator(jornada, empleado);
                            }
                        }
                        Jornada newJornada = jornadaRepository.save(jornada);
                        return jornadaMapper.entityToDTO(newJornada);
                    }
                    if (jornada.getTipo().getNombre().equalsIgnoreCase("extra")) {
                        if(jornada.getEmpleados().size()>0){
                            for (Empleado empleado: jornada.getEmpleados()) {
                                try {
                                    jornadaValidator.jornadaExtraValidator(jornada, empleado);
                                }
                                catch (Exception e){
                                    throw new Exception(e.getMessage());
                                }
                            }
                        }
                        Jornada newJornada = jornadaRepository.save(jornada);
                        return jornadaMapper.entityToDTO(newJornada);
                    }
                    //En caso de no ser los tipos predefinidos, se usan validadores genéricos.
                    else{
                        throw new Exception("Error al asignar tipo.");
                    }
                    //TODO caso jornada creada por el usuario (Validador genérico)
                }
                else {
                    throw new Exception("Error al crear entidad.");
                }
            }catch(Exception e){
                throw new Exception(e.getMessage());
            }
    }

//    @Override
//    public JornadaDTO updateJornada(Long id, JornadaDTO dto) throws Exception {
//        return null;
//    }

    @Override
    public JornadaDTO updateJornada(Long id, JornadaDTO dto) throws Exception {
        try {
            //Se busca la entidad en la base de datos por su id.
            Optional<Jornada> opt = jornadaRepository.findById(id);
            if (opt.isPresent()) {
                // Se obtiene la jornada de la base de datos y se modifican sólo los datos del DTO que no son nulos y distintos.
                DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                Jornada jornadaDB = opt.get();
                //Si por el dto llega información de los empleados, los asigna a la jornada.
                if(dto.getEmpleadosId().size()>0) {
                    if(dto.getEmpleadosId().size()<=2) {
                        //Se verifica que todos los empleados existan en la base de datos.
                        if (dto.getEmpleadosId().stream().anyMatch(item -> !empleadoRepository.existsById(item))){
                            throw new Exception("Empleado no existe.");
                        }
                        Set<Empleado> empleados = new HashSet<>();
                        dto.getEmpleadosId().stream().forEach(item -> empleados.add(empleadoRepository.getReferenceById(item)));
                        jornadaDB.setEmpleados(empleados);
                    }
                    else{
                        throw new Exception("Solo se permiten 2 empleados máximo.");
                    }
                }

                if (dto.getEntrada() != null) {
                    LocalDateTime horaEntrada = LocalDateTime.parse(dto.getEntrada(), formatterHour);
                    jornadaDB.setEntrada(horaEntrada);
                }
                if (dto.getSalida() != null) {
                    LocalDateTime horaSalida = LocalDateTime.parse(dto.getSalida(), formatterHour);
                    jornadaDB.setSalida(horaSalida);
                }
                if (jornadaDB.getTipo().getNombre().equalsIgnoreCase("dia_libre")) {
                    if(jornadaDB.getEmpleados().size()>0){
                        for (Empleado empleado: jornadaDB.getEmpleados()) {
                            jornadaValidator.diaLibreValidator(jornadaDB, empleado);
                        }
                    }
                    return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }

                jornadaValidator.horarioValido(jornadaDB);

                if(jornadaDB.getTipo().getNombre().equalsIgnoreCase("vacaciones")){
                    return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }
                if (jornadaDB.getTipo().getNombre().equalsIgnoreCase("normal")) {
                    if(jornadaDB.getEmpleados().size()>0){
                        for (Empleado empleado: jornadaDB.getEmpleados()) {
                            jornadaValidator.jornadaNormalValidator(jornadaDB, empleado);
                        }
                    }
                    return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }
                if (jornadaDB.getTipo().getNombre().equalsIgnoreCase("extra")) {
                    if(jornadaDB.getEmpleados().size()>0){
                        for (Empleado empleado: jornadaDB.getEmpleados()) {
                            try {
                                jornadaValidator.jornadaExtraValidator(jornadaDB, empleado);
                            }
                            catch (Exception e){
                                throw new Exception(e.getMessage());
                            }
                        }
                    }
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
        //Se obtienen todos los registros de jornadas de la base de datos
        List<Jornada> jornadas = jornadaRepository.findAll();
        List<JornadaDTO> dtos = new ArrayList<>();
        //En caso de añadir un parámetro de tipo, se filtra con la API Stream
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
        //Se obtienen todos los registros de jornadas de la base de datos con el mismo ID de Empleado
        List<Jornada> jornadas = jornadaRepository.findByEmpleados(id);
        List<JornadaDTO> dtos = new ArrayList<>();
        //En caso de añadir un parámetro de tipo, se filtra con la API Stream
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

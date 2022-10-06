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
            //Se crea una entidad nueva con los datos que llegan desde el DTO.
            //Dentro de la clase JornadaMapper se realizan las validaciones por cada tipo
            Jornada jornada = jornadaMapper.dtoToEntity(dto);
            return jornadaMapper.entityToDTO(jornadaRepository.save(jornada));
        }
        catch(Exception e){
                throw new Exception(e.getMessage());
        }
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
                }
                else{
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
                jornadaValidator.horarioValido(jornadaDB);
                jornadaValidator.horarioDisponible(jornadaDB);

                if (jornadaDB.getTipo().equals(JornadaEnum.DIA_LIBRE)) {
//                        jornadaValidator.diaLibreValidator(jornadaDB);
                        return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                }

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
                    if(jornadaValidator.obtenerHoras(jornadaDB)>=2 && jornadaValidator.obtenerHoras(jornadaDB)<=6) {
//                    jornadaValidator.jornadaExtraValidator(jornadaDB);
                    return jornadaMapper.entityToDTO(jornadaRepository.save(jornadaDB));
                    }
                    else{
                        throw new Exception("No tiene entre 2 y 6hs");
                    }
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

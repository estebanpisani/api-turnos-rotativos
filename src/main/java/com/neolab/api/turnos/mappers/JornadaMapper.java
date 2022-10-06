package com.neolab.api.turnos.mappers;

import com.neolab.api.turnos.dto.JornadaDTO;
import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.entity.Jornada;
import com.neolab.api.turnos.enums.JornadaEnum;
import com.neolab.api.turnos.repository.EmpleadoRepository;
import com.neolab.api.turnos.validators.JornadaValidator;
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
    JornadaValidator jornadaValidator;
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
        //Se crea una nueva entidad de Jornada vacía.
        Jornada jornada = new Jornada();

        //Los campos Tipo y Entrada son necesarios para cualquier tipo de Jornada Laboral
        if(dto.getTipo() == null || dto.getTipo().isEmpty() || dto.getEntrada() == null || dto.getEntrada().isEmpty()){
            throw new Exception("Campos requeridos.");
        }
        //Se agregan los empleados a la jornada laboral solo si fueron ingresados en la solicitud, si existen en la Base de Datos y no superan el máximo permitido (2).
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
        //Se asignan y se validan los datos según lo requiere cada tipo.
        //Día Libre
        if (dto.getTipo().toUpperCase().trim().replace(" ", "_").equals(JornadaEnum.DIA_LIBRE.toString())) {
            jornada.setTipo(JornadaEnum.DIA_LIBRE);
            //Si es Día Libre se le asigna el horario de entrada y salida para que siempre sea de 24hs
            jornada.setEntrada(LocalDateTime.of(LocalDate.parse(dto.getEntrada(), formatterDate), LocalTime.of(0, 0)));
            jornada.setSalida(LocalDateTime.of(LocalDate.parse(dto.getEntrada(), formatterDate), LocalTime.of(23, 59)));
            //Verifica que cada empleado pueda tomarse el día libre.
            if(jornada.getEmpleados().size()>0){
                for (Empleado empleado: jornada.getEmpleados()) {
                    jornadaValidator.diaLibreValidator(jornada, empleado);
                }
            }
            return jornada;
        }

        //En caso de jornada normal, extra o vacaciones, se requiere el dato de la fecha/hora de salida.
        if (dto.getSalida() == null || dto.getSalida().isEmpty()) {
            throw new Exception("La Fecha/hora de salida es requerida.");
        }
        //Se verifica que el formato de la fecha/hora de entrada y salida sean coherentes
        jornadaValidator.horarioValido(jornada);
        //Vacaciones
        if(dto.getTipo().toUpperCase().trim().equals(JornadaEnum.VACACIONES.toString())){
            jornada.setTipo(JornadaEnum.VACACIONES);
            jornada.setEntrada(LocalDateTime.of(LocalDate.parse(dto.getEntrada(), formatterDate), LocalTime.of(0, 0)));
            jornada.setSalida(LocalDateTime.of(LocalDate.parse(dto.getSalida(), formatterDate), LocalTime.of(23, 59)));
            //Verifica si no hay otra jornada en ese horario
            jornadaValidator.horarioDisponible(jornada);
            return jornada;
        }
        //En caso de las jornadas normal y extra necesitamos el dato de la hora de salida.
        LocalDateTime horaEntrada = LocalDateTime.parse(dto.getEntrada(), formatterHour);
        jornada.setEntrada(horaEntrada);
        LocalDateTime horaSalida = LocalDateTime.parse(dto.getSalida(), formatterHour);
        jornada.setSalida(horaSalida);

        //Jornada Normal
        if (dto.getTipo().toUpperCase().trim().equals(JornadaEnum.NORMAL.toString())) {
            jornada.setTipo(JornadaEnum.NORMAL);
            //Se valida que el rango horario sea correcto
            if(jornadaValidator.obtenerHoras(jornada)>=6 && jornadaValidator.obtenerHoras(jornada)<=8) {
                //Verifica si no hay otra jornada en ese horario
                jornadaValidator.horarioDisponible(jornada);
                if(jornada.getEmpleados().size()>0){
                    //Verifica que cada empleado pueda trabajar en ese horario.
                    for (Empleado empleado: jornada.getEmpleados()) {
                        jornadaValidator.jornadaNormalValidator(jornada, empleado);
                    }
                }
            }
            else{
                throw new Exception("No tiene entre 6 y 8hs");
            }
        }
        //Jornada Extra
        if (dto.getTipo().toUpperCase().trim().equals(JornadaEnum.EXTRA.toString())) {
            jornada.setTipo(JornadaEnum.EXTRA);
            //Se valida que el rango horario sea correcto
            if (jornadaValidator.obtenerHoras(jornada) >= 2 && jornadaValidator.obtenerHoras(jornada) <= 6) {
                //Verifica si no hay otra jornada en ese horario
                jornadaValidator.horarioDisponible(jornada);
                if(jornada.getEmpleados().size()>0){
                    //Verifica que cada empleado pueda trabajar en ese horario.
                    for (Empleado empleado: jornada.getEmpleados()) {
                        jornadaValidator.jornadaExtraValidator(jornada, empleado);
                    }
                }
            }
            else {
                throw new Exception("No tiene entre 2 y 6hs");
            }
        }
        //Finalmente, si los datos están correctos, se retorna la nueva entidad Jornada
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

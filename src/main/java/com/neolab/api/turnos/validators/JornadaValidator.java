package com.neolab.api.turnos.validators;

import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.entity.Jornada;
import com.neolab.api.turnos.enums.JornadaEnum;
import com.neolab.api.turnos.repository.EmpleadoRepository;
import com.neolab.api.turnos.repository.JornadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class JornadaValidator {
@Autowired
EmpleadoRepository empleadoRepository;
@Autowired
JornadaRepository jornadaRepository;
    //Validar jornada
    public boolean esJornadaValida(Jornada jornada){
        //Verifica si el ingreso es anterior a la salida y si el usuario referenciado existe en la base de datos.
            return jornada.getHoraEntrada().isBefore(jornada.getHoraSalida()) && empleadoRepository.existsById(jornada.getEmpleadoId());
    }
    public boolean horarioDisponible(Jornada jornada){
        return jornadaRepository.findAll().stream().noneMatch(item ->
                        item.getFecha().equals(jornada.getFecha()) &&
                        (
                            jornada.getHoraEntrada().isEqual(item.getHoraEntrada()) ||
                            (
                            jornada.getHoraEntrada().isAfter(item.getHoraEntrada()) &&
                            jornada.getHoraEntrada().isBefore(item.getHoraSalida()) ||
                            jornada.getHoraSalida().isAfter(item.getHoraEntrada()) &&
                            jornada.getHoraSalida().isBefore(item.getHoraSalida())
                            )
                        )
        );
    }
    public long obtenerHoras(Jornada jornada){
        return jornada.getHoraEntrada().until(jornada.getHoraSalida(), ChronoUnit.HOURS);
    }
    public boolean noSuperaHorasSemanales(Jornada jornada, Empleado empleado) {
        TemporalField weekNumber = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int semanaJornadaNueva = jornada.getFecha().get(weekNumber);
        //Filtrar jornadas por misma semana
        List<Jornada> jornadasMismaSemana = empleado
                .getJornadas()
                .stream()
                .filter(item->
                        item.getFecha().get(weekNumber) == semanaJornadaNueva
                                && (item.getTipo().equals(JornadaEnum.NORMAL) || item.getTipo().equals(JornadaEnum.EXTRA))
                )
                .collect(Collectors.toList());
        //Filtrar cantidad de horas
        if(jornadasMismaSemana.size()>0) {
            long horasSemanales = 0;
            for (Jornada item : jornadasMismaSemana) {
                horasSemanales += this.obtenerHoras(item);
            }
            return horasSemanales+obtenerHoras(jornada)<=48;
        }
        else {
            return true;
        }
    }
    public boolean noSuperaHorasDiarias(Jornada jornada, Empleado empleado) {
        long horasDelDia = 0;
        List<Jornada> jornadasDelDia = empleado.getJornadas()
                .stream()
                .filter(item ->
                        item.getFecha().isEqual(jornada.getFecha()) &&
                                (item.getTipo().equals(JornadaEnum.NORMAL) || item.getTipo().equals(JornadaEnum.EXTRA))
                )
                .collect(Collectors.toList());
        for (Jornada item: jornadasDelDia) {
            horasDelDia += this.obtenerHoras(item);
        }
        return horasDelDia+obtenerHoras(jornada)<=12;
    }
    public boolean tieneDiaLibre(Jornada jornada, Empleado empleado){
        return empleado.getJornadas().stream().anyMatch(item -> item.getFecha().isEqual(jornada.getFecha()) && item.getTipo().equals(JornadaEnum.DIA_LIBRE));
    }

    //Verificar según tipo
    //Normal:
    public boolean jornadaNormalValidator(Jornada jornada){
        //Se verifica si no tiene menos de 6hs ni más de 8hs
        if(obtenerHoras(jornada)>=6 && obtenerHoras(jornada)<=8){
            Empleado empleado = empleadoRepository.findById(jornada.getEmpleadoId()).get();
            // Se verifica si el empleado tiene jornadas cargadas
                if(empleado.getJornadas().size()>0){
                    //Se verifica si no tiene el día libre
                    if(!tieneDiaLibre(jornada, empleado)) {
                    //Se verifica si la jornada no supera las horas semanales.
                        if(noSuperaHorasSemanales(jornada, empleado)) {
                                //Se verifica si supera las horas diarias máximas
                            if (noSuperaHorasDiarias(jornada, empleado)) {
                                System.out.println("Jornada Creada.");
                                return true;
                            } else {
                                System.out.println("La jornada excede las 12hs diarias.");
                                return false;
                            }
                        }
                        else{
                            System.out.println("No puede trabajar más de 48hs semanales.");
                            return false;
                        }
                    }
                    else{
                        System.out.println("El empleado tiene el día libre");
                        return false;
                    }
                }
                else{
                    System.out.println("Jornada Creada");
                    return true;
                }
        }
        else{
            System.out.println("No tiene entre 6 y 8hs");
            return false;
        }
    }
    //Extra
    public boolean jornadaExtraValidator(Jornada jornada) {
        //Se verifica si no tiene menos de 2hs ni más de 6hs
        if (obtenerHoras(jornada) >= 2 && obtenerHoras(jornada) <= 6) {
            Empleado empleado = empleadoRepository.findById(jornada.getEmpleadoId()).get();
            // Se verifica si el empleado tiene jornadas cargadas
            if (empleado.getJornadas().size() > 0) {
                if (!tieneDiaLibre(jornada, empleado)) {
                    if(this.horarioDisponible(jornada)) {
                        if (noSuperaHorasSemanales(jornada, empleado)) {
                            if (noSuperaHorasDiarias(jornada, empleado)) {
                                System.out.println("Jornada Creada.");
                                return true;
                            } else {
                                System.out.println("La jornada excede las 12hs diarias.");
                                return false;
                            }
                        } else {
                            System.out.println("No puede trabajar más de 48hs semanales.");
                            return false;
                        }
                    }else{
                        System.out.println("Ya hay una jornada en ese horario.");
                        return false;
                    }
                } else {
                    System.out.println("El empleado tiene el día libre");
                    return false;
                }
            } else {
                //Si no tiene ninguna jornada, se crea exitosamente.
                System.out.println("Jornada Creada.");
                return true;
            }
        }
        else {
            System.out.println("No tiene entre 2 y 6hs");
            return false;
        }
    }

    public boolean diaLibreValidator(Jornada jornada){
        //Se verifica si el Día Libre tiene 24 hs
        if(obtenerHoras(jornada)==24){
            Empleado empleado = empleadoRepository.findById(jornada.getEmpleadoId()).get();
            // Se verifica si el empleado tiene jornadas cargadas
            if(empleado.getJornadas().size()>0){
                //Se verfica si el empleado tiene jornadas ese día
                if(empleado.getJornadas()
                        .stream()
                        .anyMatch(item -> item.getFecha().isEqual(jornada.getFecha()))){
                    //No permite reemplazar jornadas laborales con dias libres.
                    System.out.println("La fecha ingresada ya tiene una jornada laboral asignada.");
                    return false;
                }
                else{
                    System.out.println("Día Libre válido");
                    return true;
                }
            }
            else{
                System.out.println("Día Libre Válido.");
                return true;
            }
        }
        else{
            System.out.println("El Día Libre debe tener 24hs.");
            return false;
        }
    }

//  Cada empleado no puede trabajar más de 48 horas semanales, ni menos de 30.
//  Las horas de un turno normal pueden variar entre 6 y 8, y las de un turno extra entre 2 y 6.
//  Para cada fecha, un empleado (siempre que no esté de vacaciones o haya pedido día libre)
//  podrá cargar un turno normal, un turno extra o una combinación de ambos que no supere las 12 horas.
//  Por cada turno no puede haber más que 2 empleados.
//  Si un empleado cargó “Dia libre” no podrá trabajar durante las 24 horas correspondientes a ese día.
//  En la semana el empleado podrá tener hasta 2 días libres.
}

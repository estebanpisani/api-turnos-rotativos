package com.neolab.api.turnos.validators;

import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.entity.Jornada;
import com.neolab.api.turnos.enums.JornadaEnum;
import com.neolab.api.turnos.repository.EmpleadoRepository;
import com.neolab.api.turnos.repository.JornadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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
    public boolean horarioValido(Jornada jornada){
        //Verifica si el ingreso es anterior a la salida y si la fecha de ingreso coincide con la fecha.
            return jornada.getHoraEntrada().isBefore(jornada.getHoraSalida())
                    && LocalDate.of(jornada.getHoraEntrada().getYear(),jornada.getHoraEntrada().getMonth(), jornada.getHoraEntrada().getDayOfMonth()).isEqual(jornada.getFecha());
    }
    public boolean usuarioExiste(Jornada jornada){
        //Verifica si el usuario referenciado existe en la base de datos.
        return empleadoRepository.existsById(jornada.getEmpleadoId());
    }
    public boolean horarioDisponible(Jornada jornada){
        if(jornada.getTipo().equals(JornadaEnum.NORMAL)){
            return jornadaRepository.findAll().stream()
                    .noneMatch(item -> item.getTipo().equals(JornadaEnum.NORMAL) &&
                    item.getFecha().equals(jornada.getFecha()));
        }
        return jornadaRepository.findAll().stream()
                .noneMatch(item ->
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
        //  Cada empleado no puede trabajar más de 48 horas semanales, ni menos de 30.
    public boolean noSuperaHorasSemanales(Jornada jornada, Empleado empleado) {
        //Se obtiene el número de la semana del año para comparar.
        TemporalField weekNumber = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int semanaJornadaNueva = jornada.getFecha().get(weekNumber);
        //Se filtran jornadas existentes en la misma semana
        List<Jornada> jornadasMismaSemana = empleado
                .getJornadas()
                .stream()
                .filter(item->
                        item.getFecha().get(weekNumber) == semanaJornadaNueva
                                && (item.getTipo().equals(JornadaEnum.NORMAL) || item.getTipo().equals(JornadaEnum.EXTRA))
                )
                .collect(Collectors.toList());
        //Se otienen las horas semanales
        if(jornadasMismaSemana.size()>0) {
            long horasSemanales = 0;
            for (Jornada item : jornadasMismaSemana) {
                horasSemanales += this.obtenerHoras(item);
            }
            //Se evalúa si la suma entre las horas semanales y la jornada nueva no superan las 48hs
            return horasSemanales+obtenerHoras(jornada)<=48;
        }
        else {
            return true;
        }
    }
    //  Para cada fecha, un empleado (siempre que no esté de vacaciones o haya pedido día libre)
    //  podrá cargar un turno normal, un turno extra o una combinación de ambos que no supere las 12 horas.
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
    //  Si un empleado cargó “Dia libre” no podrá trabajar durante las 24 horas correspondientes a ese día.
    public boolean tieneDiaLibre(Jornada jornada, Empleado empleado){
        return empleado.getJornadas().stream().anyMatch(item -> item.getFecha().isEqual(jornada.getFecha()) && (item.getTipo().equals(JornadaEnum.DIA_LIBRE)));
    }
    public boolean estaDeVacaciones(Jornada jornada, Empleado empleado){
        return empleado.getJornadas().stream()
                .anyMatch(item ->
                        item.getTipo().equals(JornadaEnum.VACACIONES) &&
                                (
                                        jornada.getFecha().isEqual(item.getHoraEntrada().toLocalDate()) ||
                                                (
                                                        jornada.getFecha().isAfter(item.getHoraEntrada().toLocalDate()) &&
                                                        jornada.getFecha().isBefore(item.getHoraSalida().toLocalDate())
                                                ) ||
                                                jornada.getFecha().isEqual(item.getHoraSalida().toLocalDate())
                                )
                );

    }
    //  En la semana el empleado podrá tener hasta 2 días libres.
    public boolean tieneDiasLibresDisponibles(Jornada jornada,Empleado empleado, int maximo){
        //Se obtiene el número de la semana del año para comparar.
        TemporalField weekNumber = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        //Se filtran días libres en la misma semana
        return empleado.getJornadas()
                .stream()
                .filter(item->
                        item.getFecha().get(weekNumber) == jornada.getFecha().get(weekNumber)
                                && item.getTipo().equals(JornadaEnum.DIA_LIBRE
                ))
                .count()<maximo;
    }
    public boolean tieneVacacionesDisponibles(Jornada jornada,Empleado empleado, int maximo){
        //Se filtran los días de vacaciones en el mismo año
        return empleado.getJornadas()
                .stream()
                .filter(item->
                        item.getFecha().getYear() == jornada.getFecha().getYear()
                                && item.getTipo().equals(JornadaEnum.VACACIONES
                        ))
                .count()<maximo;
    }

    //Validar jornada según tipo
    //Normal:
    public boolean jornadaNormalValidator(Jornada jornada){
        //Se verifica si no tiene menos de 6hs ni más de 8hs
        if(obtenerHoras(jornada)>=6 && obtenerHoras(jornada)<=8){
            Empleado empleado = empleadoRepository.findById(jornada.getEmpleadoId()).get();
            // Se verifica si el empleado tiene jornadas cargadas
                if(empleado.getJornadas().size()>0){
                    if(!estaDeVacaciones(jornada, empleado)) {
                        //Se verifica si no tiene el día libre
                        if (!tieneDiaLibre(jornada, empleado) && !estaDeVacaciones(jornada, empleado)) {
                            //Se verifica si el día está disponible
                            if (horarioDisponible(jornada)) {
                                //Se verifica si la jornada no supera las horas semanales.
                                if (noSuperaHorasSemanales(jornada, empleado)) {
                                    //Se verifica si supera las horas diarias máximas
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
                            } else {
                                System.out.println("El horario ya está ocupado con otra jornada");
                                return false;
                            }
                        } else {
                            System.out.println("El empleado tiene el día libre");
                            return false;
                        }
                    }
                    else {
                        System.out.println("El empleado está de vacaciones en esa fecha.");
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
                if(!estaDeVacaciones(jornada, empleado)) {
                    if (!tieneDiaLibre(jornada, empleado)) {
                        if (this.horarioDisponible(jornada)) {
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
                        } else {
                            System.out.println("Ya hay una jornada en ese horario.");
                            return false;
                        }
                    } else {
                        System.out.println("El empleado tiene el día libre");
                        return false;
                    }
                }
                else{
                    System.out.println("El empleado está de vacaciones en esa fecha.");
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
    //Día Libre
    public boolean diaLibreValidator(Jornada jornada){
        Empleado empleado = empleadoRepository.findById(jornada.getEmpleadoId()).get();
        // Se verifica si el empleado tiene jornadas cargadas
        if(empleado.getJornadas().size()>0){
            //Se verifica si no está de vacaciones
            if(!estaDeVacaciones(jornada, empleado)) {
                //Se verificar si tiene días libres disponibles esa semana
                if (tieneDiasLibresDisponibles(jornada, empleado, 2)) {
                    //Se verifica si el empleado tiene jornadas ese día
                    if (empleado.getJornadas()
                            .stream()
                            .anyMatch(item -> item.getFecha().isEqual(jornada.getFecha()))) {
                        //No permite reemplazar jornadas laborales con dias libres.
                        System.out.println("La fecha ingresada ya tiene una jornada laboral asignada.");
                        return false;
                    } else {
                        System.out.println("Día Libre válido");
                        return true;
                    }
                } else {
                    System.out.println("No tiene días libres disponibles para esa semana.");
                    return false;
                }
            }
            else {
                System.out.println("El empleado está de vacaciones en esa fecha.");
                return false;
            }
        }
        else{
            System.out.println("Día Libre Válido.");
            return true;
        }
    }
    //TODO Vacaciones
    public boolean vacacionesValidator(Jornada jornada){
            return true;
    }

//TODO
//  Por cada turno no puede haber más que 2 empleados.
}

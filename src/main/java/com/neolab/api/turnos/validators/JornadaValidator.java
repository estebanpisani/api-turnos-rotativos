package com.neolab.api.turnos.validators;

import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.entity.Jornada;
import com.neolab.api.turnos.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JornadaValidator {
@Autowired
EmpleadoRepository empleadoRepository;
    //Verificar según tipo

    //Normal:

    public boolean jornadaNormalValidator(Jornada jornada){
        //Verificar fecha/hora de entrada es anterior a hora de salida
        if(jornada.getHoraEntrada().isBefore(jornada.getHoraSalida())){
            System.out.println("Hora de entrada es anterior a hora de salida");
            //Obtengo valores de hora en formato LocalTime
            LocalTime horaEntrada = LocalTime.of(jornada.getHoraEntrada().getHour(), jornada.getHoraEntrada().getMinute(), jornada.getHoraEntrada().getSecond());
            LocalTime horaSalida = LocalTime.of(jornada.getHoraSalida().getHour(), jornada.getHoraSalida().getMinute(), jornada.getHoraSalida().getSecond());
            //Obtengo cantidad de horas de la jornada
            long horasJornada = ChronoUnit.HOURS.between(horaEntrada, horaSalida);
            //Verificar si no tiene menos de 6hs ni más de 8hs
            if(horasJornada>=6&&horasJornada<=8){
                System.out.println("Tiene entre 6hs y 8hs ("+horasJornada+"hs).");
                //Se busca el empleado en la base de datos por su id
                Optional<Empleado> opt = empleadoRepository.findById(jornada.getEmpleadoId());
                if(opt.isPresent()){
                    Empleado empleado = opt.get();
                // Verificar si el empleado tiene jornadas cargadas
                    if(empleado.getJornadas().size()>0){
                    //Si tiene jornadas, verificar que sea la única jornada del día
                        if (empleado.getJornadas().stream().filter(item -> item.getFecha().isEqual(jornada.getFecha())).count()<1){
                            System.out.println("Es la única jornada normal del día del empleado.");
                            //Obtener número de semana del año
                            TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
                            int semanaJornadaNueva = jornada.getFecha().get(woy);
                            //Filtrar jornadas por misma semana
                            List<Jornada> jornadasMismaSemana = empleado.getJornadas().stream().filter(item-> item.getFecha().get(woy) == semanaJornadaNueva).collect(Collectors.toList());
                            //Filtrar cantidad de horas
                            if(jornadasMismaSemana !=null && jornadasMismaSemana.size()>0){
                                long horasSemanales = 0;
                                for (Jornada item:jornadasMismaSemana) {
                                    horasSemanales += ChronoUnit.HOURS.between(item.getHoraEntrada(), item.getHoraSalida());
                                }
                            //Verificar que la sumatoria de las horas de la jornada y el resto de la semana no supere las 48hs
                                if(horasSemanales+horasJornada<=48){
                                    System.out.println("Jornada Creada");
                                    return true;
                                }else{
                                    System.out.println("No puede trabajar más de 48hs semanales.");
                                    return false;
                                }
                            }else{
                                System.out.println("Es la única jornada normal de la misma semana.");
                                System.out.println("Jornada Creada");
                                return true;
                            }
                        }
                        else{
                            System.out.println("El empleado ya tiene una jornada normal este día");
                            return false;
                        }
                    }
                    else{
                        System.out.println("El empleado aún no tiene ninguna jornada.");
                        //Si no tiene ninguna jornada, se crea exitosamente.
                        return true;
                    }
                }else{
                    System.out.println("No existe un empleado con ese id");
                    return false;
                }
            }
            else{
                System.out.println("No tiene entre 6 y 8hs: "+ChronoUnit.HOURS.between(horaEntrada, horaSalida));
                return false;
            }
        }
        else{
            System.out.println("Hora de Entrada es posterior a hora de salida");
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

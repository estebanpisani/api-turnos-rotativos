package com.neolab.api.turnos.validators;

import com.neolab.api.turnos.entity.*;
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
JornadaRepository jornadaRepository;
    //Validar jornada
    public void horarioValido(Jornada jornada) throws Exception{
        //Verifica si el ingreso es anterior a la salida y si la fecha de ingreso coincide con la fecha.
        if(jornada.getEntrada().isAfter(jornada.getSalida())
                || !LocalDate.of(jornada.getEntrada().getYear(),jornada.getEntrada().getMonth(), jornada.getEntrada().getDayOfMonth()).isEqual(jornada.getEntrada().toLocalDate())){
            throw new Exception("El horario ingresado no es válido");
        }
    }
    public void horarioDisponible(Jornada jornada, Empleado empleado) throws Exception{
        //Verifica que no haya otra jornada distinta que ocupe ese rango horario.
        //En el caso de la jornada normal, no puede haber dos jornadas normales el mismo día para el mismo Empleado.
        if(jornada.getTipo().getNombre().equalsIgnoreCase("normal") && jornadaRepository.findAll().stream()
                .anyMatch(item ->
                        item.getEmpleados().stream().anyMatch(e -> e.getId().equals(empleado.getId())) &&
                        item.getTipo().getNombre().equalsIgnoreCase("normal") &&
                        item.getEntrada().toLocalDate().equals(jornada.getEntrada().toLocalDate()) &&
                        !item.getId().equals(jornada.getId()))){
            throw new Exception("El empleado "+empleado.getNombre()+" "+empleado.getApellido()+" ya tiene una jornada en ese horario.");
        }
        else if (!jornada.getTipo().getNombre().equalsIgnoreCase("dia libre") && !jornada.getTipo().getNombre().equalsIgnoreCase("vacaciones") && jornadaRepository.findAll().stream()
                .anyMatch(item ->
                        !item.getId().equals(jornada.getId()) &&
                                !item.getTipo().getNombre().equalsIgnoreCase("dia libre") &&
                                !item.getTipo().getNombre().equalsIgnoreCase("vacaciones") &&
                        item.getEntrada().toLocalDate().equals(jornada.getEntrada().toLocalDate()) &&
                        (
                            jornada.getEntrada().isEqual(item.getEntrada()) ||
                            (
                            jornada.getEntrada().isAfter(item.getEntrada()) &&
                            jornada.getEntrada().isBefore(item.getSalida()) ||
                            jornada.getSalida().isAfter(item.getEntrada()) &&
                            jornada.getSalida().isBefore(item.getSalida())
                            )
                        )
        )){
            throw new Exception("Ya hay una jornada en ese horario.");
        }
    }
    public long obtenerHoras(Jornada jornada){
        return jornada.getEntrada().until(jornada.getSalida(), ChronoUnit.HOURS);
    }
    public void noSuperaHorasSemanales(Jornada jornada, Empleado empleado) throws Exception {
    // Cada empleado no puede trabajar más de 48 horas semanales, ni menos de 30.
        //Se obtiene el número de la semana del año para comparar.
        TemporalField weekNumber = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int semanaJornadaNueva = jornada.getEntrada().toLocalDate().get(weekNumber);
        //Se filtran jornadas normales/extras existentes en la misma semana
        List<Jornada> jornadasMismaSemana = empleado
                .getJornadas()
                .stream()
                .filter(item->
                        item.getEntrada().toLocalDate().get(weekNumber) == semanaJornadaNueva && !item.getId().equals(jornada.getId())
                                && (!item.getTipo().getNombre().equalsIgnoreCase("dia libre") && !item.getTipo().getNombre().equalsIgnoreCase("vacaciones"))
                )
                .collect(Collectors.toList());
        //Se obtienen las horas semanales
        if(jornadasMismaSemana.size()>0) {
            long horasSemanales = 0;
            for (Jornada item : jornadasMismaSemana) {
                horasSemanales += this.obtenerHoras(item);
            }
            //Se evalúa si la suma entre las horas semanales y la jornada nueva no superan las 48hs
            if(horasSemanales+obtenerHoras(jornada)>48){
                throw new Exception("La jornada excede las 48hs semanales del empleado "+empleado.getNombre()+" "+empleado.getApellido());
            }
        }
    }
    public void noSuperaHorasDiarias(Jornada jornada, Empleado empleado) throws Exception {
        //  Para cada fecha, un empleado (siempre que no esté de vacaciones o haya pedido día libre)
        //  podrá cargar un turno normal, un turno extra o una combinación de ambos que no supere las 12 horas.
        long horasDelDia = 0;
        List<Jornada> jornadasDelDia = empleado.getJornadas()
                .stream()
                .filter(item ->
                        item.getEntrada().toLocalDate().isEqual(jornada.getEntrada().toLocalDate()) && !item.getId().equals(jornada.getId()) &&
                                (!item.getTipo().getNombre().equalsIgnoreCase("dia libre") && !item.getTipo().getNombre().equalsIgnoreCase("vacaciones"))
                )
                .collect(Collectors.toList());
        for (Jornada item: jornadasDelDia) {
            horasDelDia += this.obtenerHoras(item);
        }
        if (horasDelDia+obtenerHoras(jornada)>12) {
            throw new Exception("La jornada excede las 12hs diarias del empleado "+empleado.getNombre()+" "+empleado.getApellido());
        }
    }
    public void rangoHorarioCorrecto(Jornada jornada) throws Exception{
        if(obtenerHoras(jornada)<jornada.getTipo().getHorasDiariasMin() || obtenerHoras(jornada)>jornada.getTipo().getHorasDiariasMax()){
            throw new Exception("Rango horario incorrecto. Debe tener entre "+jornada.getTipo().getHorasDiariasMin()+" y "+jornada.getTipo().getHorasDiariasMax()+"hs.");
        }
    }
    public void noTieneDiaLibre(Jornada jornada, Empleado empleado) throws Exception{
    //  Si un empleado cargó “Dia libre” no podrá trabajar durante las 24 horas correspondientes a ese día.
        if (empleado.getJornadas().stream().anyMatch(item -> item.getEntrada().toLocalDate().isEqual(jornada.getEntrada().toLocalDate()) &&
                (item.getTipo().getNombre().equalsIgnoreCase("dia libre")))){
            throw new Exception("El empleado "+empleado.getNombre()+" "+empleado.getApellido()+" tiene el día libre");
        }
    }
    public void noEstaDeVacaciones(Jornada jornada, Empleado empleado) throws Exception{
        if (empleado.getJornadas().stream()
                .anyMatch(item ->
                        item.getTipo().getNombre().equalsIgnoreCase("vacaciones") &&
                                (
                                        jornada.getEntrada().toLocalDate().isEqual(item.getEntrada().toLocalDate()) ||
                                                (
                                                        jornada.getEntrada().toLocalDate().isAfter(item.getEntrada().toLocalDate()) &&
                                                        jornada.getEntrada().toLocalDate().isBefore(item.getSalida().toLocalDate())
                                                ) ||
                                                jornada.getEntrada().toLocalDate().isEqual(item.getSalida().toLocalDate())
                                )
                )){
            throw new Exception("El empleado "+empleado.getNombre()+" "+empleado.getApellido()+" está de vacaciones en esa fecha.");
        }
    }
    public void tieneDiasLibresDisponibles(Jornada jornada,Empleado empleado, int maximo) throws Exception{
    //  En la semana el empleado podrá tener hasta 2 días libres.
        //Se obtiene el número de la semana del año para comparar.
        TemporalField weekNumber = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        //Se filtran días libres en la misma semana
        if (empleado.getJornadas()
                .stream()
                .filter(item->
                        item.getEntrada().toLocalDate().get(weekNumber) == jornada.getEntrada().toLocalDate().get(weekNumber)
                                && item.getTipo().getNombre().equalsIgnoreCase("dia libre") && !item.getId().equals(jornada.getId())
                )
                .count()==maximo){
            throw new Exception("El empleado "+empleado.getNombre()+" "+empleado.getApellido()+" no tiene días libres disponibles para esa semana.");
        }
    }

    //Validaciones según tipo
    //Laboral (Normal/Extra):
    public void jornadaLaboralValidator(Jornada jornada, Empleado empleado) throws Exception{
            // Se verifica si el empleado tiene jornadas cargadas
            if (empleado.getJornadas().size() > 0) {
                noEstaDeVacaciones(jornada, empleado);
                //Se verifica si no tiene el día libre
                noTieneDiaLibre(jornada, empleado);
                //Se verifica si el día está disponible
                horarioDisponible(jornada, empleado);
                //Se verifica si la jornada no supera las horas semanales.
                noSuperaHorasSemanales(jornada, empleado);
                //Se verifica si supera las horas diarias máximas
                noSuperaHorasDiarias(jornada, empleado);
            }
    }
    //Día Libre
    public void diaLibreValidator(Jornada jornada, Empleado empleado) throws Exception{
        // Se verifica si el empleado tiene jornadas cargadas
        if(empleado.getJornadas().size()>0){
            //Se verifica si no está de vacaciones
            noEstaDeVacaciones(jornada, empleado);
                //Se verifica si tiene días libres disponibles esa semana
                tieneDiasLibresDisponibles(jornada, empleado, 2);
                    //Se verifica si el empleado tiene jornadas ese día
                    if (empleado.getJornadas()
                            .stream()
                            .anyMatch(item -> item.getEntrada().toLocalDate().isEqual(jornada.getEntrada().toLocalDate()) && !item.getId().equals(jornada.getId()))) {
                        //No permite reemplazar jornadas laborales con dias libres.
                        throw new Exception("La fecha ingresada ya tiene una jornada laboral asignada.");
                    }
        }
    }
    //Vacaciones
    public void vacacionesValidator(Jornada jornada, Empleado empleado) throws Exception{
        // Se verifica si el empleado tiene jornadas cargadas
        if(empleado.getJornadas().size()>0){
            //Se verifica si ya no está de vacaciones
            noEstaDeVacaciones(jornada, empleado);
        }
    }
}

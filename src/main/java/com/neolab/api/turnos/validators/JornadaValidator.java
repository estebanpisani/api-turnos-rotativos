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
    public void horarioValido(Jornada jornada) throws Exception{
        //Verifica si el ingreso es anterior a la salida y si la fecha de ingreso coincide con la fecha.
        if(jornada.getEntrada().isAfter(jornada.getSalida())
                || !LocalDate.of(jornada.getEntrada().getYear(),jornada.getEntrada().getMonth(), jornada.getEntrada().getDayOfMonth()).isEqual(jornada.getEntrada().toLocalDate())){
            throw new Exception("El horario ingresado no es válido");
        }
    }
    public void horarioDisponible(Jornada jornada) throws Exception{
        //Verifica que no haya otra jornada distinta que ocupe ese rango horario.
        //En el caso de la jornada normal, no puede haber dos jornadas normales el mismo día.
        if(jornada.getTipo().equals(JornadaEnum.NORMAL) && jornadaRepository.findAll().stream()
                .anyMatch(item ->
                        item.getTipo().equals(JornadaEnum.NORMAL) &&
                        item.getEntrada().toLocalDate().equals(jornada.getEntrada().toLocalDate()) &&
                        !item.getId().equals(jornada.getId()))){
            throw new Exception("Ya hay una jornada en ese horario.");
        }
        if (jornadaRepository.findAll().stream()
                .anyMatch(item ->
                        !item.getId().equals(jornada.getId()) &&
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
        //  Cada empleado no puede trabajar más de 48 horas semanales, ni menos de 30.
    public void noSuperaHorasSemanales(Jornada jornada, Empleado empleado) throws Exception {
        //Se obtiene el número de la semana del año para comparar.
        TemporalField weekNumber = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int semanaJornadaNueva = jornada.getEntrada().toLocalDate().get(weekNumber);
        //Se filtran jornadas existentes en la misma semana
        List<Jornada> jornadasMismaSemana = empleado
                .getJornadas()
                .stream()
                .filter(item->
                        item.getEntrada().toLocalDate().get(weekNumber) == semanaJornadaNueva && !item.getId().equals(jornada.getId())
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
            if(horasSemanales+obtenerHoras(jornada)>48){
                throw new Exception("No puede trabajar más de 48hs semanales.");
            }
        }
    }
    //  Para cada fecha, un empleado (siempre que no esté de vacaciones o haya pedido día libre)
    //  podrá cargar un turno normal, un turno extra o una combinación de ambos que no supere las 12 horas.
    public void noSuperaHorasDiarias(Jornada jornada, Empleado empleado) throws Exception {
        long horasDelDia = 0;
        List<Jornada> jornadasDelDia = empleado.getJornadas()
                .stream()
                .filter(item ->
                        item.getEntrada().toLocalDate().isEqual(jornada.getEntrada().toLocalDate()) && !item.getId().equals(jornada.getId()) &&
                                (item.getTipo().equals(JornadaEnum.NORMAL) || item.getTipo().equals(JornadaEnum.EXTRA))
                )
                .collect(Collectors.toList());
        for (Jornada item: jornadasDelDia) {
            horasDelDia += this.obtenerHoras(item);
        }
        if (horasDelDia+obtenerHoras(jornada)>12) {
            throw new Exception("La jornada excede las 12hs diarias.");
        }
    }
    //  Si un empleado cargó “Dia libre” no podrá trabajar durante las 24 horas correspondientes a ese día.
    public void noTieneDiaLibre(Jornada jornada, Empleado empleado) throws Exception{
        if (empleado.getJornadas().stream().anyMatch(item -> item.getEntrada().toLocalDate().isEqual(jornada.getEntrada().toLocalDate()) && (item.getTipo().equals(JornadaEnum.DIA_LIBRE)))){
            throw new Exception("El empleado tiene el día libre");
        }
    }
    public void noEstaDeVacaciones(Jornada jornada, Empleado empleado) throws Exception{
        if (empleado.getJornadas().stream()
                .anyMatch(item ->
                        item.getTipo().equals(JornadaEnum.VACACIONES) &&
                                (
                                        jornada.getEntrada().toLocalDate().isEqual(item.getEntrada().toLocalDate()) ||
                                                (
                                                        jornada.getEntrada().toLocalDate().isAfter(item.getEntrada().toLocalDate()) &&
                                                        jornada.getEntrada().toLocalDate().isBefore(item.getSalida().toLocalDate())
                                                ) ||
                                                jornada.getEntrada().toLocalDate().isEqual(item.getSalida().toLocalDate())
                                )
                )){
            throw new Exception("El empleado está de vacaciones en esa fecha.");
        }
    }
    //  En la semana el empleado podrá tener hasta 2 días libres.
    public void tieneDiasLibresDisponibles(Jornada jornada,Empleado empleado, int maximo) throws Exception{
        //Se obtiene el número de la semana del año para comparar.
        TemporalField weekNumber = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        //Se filtran días libres en la misma semana
        if (empleado.getJornadas()
                .stream()
                .filter(item->
                        item.getEntrada().toLocalDate().get(weekNumber) == jornada.getEntrada().toLocalDate().get(weekNumber)
                                && item.getTipo().equals(JornadaEnum.DIA_LIBRE) && !item.getId().equals(jornada.getId())
                )
                .count()==maximo){
            throw new Exception("No tiene días libres disponibles para esa semana.");
        }
    }
    //TODO
    //  Por cada turno
    //  no puede haber más que 2 empleados.
    //Validaciones según tipo
    //Normal:
    public void jornadaNormalValidator(Jornada jornada, Empleado empleado) throws Exception {
        // Se verifica si el empleado tiene jornadas cargadas
        if (empleado.getJornadas().size() > 0 && empleado.getJornadas().stream().noneMatch(item -> item == null)) {
            try {
                noEstaDeVacaciones(jornada, empleado);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
            try {
                //Se verifica si no tiene el día libre
                noTieneDiaLibre(jornada, empleado);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
            try {
                //Se verifica si el día está disponible
                horarioDisponible(jornada);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
            try {
                //Se verifica si la jornada no supera las horas semanales.
                noSuperaHorasSemanales(jornada, empleado);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
            try {
                //Se verifica si supera las horas diarias máximas
                noSuperaHorasDiarias(jornada, empleado);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }
    //Extra
    public void jornadaExtraValidator(Jornada jornada, Empleado empleado) throws Exception {
        // Se verifica si el empleado tiene jornadas cargadas
        if (empleado.getJornadas().size() > 0) {
            noEstaDeVacaciones(jornada, empleado);
            noTieneDiaLibre(jornada, empleado);
            noSuperaHorasSemanales(jornada, empleado);
            noSuperaHorasDiarias(jornada, empleado);
        }
    }
    //Día Libre
    public void diaLibreValidator(Jornada jornada, Empleado empleado) throws Exception{
//        Empleado empleado = empleadoRepository.findById(jornada.getEmpleadoId()).get();
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
    //TODO vacacionesValidator
    //    public boolean vacacionesValidator(Jornada jornada){
    //            return true;
    //    }


}

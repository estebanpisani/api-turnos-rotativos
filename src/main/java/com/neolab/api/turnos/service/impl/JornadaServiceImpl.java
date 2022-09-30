package com.neolab.api.turnos.service.impl;

import com.neolab.api.turnos.entity.Jornada;
import com.neolab.api.turnos.repository.JornadaRepository;
import com.neolab.api.turnos.service.JornadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class JornadaServiceImpl implements JornadaService {

    @Autowired
    JornadaRepository jornadaRepository;

    @Override
    public Jornada createJornada(Jornada jornada) {
        Jornada response = jornadaRepository.save(jornada);
        return response;
    }

    @Override
    public Jornada updateJornada(Long id, Jornada jornada) {
        Optional<Jornada> opt = jornadaRepository.findByEmpleadoId(jornada.getEmpleadoId());
        if(opt.isPresent()){
            Jornada jornadaUpd = opt.get();
            jornadaUpd.setFecha(jornada.getFecha());
            jornadaUpd.setHoraEntrada(jornada.getHoraEntrada());
            jornadaUpd.setHoraSalida(jornada.getHoraSalida());
            return jornadaRepository.save(jornadaUpd);
        }
        return null;
    }

    @Override
    public List<Jornada> getJornadasByEmpleado(Long id) {

        return null;
    }

    @Override
    public Jornada getJornadaById(Long id) {
        return null;
    }

    @Override
    public void deleteJornada(Long id) {

    }
}

package com.neolab.api.turnos.service.impl;

import com.neolab.api.turnos.dto.JornadaDTO;
import com.neolab.api.turnos.entity.Jornada;
import com.neolab.api.turnos.entity.JornadaNormal;
import com.neolab.api.turnos.enums.JornadaEnum;
import com.neolab.api.turnos.service.JornadaService;

import java.util.List;

public class JornadaServiceImpl implements JornadaService {

    @Override
    public JornadaDTO createJornada(JornadaDTO dto) {
        if (dto.getType().equals(JornadaEnum.NORMAL)) {
            JornadaNormal jornadaNormal = new JornadaNormal();
            jornadaNormal.setTimeIn(dto.getDateTimeIn());
            jornadaNormal.setTimeOut(dto.getDateTimeOut());
        } else if (dto.getType().equals(JornadaEnum.EXTRA))

        return null;
    }

    @Override
    public JornadaDTO updateJornada(Long id, JornadaDTO dto) {
        return null;
    }

    @Override
    public List<JornadaDTO> getJornadas() {
        return null;
    }

    @Override
    public JornadaDTO getJornadaById(Long id) {
        return null;
    }

    @Override
    public void deleteJornada(Long id) {

    }
}

package com.neolab.api.turnos.service.impl;

import com.neolab.api.turnos.entity.Tipo;
import com.neolab.api.turnos.repository.TipoRepository;
import com.neolab.api.turnos.service.TipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TipoServiceImpl implements TipoService {
    @Autowired
    TipoRepository tipoRepository;
    @Override
    public Tipo createTipo(Tipo tipo) throws Exception {
        Tipo newTipo = new Tipo();
        newTipo.setNombre(tipo.getNombre());
        newTipo.setHorasDiariasMin(tipo.getHorasDiariasMin());
        newTipo.setHorasDiariasMax(tipo.getHorasDiariasMax());
        newTipo.setHorasSemanalesMax(tipo.getHorasSemanalesMax());
        return tipoRepository.save(newTipo);
    }

    @Override
    public Tipo updateTipo(Long id, Tipo tipo) throws Exception {
        Tipo tipoDB = tipoRepository.findById(id).orElse(null);
        if(tipoDB != null) {
            tipoDB.setNombre(tipo.getNombre());
            tipoDB.setHorasDiariasMin(tipo.getHorasDiariasMin());
            tipoDB.setHorasDiariasMax(tipo.getHorasDiariasMax());
            tipoDB.setHorasSemanalesMax(tipo.getHorasSemanalesMax());
            return tipoRepository.save(tipoDB);
        }
        return null;
    }

    @Override
    public List<Tipo> getAllTipos() {
        return tipoRepository.findAll();
    }

    @Override
    public Tipo getTipoById(Long id) {
        return tipoRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean deleteTipo(Long id) {
        if(tipoRepository.existsById(id)) {
            tipoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

package com.neolab.api.turnos.service;
import com.neolab.api.turnos.entity.Tipo;

import java.util.List;

public interface TipoService {
    Tipo createTipo(Tipo tipo) throws Exception;
    Tipo updateTipo(Long id, Tipo tipo) throws Exception;
    List<Tipo> getAllTipos();;
    Tipo getTipoById(Long id);
    Boolean deleteTipo(Long id);
}

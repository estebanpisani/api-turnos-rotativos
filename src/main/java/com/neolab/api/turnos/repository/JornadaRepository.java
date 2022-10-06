package com.neolab.api.turnos.repository;

import com.neolab.api.turnos.entity.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface JornadaRepository extends JpaRepository<Jornada, Long> {
    List<Jornada> findJornadasByEmpleadosId(Long empleadoId);
}

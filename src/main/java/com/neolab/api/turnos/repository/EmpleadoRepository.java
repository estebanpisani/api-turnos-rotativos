package com.neolab.api.turnos.repository;

import com.neolab.api.turnos.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

}

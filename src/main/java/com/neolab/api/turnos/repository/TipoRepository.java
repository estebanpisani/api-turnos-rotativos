package com.neolab.api.turnos.repository;
import com.neolab.api.turnos.entity.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {
    Optional<Tipo> findByNombre( String nombre);

}

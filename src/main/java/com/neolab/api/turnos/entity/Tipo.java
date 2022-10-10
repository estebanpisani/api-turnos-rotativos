package com.neolab.api.turnos.entity;

import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;

@Data
@Table(name = "tipos")
public class Tipo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tipo_id")
    protected Long id;
    protected String nombre;
    protected Integer horasDiariasMax;
    protected Integer horasDiariasMin;
    protected Integer horasSemanalesMax;
}

package com.neolab.api.turnos.entity;

import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "tipos")
public class Tipo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipo_id")
    protected Long id;
    protected String nombre;
    protected Integer horasDiariasMax;
    protected Integer horasDiariasMin;
    protected Integer horasSemanalesMax;
}

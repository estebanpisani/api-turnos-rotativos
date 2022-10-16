package com.neolab.api.turnos.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Data
@Table(name = "tipos")
public class Tipo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipo_id")
    protected Long id;
    @NotNull
    protected String nombre;
    @NotNull
    protected Integer horasDiariasMax;
    @NotNull
    protected Integer horasDiariasMin;
    protected Integer horasSemanalesMax;
}

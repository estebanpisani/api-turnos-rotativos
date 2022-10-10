package com.neolab.api.turnos.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "tipos")
public class Tipo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tipo_id")
    private Long id;
    private String nombre;
    private Integer horasDiariasMax;
    private Integer horasDiariasMin;
    private Integer horasSemanalesMax;
}

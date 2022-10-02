package com.neolab.api.turnos.controllers;

import com.neolab.api.turnos.dto.JornadaDTO;
import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.entity.Jornada;
import com.neolab.api.turnos.service.impl.JornadaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendario")
public class JornadaController {
    @Autowired
    JornadaServiceImpl jornadaService;

    @GetMapping()
    public ResponseEntity<?> obtenerJornadas(){
        List<JornadaDTO> dtos = jornadaService.getAllJornadas();
        if(dtos!=null){
            return new ResponseEntity<>(dtos, HttpStatus.OK);
        }
        return new ResponseEntity<>("No hay jornadas laborales disponibles", HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerJornadasPorEmpleado(@PathVariable Long id){
        List<JornadaDTO> dtos = jornadaService.getJornadasByEmpleado(id);
        if(dtos!=null){
            return new ResponseEntity<>(dtos, HttpStatus.OK);
        }
        return new ResponseEntity<>("No hay jornadas laborales disponibles", HttpStatus.BAD_REQUEST);
    }

    @PostMapping()
    public ResponseEntity<?> createJornada(@RequestBody JornadaDTO dto){
        JornadaDTO response = jornadaService.createJornada(dto);
        if(response != null){
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear la jornada laboral", HttpStatus.BAD_REQUEST);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateJornada(@PathVariable Long id, @RequestBody JornadaDTO dto){
        return new ResponseEntity<>("Error al editar la jornada laboral", HttpStatus.BAD_REQUEST);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJornada(@PathVariable Long id){
        return new ResponseEntity<>("Error al eliminar la jornada laboral", HttpStatus.INTERNAL_SERVER_ERROR);
    }




}

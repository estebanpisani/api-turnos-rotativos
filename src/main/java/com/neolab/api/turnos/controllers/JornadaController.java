package com.neolab.api.turnos.controllers;

import com.neolab.api.turnos.dto.JornadaDTO;
import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.entity.Jornada;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendario")
public class JornadaController {

    @GetMapping("/${id}")
    public ResponseEntity<?> obtenerJornadasPorEmpleado(@PathVariable Long id){

        return new ResponseEntity<>("No hay jornadas laborales disponibles", HttpStatus.BAD_REQUEST);
    }

    @PostMapping()
    public ResponseEntity<?> createJornada(@RequestBody JornadaDTO dto){
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

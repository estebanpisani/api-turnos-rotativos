package com.neolab.api.turnos.controllers;

import com.neolab.api.turnos.dto.JornadaDTO;
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

    //Endpoint para obtener todas las jornadas de la base de datos.
    @GetMapping()
    public ResponseEntity<?> obtenerJornadas(){
        List<JornadaDTO> dtos = jornadaService.getAllJornadas();
        if(dtos!=null){
            return new ResponseEntity<>(dtos, HttpStatus.OK);
        }
        return new ResponseEntity<>("No hay jornadas laborales disponibles", HttpStatus.BAD_REQUEST);
    }
    //Endpoint para obtener todas las jornadas del mismo empleado.
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerJornadasPorEmpleado(@PathVariable Long id, @RequestParam(required = false) String tipo){
        List<JornadaDTO> dtos = jornadaService.getJornadasByEmpleado(id, tipo);
        if(dtos!=null){
            return new ResponseEntity<>(dtos, HttpStatus.OK);
        }
        return new ResponseEntity<>("No hay jornadas laborales disponibles", HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/{id}/empleado")
    public ResponseEntity<?> obtenerEmpleadoPorJornada(@PathVariable Long id){
        String nombre = jornadaService.getEmpleadoByJornadaId(id);
        if(nombre!=null){
            return new ResponseEntity<>(nombre, HttpStatus.OK);
        }
        return new ResponseEntity<>("No hay empleado en esa jornada", HttpStatus.BAD_REQUEST);
    }
    //Endpoint para crear una nueva jornada. Recibe un DTO en el body de la request.
    @PostMapping()
    public ResponseEntity<?> createJornada(@RequestBody JornadaDTO dto){
        JornadaDTO response = jornadaService.createJornada(dto);
        if(response != null){
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear la jornada laboral", HttpStatus.BAD_REQUEST);
    }
    //Endpoint para modificar una jornada laboral existente en la base de datos. Recibe los datos a modificar en el body y el id de referencia como path variable.
    @PutMapping("/{id}")
    public ResponseEntity<?> updateJornada(@PathVariable Long id, @RequestBody JornadaDTO dto){
        return new ResponseEntity<>("Error al editar la jornada laboral", HttpStatus.BAD_REQUEST);
    }
    //Endpoint para eliminar una jornada laboral de la base de datos. Recibe el id de referencia como path variable.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJornada(@PathVariable Long id){
        return new ResponseEntity<>("Error al eliminar la jornada laboral", HttpStatus.INTERNAL_SERVER_ERROR);
    }




}

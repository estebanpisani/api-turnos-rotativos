package com.neolab.api.turnos.controllers;

import com.neolab.api.turnos.dto.JornadaDTO;
import com.neolab.api.turnos.entity.Tipo;
import com.neolab.api.turnos.service.impl.TipoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos")
public class TipoController {
    @Autowired
    TipoServiceImpl tipoService;
    //Endpoint para obtener todos los tipos de jornadas en la base de datos.
    @GetMapping()
    public ResponseEntity<?> getAllTipos(){
        List<Tipo> tipos = tipoService.getAllTipos();
        if(tipos!=null){
            return new ResponseEntity<>(tipos, HttpStatus.OK);
        }
        return new ResponseEntity<>("No hay tipos de jornadas laborales disponibles", HttpStatus.BAD_REQUEST);
    }
    //Endpoint para crear un nuevo tipo de jornada laboral.
    @PostMapping()
    public ResponseEntity<?> createJornada(@RequestBody Tipo tipo) throws Exception{
        try{
            Tipo response = tipoService.createTipo(tipo);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJornada(@PathVariable Long id){
        if(tipoService.deleteTipo(id)){
            return new ResponseEntity<>("Tipo de jornada laboral eliminada.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al eliminar el tipo de jornada laboral", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

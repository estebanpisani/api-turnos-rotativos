package com.neolab.api.turnos.controllers;

import com.neolab.api.turnos.entity.Empleado;
import com.neolab.api.turnos.service.impl.EmpleadoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {
    @Autowired
    EmpleadoServiceImpl empleadoService;

    @GetMapping()
    public ResponseEntity<List<Empleado>> getEmpleados(){
        List<Empleado> empleados = empleadoService.getEmpleados();
        if(empleados != null) {
            return new ResponseEntity<>(empleados, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Empleado> getEmpleados(@PathVariable Long id){
        Empleado empleado = empleadoService.getEmpleadoById(id);
        if(empleado != null) {
            return new ResponseEntity<>(empleado, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    @PostMapping()
    public ResponseEntity<Empleado> createEmpleado(@RequestBody Empleado empleado){
        Empleado newEmpleado = empleadoService.createEmpleado(empleado);
        if(newEmpleado != null){
        return new ResponseEntity<>(empleado, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Empleado> updateEmpleado(@PathVariable Long id, @RequestBody Empleado empleado){
        Empleado updatedEmpleado = empleadoService.updateEmpleado(id, empleado);
        if(updatedEmpleado != null){
            return new ResponseEntity<>(updatedEmpleado, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmpleado(@PathVariable Long id){
        empleadoService.deleteEmpleado(id);
        if(empleadoService.getEmpleadoById(id) == null){
            return new ResponseEntity<>("Empleado eliminado exitosamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al eliminar el empleado", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

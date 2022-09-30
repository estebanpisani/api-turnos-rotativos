package com.neolab.api.turnos.controllers;

import com.neolab.api.turnos.dto.EmpleadoDTO;
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
    public ResponseEntity<?> getEmpleados(){
        List<EmpleadoDTO> empleados = empleadoService.getEmpleados();
        if(empleados != null) {
            return new ResponseEntity<>(empleados, HttpStatus.OK);
        }
        return new ResponseEntity<>("Aún no hay empleados", HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmpleadoPorId(@PathVariable Long id){
        EmpleadoDTO dto = empleadoService.getEmpleadoById(id);
        if(dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>("Empleado no encontrado", HttpStatus.BAD_REQUEST);
    }
    @PostMapping()
    public ResponseEntity<?> createEmpleado(@RequestBody EmpleadoDTO dto){
        EmpleadoDTO newEmpleado = empleadoService.createEmpleado(dto);
        if(newEmpleado != null){
        return new ResponseEntity<>(newEmpleado, HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al crear empleado", HttpStatus.BAD_REQUEST);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmpleado(@PathVariable Long id, @RequestBody EmpleadoDTO dto){
        EmpleadoDTO updatedEmpleado = empleadoService.updateEmpleado(id, dto);
        if(updatedEmpleado != null){
            return new ResponseEntity<>(updatedEmpleado, HttpStatus.OK);
        }
        return new ResponseEntity<>("Empleado no encontrado", HttpStatus.BAD_REQUEST);
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

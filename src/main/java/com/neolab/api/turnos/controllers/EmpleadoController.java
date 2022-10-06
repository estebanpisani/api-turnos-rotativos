package com.neolab.api.turnos.controllers;

import com.neolab.api.turnos.dto.EmpleadoDTO;
import com.neolab.api.turnos.service.impl.EmpleadoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {
    @Autowired
    EmpleadoServiceImpl empleadoService;

    //Endpoint para obtener todos los empleados de la base de datos.
    @GetMapping()
    public ResponseEntity<?> getAllEmpleados(){
        List<EmpleadoDTO> empleados = empleadoService.getEmpleados();
        if(empleados != null) {
            return new ResponseEntity<>(empleados, HttpStatus.OK);
        }
        return new ResponseEntity<>("Aún no hay empleados", HttpStatus.BAD_REQUEST);
    }
    //Endpoint para obtener un empleado por su ID.
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmpleadoPorId(@PathVariable Long id){
        EmpleadoDTO dto = empleadoService.getEmpleadoById(id);
        if(dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>("Empleado no encontrado", HttpStatus.BAD_REQUEST);
    }
    //Endpoint para crear un nuevo empleado. Recibe sus datos mediante un DTO en el body de la request.
    @PostMapping()
    public ResponseEntity<?> createEmpleado(@RequestBody EmpleadoDTO dto){
        try {
            EmpleadoDTO newEmpleado = empleadoService.createEmpleado(dto);
            if (newEmpleado != null) {
                return new ResponseEntity<>(newEmpleado, HttpStatus.OK);
            }
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Error al crear empleado", HttpStatus.BAD_REQUEST);
    }
    // Endpoint para modificar un empleado existente en la base de datos.
    // Recibe los datos a modificar mediante un DTO en el body y el ID de referencia como path variable.
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmpleado(@PathVariable Long id, @RequestBody EmpleadoDTO dto){
        try {
            EmpleadoDTO updatedEmpleado = empleadoService.updateEmpleado(id, dto);
            if(updatedEmpleado != null){
                return new ResponseEntity<>(updatedEmpleado, HttpStatus.OK);
            }
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Empleado no encontrado", HttpStatus.BAD_REQUEST);
    }
    //Endpoint para eliminar un empleado de la base de datos. Recibe el ID de referencia como path variable.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmpleado(@PathVariable Long id){
        empleadoService.deleteEmpleado(id);
        if(empleadoService.getEmpleadoById(id) == null){
            return new ResponseEntity<>("Empleado eliminado exitosamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al eliminar el empleado", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

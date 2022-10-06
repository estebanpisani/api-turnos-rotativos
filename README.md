# Neoris Lab Java 2022 - Trabajo Páctico Nro. 2
Api para Turnos Rotativos

## Funcionalidades

- [ ] CRUD Empleado
- [ ] CRUD Jornada Laboral

## Endpoints

### Empleado

- `[GET] /api/empleados`: Obtiene todos los empleados de la base de datos.
- `[GET] /api/empleados/:id`: Obtiene un único empleado referenciado por su ID.
- `[POST] /api/empleados`: Guarda un nuevo empleado en la base de datos.
- `[PUT] /api/empleados/:id`: Actualiza los datos del empleado referenciado por su ID.
- `[DELETE] api/empleados/:id`: Elimina un empleado de la base de datos referenciado por su ID.

### Jornada

- `[GET] /api/calendario`: lista todas las jornadas laborales disponibles en la base de datos.
- `[POST] /api/calendario`: Agrega un nueva jornada laboral.
- `[GET] /api/calendario/:id`: Obtiene una única jornada laboral referenciada por su ID.
- `[PUT] /api/calendario/:id`: Actualiza la información de una jornada laboral específica.
- `[DELETE] /api/calendario/:id`: Elimina una jornada laboral de la base de datos.

## Instrucciones
Una vez iniciada la aplicación, importar en Postman la colección adjunta con el nombre `API Turnos Rotativos.postman_collection.json`.

Todas las request son casos funcionales, pero se pueden probar modificando los datos para comprobar que funcionan las validaciones en cada caso. Dentro de la colección de Postman, se encuentra una carpeta Errores, que son casos sencillos de horarios de entrada/salida mal ingresados.
El resto de las validaciones puede probarse creando jornadas en horarios ocupados o con días libres.

### Base de Datos
En caso de requerir acceder a la base de datos, ingresar a `{localhost}/h2-console/` e ingresar los siguientes datos:
- Driver Class: `org.h2.Driver`
- JDBC URL: `jdbc:h2:mem:testdb`
- User Name: `admin`
- Password: `admin`
## Datos
Los datos para las solicitudes POST y PUT requieren de un formato en particular para cada entidad.
Estos ya están ingresados en la colección de POSTMAN adjunta (.json).
### Empleado
`
{
  "nombre": string,
  "apellido": string,
  "fechaDeNacimiento": "yyyy/MM/dd",
  "email": string,
  "fechaAlta": "yyyy/MM/dd",
  "fechaBaja": "yyyy/MM/dd" (opcional para POST)
 }
 `
 ### Jornada
 `
{
  "entrada": "yyyy/MM/dd hh:mm",
  "salida": "yyyy/MM/dd hh:mm",
  "tipo": string,
  "empleadoId": int,
 }
 `
 
 
 El campo empleadoId siempre es requerido. No es posible asignar una jornada laboral sin empleado.
 Depende cada tipo de jornada (Normal, Extra, Dia Libre, Vacaciones), los campos serán requeridos o ignorados.
 Sólo en el caso de Vacaciones, los campos `"entrada"` y `"salida"` requieren un formato `"yyyy/MM/dd"`.
 
## Comentarios
- No logré encontrar una forma de solucionar los tipos de Jornadas con distintas clases, por lo que al tener clases especificadas con Enums, no logré desarrollar un
endpoint para crear un tipo de jornada nuevo.
En su defecto, las consignas según cada tipo fueron resueltas con validaciones.
- La relación de Empleado y Jornada debería ser ManyToMany. Por cuestiones de tiempo logré terminar la aplicación pero utilizando una relación OneToMany
Por lo que las jornadas sólo se crean para un solo empleado a la vez.
Estuve hasta última hora refactorizando para utilizar la relación ManyToMany pero no logré finalizarlo antes del horario de entrega.

## Autor
Esteban Pisani

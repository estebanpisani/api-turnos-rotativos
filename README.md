# Neoris Lab Java 2022 - Trabajo Páctico Nro. 2
La API responde a la necesidad del manejo de turnos rotativos, permitiendo a los usuarios gestionar las horas de trabajo y su jornada laboral.

## Funcionalidades

- [ ] CRUD Empleado
- [ ] CRUD Jornada Laboral
- [ ] CRUD Tipo de Jornada Laboral

## Descripción

Esta API permite:
- Dar de alta, editar y eliminar un empleado.
- Dar de alta un tipo de jornada laboral (Turno Extra, Vacaciones, Día Libre, Turno Normal,
etc.).
- Cargar la jornada laboral de un empleado, especificando el tipo, la fecha, hora de entrada y
salida.
- Listar para cada empleado la cantidad de horas cargadas por cada tipo de jornada laboral. 
- Mdificar la cantidad de horas de una jornada laboral previamente cargada.

### Validaciones por Tipo.
La API permite crear distintos tipos de jornadas especificando horas mínimas y máximas tanto para el día como para la semana.
Con estos atributos es posible validar cualquier tipo de jornada laboral.
Hay 4 tipos de Jornada previamente definidos.
- Normal
- Extra
- Dia Libre
- Vacaciones

Existen validaciones particulares solo para las tipos Dia Libre y Vacaciones.
Para el resto - Normal, Extra y cualquier tipo creado por el usuario - las validaciones son genéricas.

- Cada empleado no puede trabajar más de 48 horas semanales.
- Las horas de un turno normal pueden variar entre 6 y 8, y las de un turno extra entre 2 y 6.
- Para cada fecha, un empleado podrá cargar un turno normal, un turno extra o una combinación de ambos que no supere
las 12 horas.
- Por cada turno no puede haber más que 2 empleados.
- No se pueden cargar jornadas en fechas en las que el empleado tenga Dia Libre o Vacaciones.
- No se pueden cargar jornadas de ningún tipo en la misma fecha y horario que otra cargada previamente. De ser necesario, es posible editar las horas de cada jornada (Dia Libre y Vacaciones están exceptuadas).
- En la semana el empleado podrá tener hasta 2 días libres.

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

### Tipo

- `[GET] /api/tipos`: lista todos los tipos de jornadas laborales disponibles en la base de datos.
- `[POST] /api/tipos`: Agrega un nuevo tipo de jornada laboral.
- `[DELETE] /api/tipos/:id`: Elimina un tipo de jornada laboral de la base de datos.

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
  "fechaDeNacimiento": string:"yyyy/MM/dd",
  "email": string,
  "fechaAlta": string:"yyyy/MM/dd",
  "fechaBaja": string:"yyyy/MM/dd" (opcional para POST)
 }
 `
 ### Jornada*
 `
{
  "tipo": string,
  "entrada": string:"yyyy/MM/dd hh:mm",
  "salida": string:"yyyy/MM/dd hh:mm",
  "empleadosId": int[]
 }
 `
 
  *Los campos `"tipo"` y `"entrada"`son requeridos para todos los casos. Sólo en el caso de `"dia libre"` el campo y `"vacaciones"`, los campos `"entrada"` y `"salida"` requieren un formato `"yyyy/MM/dd"`.

 ### Tipo
 `
{
  "nombre": string,
  "horasDiariasMax": int,
  "horasDiariasMin": int,
  "horasSemanalesMax": int,
 }
 `

## Autor
Esteban Pisani

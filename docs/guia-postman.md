\# Guia de pruebas con Postman



\## Requisitos



\- Postman instalado.

\- Instancia EC2 encendida.

\- Acceso HTTP permitido desde el equipo de pruebas.

\- Direccion publica actual de la instancia.



\## Direccion base



Usar http://IP\_PUBLICA/apiinicial, reemplazando IP\_PUBLICA por

la direccion actual de EC2. La IP puede cambiar al detener e iniciar

la instancia.



\## 1. Consultar deportes



Metodo: GET

URL: http://IP\_PUBLICA/apiinicial/deportes

Body: none



Resultado esperado: 200 OK y una lista JSON.



\## 2. Crear un deporte



Metodo: POST

URL: http://IP\_PUBLICA/apiinicial/deportes

Body: raw, formato JSON



```json

{

&#x20; "nombre": "Deporte prueba documentacion",

&#x20; "minJugadores": 2,

&#x20; "maxJugadores": 4

}

```



Resultado esperado: 201 Created.

Anotar el ID recibido para las siguientes pruebas.

No asumir que el ID sera un numero especifico.



\## 3. Actualizar el deporte creado



Metodo: PUT

URL: http://IP\_PUBLICA/apiinicial/deportes/ID

Reemplazar ID por el recibido en el paso anterior.

Body: raw, formato JSON



```json

{

&#x20; "nombre": "Deporte prueba actualizado",

&#x20; "minJugadores": 3,

&#x20; "maxJugadores": 6

}

```



Resultado esperado: 200 OK con el mismo ID y los nuevos valores.



Consultar con GET la misma URL para comprobar que se guardaron.



\## 4. Eliminar el registro de prueba



Metodo: DELETE

URL: http://IP\_PUBLICA/apiinicial/deportes/ID

Body: none



Eliminar solamente el registro creado para estas pruebas.



Resultado esperado: 204 No Content, sin cuerpo de respuesta.

Consultar nuevamente la lista con GET y comprobar que ya no aparece.



\## Persistencia



POST, PUT y DELETE modifican la base de datos de AWS.

GET solo consulta.

La base local del PC y la exportacion SQL del repositorio

son copias independientes; no se sincronizan automaticamente.



\## Resultados esperados y evidencias



Esta guia describe resultados esperados, no certifica su ejecucion.

Registrar las pruebas realizadas y adjuntar capturas sin credenciales.



\## Errores comunes



\- Usar GET para intentar guardar: seleccionar POST o PUT.

\- Crear otro registro al actualizar: usar PUT con el ID en la URL.

\- JSON rechazado: seleccionar Body > raw > JSON y revisar la sintaxis.

\- Nombre duplicado al crear: usar otro nombre de prueba.

\- Sin conexion: revisar instancia, IP y reglas de acceso HTTP.


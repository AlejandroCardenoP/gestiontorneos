# Interfaz web Cancha

La aplicación sirve su interfaz en `/apiinicial/` (con la barra final) y en `/apiinicial/index.html`. Las rutas REST y Swagger se conservan. No requiere Node.js para compilar ni un servidor adicional.

## Funcionalidad

- Resumen con cantidades reales de deportes, equipos y torneos.
- Formularios de creación y edición; eliminación con confirmación.
- Búsqueda por nombre y filtro de torneos por estado.
- Detalle de torneo con equipos inscritos y cupos utilizados.
- Inscribir equipos del mismo deporte y cancelar inscripciones antes del inicio.
- Iniciar y finalizar torneos con confirmación y validaciones del servidor.
- Mensajes de errores de negocio, estados vacíos y actualización manual.
- Diseño adaptable a pantallas pequeñas y formularios con etiquetas accesibles.

Los datos se consultan al abrir la página y después de guardar cambios. El botón Actualizar consulta nuevamente la BD a través de las APIs. No hay datos de demostración precargados ni almacenamiento local que sustituya a MariaDB.

## Desarrollo y despliegue

Los archivos están en `gestiontorneos-main/src/main/resources/static/`: `index.html`, `styles.css` y `app.js`. Se empaquetan dentro del JAR al ejecutar `bootJar`. Para habilitarlos en EC2 debe desplegarse ese nuevo JAR siguiendo la guía de despliegue existente. Esta entrega no necesita cambios en las tablas ni en el proxy que ya publica `/apiinicial/`.

No abrir index.html directamente desde el explorador de archivos: requiere la aplicación Java para consultar las APIs del mismo servidor. La interfaz usa permisos equivalentes a los endpoints existentes; este proyecto todavía no incorpora inicio de sesión.

## Verificación

La prueba de integración comprueba que la página inicial y sus archivos se sirven correctamente, además de las reglas REST ya cubiertas. Para verificar visualmente: abrir la página en computadora y celular, crear un deporte, crear dos equipos válidos, crear un torneo, inscribir ambos e iniciar/finalizar el torneo. Confirmar también mensajes de cupo lleno y eliminación de registros relacionados. Utilizar datos de prueba identificables.

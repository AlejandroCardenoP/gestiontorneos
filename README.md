# Gestión de torneos — segunda entrega

API REST Java 25 / Spring Boot 4.1.1 / Gradle 9.7.1. Persistencia con
JdbcTemplate, consultas SQL parametrizadas y MariaDB/MySQL. Sin JPA.

## Casos de negocio

1. Crear un deporte y establecer límites de jugadores.
2. Registrar equipos válidos para ese deporte.
3. Crear un torneo con deporte, fecha y capacidad de 2 a 128 equipos.
4. Inscribir equipos: mismo deporte, sin duplicados, con cupo y estado INSCRIPCION.
5. Cancelar una inscripción antes de iniciar.
6. Iniciar el torneo con al menos dos equipos: pasa a EN_CURSO.
7. Finalizarlo: pasa a FINALIZADO; no admite nuevas inscripciones ni cancelaciones.

Finalizar aquí registra el cierre administrativo. Calendario, partidos,
marcadores, clasificación y ganadores quedan fuera de esta entrega.

## Una compilación canónica

El código activo está en `gestiontorneos-main/src`. El build de la raíz delega
en ese subproyecto. `src/` de la raíz se conserva como legado de la primera
entrega y **no se compila**. Así ejecutar Gradle desde la raíz o desde la
carpeta interna produce la misma aplicación.

```powershell
.\gradlew.bat test bootJar
```

En Linux: `bash ./gradlew test bootJar`.
Las pruebas usan una base H2 aislada con SQL en modo MySQL, nunca la base AWS.
H2 no reemplaza la verificación final de bloqueos e intercalaciones en MariaDB.

## Preparación de una base nueva

Crear una base `gestiontorneos` con UTF-8 en MariaDB/MySQL. Ejecutar allí:
`database/migrations/V2__torneos_equipos_inscripciones.sql`.
La migración crea las cuatro tablas y no carga registros de ejemplo.
Para conservar deportes existentes, ejecutar esa misma migración sobre la
base actual; no importar el dump antiguo como migración.

`database/gestiontorneos.sql` es el respaldo de la primera entrega y contiene
DROP TABLE y datos históricos. No ejecutarlo sobre una instalación actualizada.

Crear un usuario de aplicación con permisos SELECT, INSERT, UPDATE, DELETE
sobre gestiontorneos.*. El usuario de migración requiere CREATE y REFERENCES.
La aplicación no crea ni modifica tablas automáticamente.

## Ejecutar localmente (PowerShell)

```powershell
$env:SPRING_DATASOURCE_URL='jdbc:mysql://127.0.0.1:3306/gestiontorneos'
$env:SPRING_DATASOURCE_USERNAME='torneos_app'
$clave=Read-Host 'Clave de la base' -AsSecureString
$env:SPRING_DATASOURCE_PASSWORD=[System.Net.NetworkCredential]::new('', $clave).Password
java -jar gestiontorneos-main/build/libs/gestiontorneos-0.0.1-SNAPSHOT.jar
```

No publicar contraseñas en Git ni en capturas. Usar `deploy/app.env.example`
solo como plantilla; Spring Boot no lee automáticamente archivos `.env`.
El servicio de systemd carga `/etc/gestiontorneos/app.env`.

## Swagger / OpenAPI

- Local: http://127.0.0.1:9090/apiinicial/swagger-ui.html
- OpenAPI JSON: http://127.0.0.1:9090/apiinicial/v3/api-docs
- AWS vía Nginx: `http://IP_ACTUAL/apiinicial/swagger-ui.html`

Swagger permite consultar contratos y ejecutar operaciones reales sobre la
base configurada. La demo no tiene autenticación: limitar acceso de red a
evaluadores y equipo. No abrir 3306 o 9090 a Internet.

## Rutas (prefijo /apiinicial)

| Recurso | Rutas y métodos |
|---|---|
| Deportes | GET/POST /deportes; GET/PUT/DELETE /deportes/{id} |
| Equipos | GET/POST /equipos; GET/PUT/DELETE /equipos/{id} |
| Torneos | GET/POST /torneos; GET/PUT/DELETE /torneos/{id} |
| Inscripciones | GET/POST /torneos/{torneoId}/inscripciones |
| Cancelación | DELETE /torneos/{torneoId}/inscripciones/{equipoId} |
| Inicio | POST /torneos/{id}/inicio |
| Finalización | POST /torneos/{id}/finalizacion |

POST de creación: 201; consultas/cambios: 200; borrado: 204.
400: entrada inválida; 404: recurso inexistente; 409: conflicto de negocio o
integridad. Los errores usan ProblemDetail y no muestran consultas SQL.

## Documentación complementaria

- [Arquitectura y SOLID](docs/arquitectura-solid.md)
- [Flujo de ramas](docs/gitflow.md)
- [Actualizar EC2 y verificar](docs/despliegue-etapa2.md)
- [Casos de aceptación](docs/aceptacion-etapa2.md)
- [Colección Postman](docs/torneos-etapa2.postman_collection.json)

Importar la colección y definir `baseUrl`. Ejecutar en orden para capturar
IDs; el prefijo de demo único evita colisiones. No borrar datos previos.

# GestiÃ³n de torneos â€” entrega final del backend

API REST Java 25 / Spring Boot 4.1.1 / Gradle 9.7.1. Persistencia con
DAO mixto: JdbcTemplate con SQL para deportes/equipos y JPA para torneos/inscripciones. Base MariaDB/MySQL.

## Casos de negocio

1. Crear un deporte y establecer lÃ­mites de jugadores.
2. Registrar equipos vÃ¡lidos para ese deporte.
3. Crear un torneo con deporte, fecha y capacidad de 2 a 128 equipos.
4. Inscribir equipos: mismo deporte, sin duplicados, con cupo y estado INSCRIPCION.
5. Cancelar una inscripciÃ³n antes de iniciar.
6. Iniciar el torneo con al menos dos equipos: pasa a EN_CURSO.
7. Finalizarlo: pasa a FINALIZADO; no admite nuevas inscripciones ni cancelaciones.

Finalizar aquÃ­ registra el cierre administrativo. Calendario, partidos,
marcadores, clasificaciÃ³n y ganadores quedan fuera de esta entrega.

## Una compilaciÃ³n canÃ³nica

El cÃ³digo activo estÃ¡ en `gestiontorneos-main/src`. El build de la raÃ­z delega
en ese subproyecto. `src/` de la raÃ­z se conserva como legado de la primera
entrega y **no se compila**. AsÃ­ ejecutar Gradle desde la raÃ­z o desde la
carpeta interna produce la misma aplicaciÃ³n.

```powershell
.\gradlew.bat test bootJar
```

En Linux: `bash ./gradlew test bootJar`.
Las pruebas usan una base H2 aislada con SQL en modo MySQL, nunca la base AWS.
H2 no reemplaza la verificaciÃ³n final de bloqueos e intercalaciones en MariaDB.

## PreparaciÃ³n de una base nueva

Crear una base `gestiontorneos` con UTF-8 en MariaDB/MySQL. Ejecutar allÃ­:
`database/migrations/V2__torneos_equipos_inscripciones.sql`.
La migraciÃ³n crea las cuatro tablas y no carga registros de ejemplo.
Para conservar deportes existentes, ejecutar esa misma migraciÃ³n sobre la
base actual; no importar el dump antiguo como migraciÃ³n.

`database/gestiontorneos.sql` es el respaldo de la primera entrega y contiene
DROP TABLE y datos histÃ³ricos. No ejecutarlo sobre una instalaciÃ³n actualizada.

Crear un usuario de aplicaciÃ³n con permisos SELECT, INSERT, UPDATE, DELETE
sobre gestiontorneos.*. El usuario de migraciÃ³n requiere CREATE y REFERENCES.
La aplicaciÃ³n no crea ni modifica tablas automÃ¡ticamente.

## Ejecutar localmente (PowerShell)

```powershell
$env:SPRING_DATASOURCE_URL='jdbc:mysql://127.0.0.1:3306/gestiontorneos'
$env:SPRING_DATASOURCE_USERNAME='torneos_app'
$clave=Read-Host 'Clave de la base' -AsSecureString
$env:SPRING_DATASOURCE_PASSWORD=[System.Net.NetworkCredential]::new('', $clave).Password
java -jar gestiontorneos-main/build/libs/gestiontorneos-0.0.1-SNAPSHOT.jar
```

No publicar contraseÃ±as en Git ni en capturas. Usar `deploy/app.env.example`
solo como plantilla; Spring Boot no lee automÃ¡ticamente archivos `.env`.
El servicio de systemd carga `/etc/gestiontorneos/app.env`.

## Swagger / OpenAPI

- Local: http://127.0.0.1:9090/apiinicial/swagger-ui.html
- OpenAPI JSON: http://127.0.0.1:9090/apiinicial/v3/api-docs
- AWS vÃ­a Nginx: `http://IP_ACTUAL/apiinicial/swagger-ui.html`

Swagger permite consultar contratos y ejecutar operaciones reales sobre la
base configurada. La demo no tiene autenticaciÃ³n: limitar acceso de red a
evaluadores y equipo. No abrir 3306 o 9090 a Internet.

## Rutas (prefijo /apiinicial)

| Recurso | Rutas y mÃ©todos |
|---|---|
| Deportes | GET/POST /deportes; GET/PUT/DELETE /deportes/{id} |
| Equipos | GET/POST /equipos; GET/PUT/DELETE /equipos/{id} |
| Torneos | GET/POST /torneos; GET/PUT/DELETE /torneos/{id} |
| Inscripciones | GET/POST /torneos/{torneoId}/inscripciones |
| CancelaciÃ³n | DELETE /torneos/{torneoId}/inscripciones/{equipoId} |
| Comprobante | GET /torneos/{torneoId}/inscripciones/{equipoId}/comprobante |
| Inicio | POST /torneos/{id}/inicio |
| FinalizaciÃ³n | POST /torneos/{id}/finalizacion |

POST de creaciÃ³n: 201; consultas/cambios: 200; borrado: 204.
400: entrada invÃ¡lida; 404: recurso inexistente; 409: conflicto de negocio o
integridad. Los errores usan ProblemDetail y no muestran consultas SQL.

## DocumentaciÃ³n complementaria

- [Arquitectura y SOLID](docs/arquitectura-solid.md)
- [Flujo de ramas](docs/gitflow.md)
- [Actualizar EC2 y verificar](docs/despliegue-etapa2.md)
- [Casos de aceptaciÃ³n](docs/aceptacion-etapa2.md)
- [ColecciÃ³n Postman](docs/torneos-etapa2.postman_collection.json)

Importar la colecciÃ³n y definir `baseUrl`. Ejecutar en orden para capturar
IDs; el prefijo de demo Ãºnico evita colisiones. No borrar datos previos.

## Entrega final: SQL + JPA y pruebas de servicios

Ver [guía de la entrega final](docs/backend-final.md) para los mapeos, cierre de recursos,
matriz de pruebas, cobertura y procedimiento de actualización. No confundir pruebas
unitarias con pruebas de integración JPA: se incluyen ambas.

```powershell
.\gradlew.bat test :gestiontorneos-main:unitTestReport :gestiontorneos-main:jacocoTestCoverageVerification bootJar
```

Los informes quedan en `gestiontorneos-main/build/reports/tests/` y
`gestiontorneos-main/build/reports/jacoco/`. La cobertura de métodos de servicios se
exige sobre las pruebas unitarias aisladas, no sobre las pruebas HTTP.

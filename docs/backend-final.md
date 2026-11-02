# Entrega final del backend

## Alcance y arquitectura

Se completan los casos acordados: deportes, equipos, torneos, inscripciones,
transiciones de estado y consulta de comprobante. No incluye partidos, resultados,
clasificaciones, usuarios ni autenticación; no forman parte de los casos acordados.
La interfaz y las rutas existentes siguen siendo compatibles.

| DAO | Tecnología | Responsabilidad |
|---|---|---|
| JdbcDeporteRepository | SQL / JdbcTemplate | CRUD y validación de rangos existentes |
| JdbcEquipoRepository | SQL / JdbcTemplate | CRUD y comprobación de inscripciones |
| JpaTorneoRepository | JPA / EntityManager | CRUD, bloqueo y cambios de estado |
| JpaInscripcionRepository | JPA / EntityManager | Inscripciones y comprobantes |

Los servicios dependen de interfaces. Los records del dominio no son entidades JPA;
los adaptadores convierten las entidades persistentes a respuestas inmutables. Así no
se serializan proxies ni colecciones recursivas y `open-in-view` está deshabilitado.

## Relaciones JPA

- Deporte `OneToMany` Equipo / Torneo; cada Equipo y Torneo tiene `ManyToOne` Deporte.
- Torneo y Equipo tienen `OneToMany` Inscripcion; Inscripcion tiene ambos `ManyToOne`.
- Inscripcion tiene `OneToOne` Comprobante, cuya clave foránea es única.
- El comprobante contiene un código UUID persistido. Se crea con la inscripción y
  se consulta con GET `/torneos/{torneoId}/inscripciones/{equipoId}/comprobante`.
- Cancelar una inscripción elimina su comprobante con cascade/orphanRemoval.
  No hay cascada de borrado desde deporte, equipo o torneo hacia inscripciones.
- El código del comprobante es una referencia administrativa, no una credencial.

## Conexiones, SQL y transacciones

Las consultas SQL activas enumeran columnas. Incluso COUNT usa una columna, sin
asterisco. JdbcTemplate administra ResultSet, Statement y Connection; al terminar
la transacción devuelve la conexión al pool. Cerrar manualmente una conexión compartida
antes de terminar la transacción rompería la atomicidad.

JdbcInsert entrega el PreparedStatement a JdbcTemplate, que lo cierra. Si falla la
asignación de parámetros antes de entregarlo, JdbcInsert lo cierra y conserva el error.
El EntityManager es administrado por Spring; los repositorios no abren EntityManagers
manuales ni devuelven streams de consultas abiertos.

Se usa un JpaTransactionManager para el mismo DataSource. Las operaciones SQL y JPA
participan de la misma transacción. Una prueba fuerza un error después de insertar
con SQL y JPA y comprueba que ambas inserciones desaparecen. Otra repite operaciones
exitosas y fallidas y comprueba cero conexiones activas al terminar.

El bloqueo pesimista del torneo precede la revisión de cupos. También se usa al
iniciar, finalizar y cancelar. Las restricciones UNIQUE y FK refuerzan las reglas.

Referencias: [JdbcTemplate y recursos](https://docs.spring.io/spring-framework/reference/data-access/jdbc/core.html)
y [sincronización transaccional](https://docs.spring.io/spring-framework/reference/data-access/transaction/tx-resource-synchronization.html).

## Swagger y manejo de errores

Cada operación documenta su resultado 200/201/204, parámetros de ruta, cuerpo
validado y respuestas de error. Los DTO incluyen descripciones y ejemplos de campos.
ErroresApi reutiliza el contrato ProblemDetail sin repetir anotaciones en cada método.

400: datos o formato inválido; 404: recurso ausente; 409: conflicto de negocio,
integridad o bloqueo; 503: acceso a BD no disponible; 500: fallo inesperado.
Los detalles técnicos se registran en el servidor y no se exponen en la respuesta.

## Pruebas de todos los servicios

| Clase unitaria | Métodos públicos cubiertos |
|---|---|
| DeporteServiceTest | listar, obtener, crear, actualizar, eliminar |
| EquipoServiceTest | listar, obtener, crear, actualizar, eliminar |
| TorneoServiceTest | listar, obtener, crear, actualizar, eliminar, iniciar, finalizar |
| InscripcionServiceTest | listar, inscribir, cancelar, obtenerComprobante |

JUnit y Mockito prueban cada servicio aislado: éxito, ausencias, restricciones y
verificación de que una operación inválida no escribe en el repositorio.
`unitTest` no arranca Spring ni una BD. `unitTestReport` genera su cobertura y
`jacocoTestCoverageVerification` exige 100% de cobertura de métodos por clase de
servicio usando exclusivamente `unitTest.exec`. Esto no equivale a 100% de ramas.

JpaServicesIntegrationTest ejecuta los métodos de los cuatro servicios con Spring,
JPA y H2; comprueba OneToMany/ManyToOne/OneToOne, cascada del comprobante, rollback
mixto y liberación de conexiones. ApiBusinessIntegrationTest prueba contratos HTTP,
Swagger, validaciones y concurrencia. H2 no sustituye la validación en MariaDB.

## Despliegue pendiente en EC2

1. Revisar y fusionar la rama feature mediante Pull Request hacia dev. Actualizar main
   mediante revisión cuando la versión esté validada; no forzar historiales.
2. Respaldar la BD y el JAR actual. Detener escrituras durante la migración.
3. Con V2 ya aplicada, ejecutar como administrador de BD:

```bash
sudo mariadb gestiontorneos < database/migrations/V3__comprobantes_inscripcion.sql
```

V3 es repetible: inserta comprobantes solo para inscripciones que no los tienen.
No importar `database/gestiontorneos.sql`, que es un respaldo histórico con DROP.
4. Compilar/instalar el nuevo JAR y arrancar el servicio siguiendo la guía existente.
5. Verificar Swagger, crear una inscripción de prueba, consultar su comprobante,
   cancelarla y comprobar las relaciones en MariaDB.

Hibernate valida el esquema al arrancar; si falta V3, la aplicación no arrancará.
El JAR previo puede funcionar conservando la tabla adicional. Si se revierte y se
crean inscripciones con la versión vieja, ejecutar V3 nuevamente antes de volver.
Esta entrega prepara código y pruebas locales: no cambia EC2 ni su BD automáticamente.

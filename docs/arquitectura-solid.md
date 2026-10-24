# Arquitectura y principios SOLID

## Flujo por capas

```mermaid
flowchart LR
    HTTP[Postman / Swagger] --> API[api: Controllers y validaciÃ³n HTTP]
    API --> S[service: interfaces de casos de uso]
    S --> I[service.impl: reglas y transacciones]
    I --> R[repository: interfaces de persistencia]
    R --> J[repository.jdbc: SQL parametrizado]
    R --> P[repository.jpa: EntityManager y entidades]
    P --> DB[(MariaDB / MySQL)]
    J --> DB
```

`domain` contiene records inmutables, estados y excepciones sin dependencia
de Spring, JDBC o HTTP. `service.dto` define las solicitudes de casos de uso.
El controlador no conoce JdbcTemplate ni implementaciones concretas.
Los servicios reciben interfaces de repositorio por constructor.

## SOLID aplicado

- **S â€” responsabilidad Ãºnica:** controladores traducen HTTP; servicios toman
  decisiones de negocio; repositorios administran persistencia SQL/JPA; el advice traduce errores.
- **O â€” abierto/cerrado:** una nueva implementaciÃ³n de EquipoRepository puede
  sustituir JDBC sin modificar los consumidores de la interfaz.
- **L â€” sustituciÃ³n:** implementaciones respetan los contratos: Optional vacÃ­o
  si un ID no existe, lista vacÃ­a si no hay filas, ID generado al crear.
  `bloquear` requiere una transacciÃ³n activa y conserva semÃ¡ntica de consulta.
- **I â€” segregaciÃ³n:** cuatro contratos de servicio y cuatro de repositorio,
  especÃ­ficos de recursos; no existe una interfaz universal con mÃ©todos vacÃ­os.
- **D â€” inversiÃ³n:** capas superiores dependen de interfaces y reciben sus
  dependencias por constructor. Spring configura la implementaciÃ³n concreta.

SOLID no significa crear una interfaz para cada clase. JdbcInsert es un
detalle interno compartido para generar claves y no requiere un contrato pÃºblico.

## Clean Code

Paquetes en minÃºsculas, nombres de negocio, DTOs separados de respuestas,
records inmutables, validaciÃ³n de entrada, estados enumerados, errores comunes
y constructor injection. Se elimina field injection en el cÃ³digo activo.
Se conserva el JSON original de deportes para compatibilidad con Postman.
Las consultas usan parÃ¡metros; no interpolan valores del usuario en SQL.

## Integridad y concurrencia

FKs impiden borrar deportes con equipos/torneos o entidades con inscripciones.
UNIQUE impide repetir una inscripciÃ³n incluso bajo concurrencia.
La inscripciÃ³n bloquea primero la fila del torneo con SELECT FOR UPDATE:
validaciÃ³n de cupos e inserciÃ³n ocurren en la misma transacciÃ³n.
Iniciar/finalizar y cancelar usan el mismo bloqueo; no pueden saltarse el cierre.
DespuÃ©s se bloquea el equipo para impedir que cambie de deporte mientras se inscribe.
No se editan equipos inscritos. Cambiar el rango de un deporte no puede
invalidar equipos existentes. No se reduce el cupo por debajo de los inscritos.

La fecha de inicio es informativa/planificada: el inicio se realiza mediante
el caso de uso explÃ­cito, no por un proceso automÃ¡tico.

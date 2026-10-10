# Casos de aceptación

| Caso | Resultado esperado |
|---|---|
| CRUD deporte | 201/200/204 y persistencia SQL |
| Equipo fuera del rango del deporte | 409, sin fila nueva |
| Torneo con deporte inexistente | 404 |
| Inscribir equipo del mismo deporte | 201 |
| Repetir inscripción | 409 |
| Inscribir equipo de otro deporte | 409 |
| Inscribir cuando cupo está completo | 409 |
| Dos solicitudes compiten por último cupo | una 201 y otra 409 |
| Reducir cupos bajo equipos inscritos | 409 |
| Cambiar deporte con inscripciones | 409 |
| Iniciar con menos de dos equipos | 409 |
| Iniciar con dos equipos | EN_CURSO |
| Cancelar/inscribir tras iniciar | 409 |
| Finalizar torneo en curso | FINALIZADO |
| Reabrir torneo finalizado | 409 |
| Borrar una entidad referenciada | 409 |
| JSON inválido/nombre vacío/fecha inválida | 400 |
| Consultar ID inexistente | 404 |
| Swagger y OpenAPI | disponibles, cuatro grupos de recursos |

La suite automatizada usa HTTP real contra un puerto aleatorio y H2 aislado.
Los casos aquí descritos son criterios; consultar el reporte de tests para
la evidencia de ejecución. La verificación de despliegue MariaDB en AWS es
un paso posterior y no se presupone realizada.

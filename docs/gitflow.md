# Git Flow mínimo para el equipo

## Estado encontrado y decisión

En la revisión del 10 de octubre de 2026, main estaba en c7b72e8.
Existían dev, develop y ramas de trabajo. dev es antecesora de main y está
cuatro commits detrás; develop tiene un historial sin ancestro común con main.
Usamos **dev como rama de integración** (cumple el papel de develop en Git Flow).
No se reescribe ni borra develop: se conserva como histórica y sus futuros
aportes deben revisarse por separado.

La primera integración de esta etapa incorpora los avances de main y el nuevo
desarrollo a dev mediante un PR, sin force push. La rama de trabajo es
feature/torneos-inscripciones-swagger, basada en el main funcional.

## Flujo

- main: entregas estables, cambios por PR.
- dev: integración de la siguiente entrega.
- feature/<caso>: nace de dev y regresa a dev por PR.
- release/<version>: nace de dev, se prueba y se integra a main y dev.
- hotfix/<correccion>: nace de main, regresa a main y dev.

## Aportes de cada integrante

Cada persona configura su propio nombre/correo, realiza un aporte, lo prueba
y crea commits descriptivos. No cambiar autores ni usar commits vacíos para
simular colaboración. Una segunda persona revisa el PR antes de fusionarlo.

Tras la integración inicial:

```bash
git switch dev
git pull --ff-only origin dev
git switch -c feature/pruebas-inscripciones
# realizar y probar el aporte
git add archivos-del-aporte
git commit -m "Prueba inscripcion con cupo agotado"
git push -u origin feature/pruebas-inscripciones
```

Abrir PR a dev. Requerir revisión y CI correcto. El administrador debe
configurar esas protecciones en GitHub: este archivo no las activa.
Guardar enlace al PR, commits y evidencias reales para la evaluación.

Esta entrega no fusiona automáticamente dev/main ni despliega a EC2.

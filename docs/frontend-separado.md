# Entrega con dos proyectos

## Responsabilidades

| Proyecto | Contenido |
| --- | --- |
| gestiontorneos | APIs Java, servicios, DAO SQL/JPA, base de datos, Swagger y pruebas del backend |
| gestiontorneos-frontend | HTML, CSS, JavaScript, cliente HTTP, servidor local, pruebas y despliegue de la web |

Backend publicado: https://github.com/AlejandroCardenoP/gestiontorneos

Repositorio del frontend: https://github.com/Brayanaristi/frontend

## Desarrollo local

1. Preparar MariaDB y sus migraciones siguiendo el README del backend.
2. Configurar las variables de conexión del backend sin guardar contraseñas en Git.
3. Iniciar el backend desde su raíz: `./gradlew.bat -p gestiontorneos-main bootRun` en Windows.
4. En otra terminal, abrir la carpeta del frontend y ejecutar `npm start` (Node 22+).
5. Abrir http://localhost:5500. El frontend apunta por defecto a http://127.0.0.1:9090/apiinicial/.

`APP_CORS_ALLOWED_ORIGINS` controla los orígenes admitidos. Por defecto admite
`http://localhost:5500` y `http://127.0.0.1:5500`. Para otro dominio o puerto,
establecer la lista exacta separada por comas y reiniciar el backend.
No es una lista de URLs de endpoints. No reemplaza autenticación.

## Despliegue EC2 sin interrupción de la interfaz

Publicar primero el frontend como archivos estáticos con Nginx, conservando el
proxy `/apiinicial/` existente. La guía y el fragmento Nginx están en el repositorio
frontend. Verificar la nueva página raíz antes de reemplazar el JAR del backend.

La página pasa de `/apiinicial/` a `/`. Swagger y las APIs conservan sus rutas.
Es posible usar el mismo servidor y dominio con repositorios y despliegues separados.
No se requieren migraciones nuevas ni importar respaldos SQL para este cambio.

## Comprobaciones

- Backend: ejecutar `test`, `unitTestReport`, `jacocoTestCoverageVerification` y `bootJar`.
- Frontend: ejecutar `npm run check`.
- Probar ambos proyectos iniciados en procesos diferentes.
- Verificar crear, consultar, editar y eliminar mediante la web y confirmar los datos en Swagger.
- Comprobar inscripciones y los errores 404/409 con datos de prueba.
- Conservar evidencias y entregar ambos enlaces GitHub, web y Swagger.

La separación se trabaja en `feature/frontend-separado`; integrar mediante un PR
revisado. El frontend tiene un historial Git independiente. Mantener main como
versión entregable y realizar cambios posteriores en ramas feature.

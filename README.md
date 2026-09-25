# Gestion de torneos

API REST de deportes con Java 25, Spring Boot, Gradle y MySQL/MariaDB.

## Proyecto utilizado

La aplicacion desplegada en EC2 se compila desde gestiontorneos-main/.
El proyecto de la raiz se conserva como version anterior.

## Contenido

- gestiontorneos-main/src/: codigo de la API con persistencia JDBC.
- database/gestiontorneos.sql: estructura y datos exportados de AWS.
- deploy/: configuraciones de systemd, Nginx y MariaDB.
- deploy/app.env.example: ejemplo de variables sin credenciales reales.

## Base de datos

Importar en una instancia de pruebas vacia:
```bash
sudo mariadb < database/gestiontorneos.sql
```

El archivo puede reemplazar tablas existentes. Respaldar antes de importarlo.
Crear un usuario de aplicacion con permisos SELECT, INSERT, UPDATE y DELETE
sobre gestiontorneos.*. Configurar sus credenciales mediante variables
SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME y
SPRING_DATASOURCE_PASSWORD.

## Compilacion

Desde la raiz del repositorio:
```bash
bash ./gradlew -p gestiontorneos-main bootJar --no-daemon --max-workers=1
```

## Ejecucion

Con las variables de conexion configuradas:
```bash
java -jar gestiontorneos-main/build/libs/gestiontorneos-0.0.1-SNAPSHOT.jar
```

## Despliegue EC2

El servicio usa /opt/gestiontorneos/app.jar y carga las variables desde
/etc/gestiontorneos/app.env, protegido con permisos 600.

Las configuraciones de deploy/ corresponden a Amazon Linux 2023:
- gestiontorneos.service: /etc/systemd/system/
- nginx-gestiontorneos.conf: /etc/nginx/default.d/
- mariadb-gestiontorneos.cnf: /etc/my.cnf.d/

Adaptar las rutas y el usuario del servicio en otros equipos.
Crear app.env a partir del ejemplo y reemplazar la clave en el servidor.
Nunca subir el archivo con credenciales reales.

Nginx recibe solicitudes en el puerto 80 y las envia a 127.0.0.1:9090.
MariaDB escucha solamente en 127.0.0.1.
La API no incorpora autenticacion; restringir el acceso de red para la demo.

## Operaciones

- GET /apiinicial/deportes
- GET /apiinicial/deportes/{id}
- POST /apiinicial/deportes
- PUT /apiinicial/deportes/{id}
- DELETE /apiinicial/deportes/{id}

Ejemplo de cuerpo JSON para POST y PUT:
```json
{"nombre":"Tenis prueba","minJugadores":1,"maxJugadores":2}
```

El ID se genera automaticamente al crear un registro.
Los cambios se guardan en la base de AWS; no se sincronizan con el PC.

## Validacion

Compilacion y consulta GET comprobadas en EC2.
Creacion POST comprobada desde Postman.
Actualizacion PUT confirmada por el usuario.
Eliminacion DELETE pendiente de confirmar.

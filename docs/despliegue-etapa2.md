# Actualización de EC2 — segunda entrega

No desplegado automáticamente. Primero revisar la rama y ejecutar las pruebas.
La configuración anterior de Nginx ya reenvía /apiinicial/, incluido Swagger.

## 1. Respaldar fuera de Git

En EC2, con el servicio actual funcionando:
```bash
sudo mariadb-dump --single-transaction gestiontorneos > "$HOME/gestiontorneos-antes-v2-$(date +%Y%m%d-%H%M%S).sql"
sudo cp /opt/gestiontorneos/app.jar /opt/gestiontorneos/app-v1.jar
```

## 2. Obtener y compilar la versión revisada

Usar un checkout de la rama revisada o de la release fusionada. No ejecutar
git reset sobre cambios locales de la instancia. Desde la raíz:
```bash
bash ./gradlew test bootJar --no-daemon --max-workers=1 -Dorg.gradle.jvmargs="-Xmx384m -XX:MaxMetaspaceSize=256m"
```

En EC2 con ~1 GB RAM, preferir compilar y probar en el PC/CI y transferir
el JAR para evitar competir con MariaDB. Si se compila en EC2 y falta memoria,
detener temporalmente gestiontorneos durante una ventana de mantenimiento.

## 3. Migración no destructiva

```bash
sudo mariadb gestiontorneos < database/migrations/V2__torneos_equipos_inscripciones.sql
sudo mariadb gestiontorneos -e "SHOW TABLES; SELECT COUNT(*) FROM deporte;"
```

No ejecutar database/gestiontorneos.sql: es el respaldo antiguo, no la migración.
V2 asume que deporte tiene el esquema original y usa InnoDB. Si se modificó
manualmente, comparar SHOW CREATE TABLE deporte antes de aplicar.

## 4. Reemplazar el ejecutable

Solo con compilación y migración correctas:
```bash
sudo systemctl stop gestiontorneos
sudo install -m 644 gestiontorneos-main/build/libs/gestiontorneos-0.0.1-SNAPSHOT.jar /opt/gestiontorneos/app.jar
sudo systemctl start gestiontorneos
```

La contraseña sigue en /etc/gestiontorneos/app.env y no se reemplaza.
El usuario torneos_app conserva CRUD sobre gestiontorneos.* (también tablas nuevas).

## 5. Verificar

```bash
curl -i http://127.0.0.1:9090/apiinicial/deportes
curl -i http://127.0.0.1/apiinicial/torneos
curl -i http://127.0.0.1/apiinicial/v3/api-docs
sudo journalctl -u gestiontorneos -n 50 --no-pager
```

Abrir Swagger desde la IP actual. Ejecutar colección Postman con datos de
prueba. Verificar tablas equipo, torneo e inscripcion directamente en MariaDB.
El acceso debe quedar restringido porque la demo aún no tiene autenticación.

## Reversión de aplicación

Ante un fallo, detener servicio, copiar app-v1.jar a app.jar e iniciar servicio.
Las tablas nuevas pueden permanecer: V2 no elimina ni cambia columnas de deporte.
No restaurar un respaldo completo sin evaluar datos nuevos y detener escrituras.

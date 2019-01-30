# Vert.x con recursos seguros utilizando Keycloak, Roles y JWT

#### Configuración de Keycloak

- Ejecutar la imagen docker de **keycloak** utilizando el puerto local 8282 y el usuario y clave **admin**:

> docker run -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -p 8282:8080 jboss/keycloak

- Acceder desde el navegador a **http://localhost:8282/** y luego de ingresar a la opción **Add realm**: 
 
![Alt text](doc/img/keycloak/00.png?raw=true "Crear Realm")

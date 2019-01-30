# Vert.x con recursos seguros utilizando Keycloak, Roles y JWT

[Configuración Keycloak](#configuración-keycloak)

[Utilizando Vert.x](#utilizando-vertx)

# Configuración Keycloak

- Ejecutar la imagen docker de **keycloak** utilizando el puerto local 8282 y el usuario y clave **admin**:

> docker run -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -p 8282:8080 jboss/keycloak

- Acceder desde el navegador a **http://localhost:8282/** y luego ingresar a la opción **Add realm** para crear el ream **vertx**: 
 
![Alt text](doc/img/keycloak/00.png?raw=true "Crear Realm - 1")

![Alt text](doc/img/keycloak/01.png?raw=true "Crear Realm - 2")

- Luego de crearlo ir a la pestaña **Keys** y obtener la clave pública RS256 que luego será utilizada en la aplicación:

![Alt text](doc/img/keycloak/02.png?raw=true "Pestaña Keys")

![Alt text](doc/img/keycloak/03.png?raw=true "Contenido Clave Pública")

- Ahora crearemos un **cliente** que podrá utilizar keycloak junto con su URL de origen:

![Alt text](doc/img/keycloak/04.png?raw=true "Agregar Cliente")

- Luego creamos un usuario para que acceda a la aplicación:

![Alt text](doc/img/keycloak/05.png?raw=true "Crear Usuario")

- También definimos la clave en la pestaña **Credentials**:

![Alt text](doc/img/keycloak/06.png?raw=true "Clave de Usuario")

- Agregar un **rol** (vertx-role) asociado al cliente anteriormente creado:

![Alt text](doc/img/keycloak/12.png?raw=true "rol del cliente")
 
- En **Users** ir a **Role Mapping**, seleccionar el cliente y luego asignar roles:

![Alt text](doc/img/keycloak/13.png?raw=true "role mapping")

# Utilizando Vertx



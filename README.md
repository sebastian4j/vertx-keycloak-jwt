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

- Para obtener la configuración de instalación en formato **OIDC JSON**:

![Alt text](doc/img/keycloak/14.png?raw=true "OIDC JSON")

# Utilizando Vertx

- La **estructura** del proyecto es simple, el objetivo es demostrar como utilizar keycloak:

![Alt text](doc/img/vertx/01.png?raw=true "estructura")

- El archivo **config.json** contiene datos de configuración para acceder a keycloak (este modo de configuración es opcional, se puede omitir el ConfigRetriever y crear un JsonObject en el código con la configuración necesaria del keycloak):

![Alt text](doc/img/vertx/02.png?raw=true "configuración keycloak")

(solo muestro en la imagen los datos relacionados con keycloak)

**realm:** el nombre del realm creado inicialmente.

**realm-public-key:** contenido de la clave pública.

**auth-server-url:** url de keycloak que autenticará.

**resource**: nombre del cliente que creamos.

utilizar la <a href="https://www.keycloak.org/docs/latest/securing_apps/index.html">documentación de referencia</a> para consultar las definiciones.

- Al ejecutar la aplicación quedará escuchando en el puerto 8080 y la url que probaremos es **http://localhost:8080/resources/secure** utilizando el método **GET** (usando RestMan):

![Alt text](doc/img/vertx/03.png?raw=true "401")

al intentar acceder al recurso obtenemos el código de respuesta 401

- Para poder acceder tenemos que solicitar un **token de acceso** a keycloak con el usuario, clave y realm que creamos:

> curl -s --data "grant_type=password&client_id=vertx&username=**usuario**&password=**clave**" http://localhost:8282/auth/realms/**vertx**/protocol/openid-connect/token

al ejecutarlo correctamente obtenemos un json que contiene la clave **access_token** (entre otras) con el contenido del token que necesitamos.

- Ahora con el token volvemos a realizar el request al recurso protegido utilizando **token authentication** (agregando el header **Authorization** y el tipo **Bearer** junto al token):


![Alt text](doc/img/vertx/04.png?raw=true "Request ok")

(en la imagen no se muestra todo el contenido del token)

- Al intentar acceder a la aplicación (**http://localhost:8080/resources/secure**) directamente desde el navegador veremos la pantalla de keycloak para realizar el login:

![Alt text](doc/img/vertx/05.png?raw=true "login keycloak")

- Luego de ingresar nuestras credenciales podemos acceder al recurso protegido:

![Alt text](doc/img/vertx/06.png?raw=true "acceso al recurso protegido")


# TODO: integrar Docker, Kafka, Consul, ... 



 
 




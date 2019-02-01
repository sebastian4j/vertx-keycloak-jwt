export consul_host=127.0.0.1 
export consul_port=8500 
export puerto=8081 
export host=127.0.0.1

java -cp target/lib/*:target/vertx-keycloak-jwt-0.0.1.jar com.sebastian.vertx.VertxKeycloak

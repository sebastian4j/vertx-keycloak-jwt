export redis_host=127.0.0.1 
export redis_port=6379
export puerto=8082 
export host=127.0.0.1
export nombre=servicio_a

java -cp target/lib/*:target/vertx-keycloak-jwt-0.0.1.jar com.sebastian.vertx.VertxKeycloak

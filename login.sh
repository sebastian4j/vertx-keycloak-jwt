curl -s --data "grant_type=password&client_id=vertx&username=$1&password=$2" http://localhost:8282/auth/realms/vertx/protocol/openid-connect/token

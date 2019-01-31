package com.sebastian.vertx;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.sebastian.vertx.clientes.ConsulCliente;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.OAuth2AuthHandler;

/**
 * Utilizando keycloak JWT para la autorizacion del acceso a recursos.
 *
 * @see <a href="https://vertx.io/">vertx.io</a>
 * @see <a href="https://vertx.io/docs/">https://vertx.io/docs/</a>
 * @see <a href= "https://vertx.io/docs/vertx-auth-oauth2/java/">vertx.io/vertx-auth-oauth2/</a>
 * @see <a href="https://jwt.io/">jwt.io/</a>
 * @author Sebastian Avila A.
 */
public class VertxKeycloak {
  private static final Logger LOGGER = Logger.getLogger(VertxKeycloak.class.getCanonicalName());
  private Vertx vertx;
  private ConsulCliente cc;
  private JsonObject config;
  private Router router;

  private void agregarAuthHandler(final String clavePublica, final String urlAuth) {
    final var oa =
        OAuth2AuthHandler.create(
            KeycloakAuth.create(vertx, config.getJsonObject("keycloak")
                .put("auth-server-url", urlAuth).put("realm-public-key", clavePublica)),
            config.getString("callback"));
    oa.setupCallback(router.get("/callback"));
    router.route("/resources/*").handler(oa);
  }

  private void configurarRouter() {
    router.route("/resources/secure").handler(rc -> {
      final StringBuilder sb = new StringBuilder();
      final User s = rc.user();
      s.isAuthorized("vertx-role", res -> {
        if (res.succeeded() && res.result()) {
          sb.append("tiene acceso al secreto");
        } else {
          sb.append("no puede acceder al secreto");
        }
      });
      LOGGER.log(Level.INFO, "principal {0}", s.principal());
      rc.response().end("hola desde el recurso protegido, " + sb.toString());
    });
  }

  private void lanzar() {
    vertx = Vertx.vertx();
    ConfigRetriever.create(vertx).getConfig(c -> {
      config = c.result();
      final var consulConfig = config.getJsonObject("consul");
      cc = new ConsulCliente(vertx);
      cc.obtenerValor(consulConfig.getString("realm-clave-publica"),
          pk -> cc.obtenerValor(consulConfig.getString("auth-server-url"), auth -> {
            router = Router.router(vertx);
            agregarAuthHandler(pk, auth);
            configurarRouter();
            vertx.createHttpServer().requestHandler(router).listen(config.getInteger("puerto"));
          }));
    });
  }

  public static void main(String[] args) {
    new VertxKeycloak().lanzar();
  }
}

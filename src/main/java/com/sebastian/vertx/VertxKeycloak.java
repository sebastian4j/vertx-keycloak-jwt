package com.sebastian.vertx;

import java.util.logging.Level;
import java.util.logging.Logger;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
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
 *
 *
 * @author Sebastian Avila A.
 */
public class VertxKeycloak {
  private static final Logger LOGGER = Logger.getLogger(VertxKeycloak.class.getCanonicalName());

  public static void main(String[] args) {
    final var vertx = Vertx.vertx();
    ConfigRetriever.create(vertx).getConfig(c -> {
      final var config = c.result();
      final var router = Router.router(vertx);
      final var oa =
          OAuth2AuthHandler.create(KeycloakAuth.create(vertx, config.getJsonObject("keycloak")),
              config.getString("callback"));
      oa.setupCallback(router.get("/callback"));
      router.route("/resources/*").handler(oa);
      router.route("/resources/secure").handler(rc -> {
        final StringBuilder sb = new StringBuilder();
        final User s = rc.user();
        s.isAuthorized(config.getString("rol-esperado"), res -> {
          if (res.succeeded() && res.result()) {
            sb.append("tiene acceso al secreto");
          } else {
            sb.append("no puede acceder al secreto");
          }
        });
        LOGGER.log(Level.INFO, "principal {0}", s.principal());
        rc.response().end("hola desde el recurso protegido, " + sb.toString());
      });
      vertx.createHttpServer().requestHandler(router).listen(config.getInteger("puerto"));
    });
  }
}

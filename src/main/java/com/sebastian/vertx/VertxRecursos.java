package com.sebastian.vertx;

import java.util.logging.Level;
import java.util.logging.Logger;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * recursos expuestos.
 *
 * @author Sebastian Avila A.
 *
 */
public class VertxRecursos {
  private static final Logger LOGGER = Logger.getLogger(VertxRecursos.class.getCanonicalName());

  public void agregarRecursos(final Router router) {
    router.route("/resources/secure").handler(this::agregarRecursoSeguro);
    router.route("/saludo").handler(this::agregarSaludo);
  }

  /**
   * agregar un saludo para el cliente =).
   *
   * @param rc
   */
  private void agregarSaludo(RoutingContext rc) {
    rc.response().end("hola");
  }

  /**
   * agregar recurso para el path secure, pero no la protección.
   *
   * @param rc
   */
  private void agregarRecursoSeguro(RoutingContext rc) {
    final StringBuilder sb = new StringBuilder();
    final User s = rc.user();
    if (s != null) {
      s.isAuthorized("vertx-role", res -> {
        if (res.succeeded() && res.result()) {
          sb.append("tiene acceso al secreto");
        } else {
          sb.append("no puede acceder al secreto");
        }
      });
      LOGGER.log(Level.INFO, "principal {0}", s.principal());
    }
    rc.response().end("hola desde el recurso protegido, " + sb.toString());
  }
}

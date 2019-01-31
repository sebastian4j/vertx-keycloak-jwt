package com.sebastian.vertx.clientes;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.vertx.core.Vertx;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.ext.consul.ConsulClientOptions;

/**
 * cliente para las operaciones sobre consul.
 *
 * <p>
 * para ejecutar consul: consul agent -dev -ui
 *
 * obtener consul -> https://www.consul.io/downloads.html
 *
 * claves requeridas:
 *
 * keycloak/realm/vertx/public-key: contenido de la clave publica para el cliente keycloak
 * keycloak/auth/url: url para autorizar con keycloak
 * 
 * @author Sebastian Avila A.
 */
public class ConsulCliente {
  private final ConsulClient cliente;
  private static final Logger LOGGER = Logger.getLogger(ConsulCliente.class.getCanonicalName());

  public ConsulCliente(Vertx vertx) {
    cliente =
        ConsulClient.create(vertx, new ConsulClientOptions().setHost(System.getenv("consul.host"))
            .setPort(Integer.parseInt(System.getenv("consul.port"))));
  }

  public void obtenerValor(final String clave, Consumer<String> callback) {
    cliente.getValue(clave, res -> {
      if (res.succeeded()) {
        LOGGER.info("clave conseguida " + clave);
        callback.accept(res.result().getValue());
      } else {
        LOGGER.log(Level.WARNING, "error en k/v consul", res.cause());
      }
    });
  }
}

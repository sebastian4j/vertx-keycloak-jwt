package com.sebastian.vertx.clientes.redis;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.HttpEndpoint;

/**
 * cliente para las operaciones sobre redis.
 *
 * claves requeridas:
 *
 * keycloak/realm/vertx/public-key: contenido de la clave publica para el cliente keycloak
 * keycloak/auth/url: url para autorizar con keycloak
 *
 * @author Sebastian Avila A.
 */
public class RedisCliente {
  private final RedisClient redis;
  private static final Logger LOGGER = Logger.getLogger(RedisCliente.class.getCanonicalName());
  private final ServiceDiscovery discovery;
  private Record publishedRecord;

  public RedisCliente(Vertx vertx) {
    final var puerto = Integer.parseInt(System.getenv("redis_port"));
    final var host = System.getenv("redis_host");
    redis = RedisClient.create(vertx, new JsonObject().put("host", host).put("port", puerto));
    discovery =
        ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(
            new JsonObject().put("host", host).put("port", puerto).put("key", "records")));
  }

  public void obtenerValor(final String clave, Consumer<String> callback) {
    redis.get(clave, res -> {
      if (res.succeeded()) {
        LOGGER.info("clave conseguida " + clave);
        LOGGER.info("clave conseguida " + res.result());
        callback.accept(res.result());
      } else {
        LOGGER.info("clave no encontrada");
        throw new IllegalStateException("error en k/v consul", res.cause());
      }
    });
  }

  /**
   * registra el servicio en consul.
   */
  public void registrarServicio(final RegistroServicio rsc, final Consumer<Record> callback) {
    final Record record =
        HttpEndpoint.createRecord(rsc.getNombre(), rsc.getHost(), rsc.getPuerto(), "/");

    discovery.publish(record, ar -> {
      if (ar.succeeded()) {
        publishedRecord = ar.result();
        LOGGER.info("registrado " + ar.result().getLocation());
        LOGGER.info("registrado " + ar.result().getName());
        callback.accept(publishedRecord);
      } else {
        throw new IllegalStateException("error al registrar servicio", ar.cause());
      }
    });
  }

  /**
   * consulta un servicio registrado en consul.
   *
   * @param nombreServicio nombre del servicio buscado
   */
  public void obtenerServicio(final String nombreServicio,
      Consumer<Optional<ServiceReference>> consumer) {
    discovery.getRecord(new JsonObject().put("name", nombreServicio), ar -> {
      if (ar.succeeded() && ar.result() != null) {
        final ServiceReference reference = discovery.getReference(ar.result());
        consumer.accept(Optional.ofNullable(reference));
      } else {
        LOGGER.info("no se pudo obtener el cliente");
        consumer.accept(Optional.empty());
      }
    });
  }
}

package com.sebastian.vertx.clientes.consul;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;
import io.vertx.core.Vertx;
import io.vertx.ext.consul.CheckOptions;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.ServiceOptions;

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
        ConsulClient.create(vertx, new ConsulClientOptions().setHost(System.getenv("consul_host"))
            .setPort(Integer.parseInt(System.getenv("consul_port"))));
  }

  public void obtenerValor(final String clave, Consumer<String> callback) {
    cliente.getValue(clave, res -> {
      if (res.succeeded()) {
        LOGGER.info("clave conseguida " + clave);
        callback.accept(res.result().getValue());
      } else {
        throw new IllegalStateException("error en k/v consul", res.cause());
      }
    });
  }

  /**
   * registra el servicio en consul.
   */
  public void registrarServicio(final RegistroServicioConsul rsc,
      final Consumer<ServiceOptions> callback) {
    final ServiceOptions opts =
        new ServiceOptions().setName(rsc.getNombre()).setId(rsc.getId()).setTags(rsc.getTags())
            .setCheckOptions(new CheckOptions()
                .setTtl(new StringBuilder().append(rsc.getSegundosTtl()).append("s").toString()))
            .setAddress(rsc.getHost()).setPort(rsc.getPuerto());
    LOGGER.info(rsc.toString());
    cliente.registerService(opts, res -> {
      if (res.succeeded()) {
        callback.accept(opts);
      } else {
        throw new IllegalStateException("error al registrar servicio en consul", res.cause());
      }
    });
  }

  /**
   * consulta un servicio en consul.
   *
   * @param nombreServicio nombre del servicio buscado
   */
  public void consultarServicio(final String nombreServicio,
      Consumer<Set<ConsultaServicioConsul>> consumer) {
    cliente.catalogServiceNodes(nombreServicio, res -> {
      if (res.succeeded()) {
        LOGGER.info("found " + res.result().getList().size() + " services");
        LOGGER.info("consul state index: " + res.result().getIndex());
        final var csc = new HashSet<ConsultaServicioConsul>();
        res.result().getList().stream()
            .forEach(service -> csc.add(new ConsultaServicioConsul(nombreServicio,
                service.getNode(), service.getAddress(), service.getPort())));
        consumer.accept(csc);
      } else {
        throw new IllegalStateException("error al obtener servicios desde consul", res.cause());
      }
    });
  }
}

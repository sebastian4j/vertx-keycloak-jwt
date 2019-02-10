package com.sebastian.vertx;

import java.util.logging.Logger;
import java.util.stream.Collectors;
import com.sebastian.vertx.clientes.redis.RedisCliente;
import com.sebastian.vertx.clientes.redis.RegistroServicio;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
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
  private RedisCliente cc;
  private JsonObject config;
  private Router router;
  private String nombreServicio;

  private void agregarAuthHandler(final String clavePublica, final String urlAuth) {
    final var oa =
        OAuth2AuthHandler.create(
            KeycloakAuth.create(vertx, config.getJsonObject("keycloak")
                .put("auth-server-url", urlAuth).put("realm-public-key", clavePublica)),
            config.getString("callback"));
    oa.setupCallback(router.get("/callback"));
    router.route("/resources/*").handler(oa);
  }

  private void lanzar() {
    vertx = Vertx.vertx();
    ConfigRetriever.create(vertx).getConfig(c -> {
      final int puerto = Integer.parseInt(System.getenv("puerto"));
      LOGGER.info("puerto: " + puerto);
      final String host = System.getenv("host");
      LOGGER.info("host: " + host);
      config = c.result();
      LOGGER.info("config: " + config);
      final var confServicio = config.getJsonObject("servicio");
      LOGGER.info("config servicio: " + confServicio);
      nombreServicio = System.getenv("nombre");
      final var redisConfig = config.getJsonObject("redis");
      LOGGER.info("config: " + redisConfig);
      cc = new RedisCliente(vertx);
      cc.obtenerValor(redisConfig.getString("realm-clave-publica"),
          pk -> cc
              .obtenerValor(redisConfig.getString("auth-server-url"),
                  auth -> cc.registrarServicio(new RegistroServicio(nombreServicio,
                      System.getenv("redis_id"), confServicio.getJsonArray("tags").stream()
                          .map(f -> (String) f).collect(Collectors.toList()),
                      10, host, puerto), e -> {
                        router = Router.router(vertx);
                        agregarAuthHandler(pk, auth);
                        agregarConsultaServicios();
                        agregarRoot();
                        new VertxRecursos().agregarRecursos(router);
                        vertx.createHttpServer().requestHandler(router).listen(puerto, host);
                        LOGGER.info("servicio cargado");
                      })));
    });
  }

  private void agregarRoot() {
    router.route("/api/")
        .handler(rc -> rc.response().end(new JsonObject().put("desde", nombreServicio).toString()));
  }

  private void agregarConsultaServicios() {
    router.route("/servicio/:nombre")
        .handler(rc -> cc.obtenerServicio(rc.request().getParam("nombre"), c -> {
          if (c.isPresent()) {
            final HttpClient client = c.get().getAs(HttpClient.class);
            client.getNow("/api/", response -> {
              response.handler(b -> {
                c.get().release();
                rc.response().end(b.toString());
              });
            });

          }
          /*
           * client.getNow("/", response -> { response.handler(b -> {
           * rc.response().end(b.toString()); }); });
           */
          else {
            rc.response().end("{}");
          }

        }));
  }

  public static void main(String[] args) {
    new VertxKeycloak().lanzar();
  }
}

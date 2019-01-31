package com.sebastian.vertx;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;


/**
 * test para los recursos de la clase {@link VertxRecursos}}.
 *
 * https://vertx.io/docs/vertx-junit5/java/
 *
 * @author Sebastian Avila A.
 */
@ExtendWith(VertxExtension.class)
public class VertxKeycloakTest extends AbstractVerticle {
  @Override
  public void start() throws Exception {
    final Router router = Router.router(vertx);
    new VertxRecursos().agregarRecursos(router);
    vertx.createHttpServer().requestHandler(router).listen(8080);
  }

  @BeforeEach
  void deployVerticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new VertxKeycloakTest(), testContext.completing());
  }

  @Test
  void noTieneQueMostrarInformacionDelSecreto(Vertx vertx, VertxTestContext testContext) {
    final WebClient client = WebClient.create(vertx);
    client.get(8080, "localhost", "/resources/secure").as(BodyCodec.string())
        .send(testContext.succeeding(response -> testContext.verify(() -> {
          Assertions.assertThat(response.body()).isEqualTo("hola desde el recurso protegido, ");
          testContext.completeNow();
        })));
  }

  @Test
  void tieneQueSaludarme(Vertx vertx, VertxTestContext testContext) {
    final WebClient client = WebClient.create(vertx);
    client.get(8080, "localhost", "/saludo").as(BodyCodec.string())
        .send(testContext.succeeding(response -> testContext.verify(() -> {
          Assertions.assertThat(response.body()).isEqualTo("hola");
          testContext.completeNow();
        })));
  }
}



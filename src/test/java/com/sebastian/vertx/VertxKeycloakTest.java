package com.sebastian.vertx;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;


/**
 * como hacer test con vertx????.
 *
 * https://vertx.io/docs/vertx-junit5/java/
 *
 * @author Sebastian Avila A.
 */
@ExtendWith(VertxExtension.class)
public class VertxKeycloakTest {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new HttpServerVerticle(), testContext.completing());
  }

  @Test
  void http_server_check_response(Vertx vertx, VertxTestContext testContext) {
    final WebClient client = WebClient.create(vertx);
    client.get(8080, "localhost", "/").as(BodyCodec.string())
        .send(testContext.succeeding(response -> testContext.verify(() -> {
          Assertions.assertThat(response.body()).isEqualTo("hola");
          testContext.completeNow();
        })));
  }
}

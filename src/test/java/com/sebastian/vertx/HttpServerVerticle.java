package com.sebastian.vertx;

import io.vertx.core.AbstractVerticle;

public class HttpServerVerticle extends AbstractVerticle {
  @Override
  public void start() throws Exception {
    vertx.createHttpServer().requestHandler(req -> {
      req.response().putHeader("content-type", "text/html").end("hola");
    }).listen(8080);
  }
}

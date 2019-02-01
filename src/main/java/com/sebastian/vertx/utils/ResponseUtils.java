package com.sebastian.vertx.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * metodos utiles para las respuestas a los clientes.
 *
 * @author Sebastian Avila A.
 *
 */
public final class ResponseUtils {
  private static final Logger LOGGER = Logger.getLogger(ResponseUtils.class.getCanonicalName());

  private static final ObjectMapper om = new ObjectMapper();

  private ResponseUtils() {}

  public static <T> String generarJson(final T data) {
    var json = "";
    try {
      json = om.writeValueAsString(data);
    } catch (final JsonProcessingException e) {
      LOGGER.log(Level.WARNING, "error al convertir a json", e);
    }
    return json;
  }
}

package com.sebastian.vertx.clientes.redis;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * datos requeridos para realizar el registro del servicio.
 *
 * @author Sebastian Avila A.
 *
 */
@Data
@AllArgsConstructor
@ToString
public class RegistroServicio {
  private final String nombre;
  private final String id;
  private final List<String> tags;
  private final int segundosTtl;
  private final String host;
  private final int puerto;
}

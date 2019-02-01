package com.sebastian.vertx.clientes.consul;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * datos requeridos para realizar el registro del servicio en consul.
 *
 * @author Sebastian Avila A.
 *
 */
@Data
@AllArgsConstructor
@ToString
public class RegistroServicioConsul {
  private final String nombre;
  private final String id;
  private final List<String> tags;
  private final int segundosTtl;
  private final String host;
  private final int puerto;
}

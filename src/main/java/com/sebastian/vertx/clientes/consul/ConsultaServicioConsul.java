package com.sebastian.vertx.clientes.consul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * representa la respuesta de Consul al consultar un servicio.
 *
 * @author Sebastian Avila A.
 *
 */
@ToString
@Data
@AllArgsConstructor
public class ConsultaServicioConsul {
  private final String nombre;
  private final String nodo;
  private final String host;
  private final int puerto;
}

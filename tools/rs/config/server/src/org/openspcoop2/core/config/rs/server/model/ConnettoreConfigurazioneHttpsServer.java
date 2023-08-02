/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.core.config.rs.server.model;

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ConnettoreConfigurazioneHttpsServer extends BaseTrustStore {
  
  @Schema(required = true, description = "")
  private KeystoreEnum truststoreTipo = null;
  
  @Schema(example = "PKIX", description = "")
  private String algoritmo = "PKIX";
  
  @Schema(description = "")
  private String pcks11Tipo = null;
 /**
   * Get truststoreTipo
   * @return truststoreTipo
  **/
  @JsonProperty("truststore_tipo")
  @NotNull
  @Valid
  public KeystoreEnum getTruststoreTipo() {
    return this.truststoreTipo;
  }

  public void setTruststoreTipo(KeystoreEnum truststoreTipo) {
    this.truststoreTipo = truststoreTipo;
  }

  public ConnettoreConfigurazioneHttpsServer truststoreTipo(KeystoreEnum truststoreTipo) {
    this.truststoreTipo = truststoreTipo;
    return this;
  }

 /**
   * Get algoritmo
   * @return algoritmo
  **/
  @JsonProperty("algoritmo")
  @Valid
 @Size(max=255)  public String getAlgoritmo() {
    return this.algoritmo;
  }

  public void setAlgoritmo(String algoritmo) {
    this.algoritmo = algoritmo;
  }

  public ConnettoreConfigurazioneHttpsServer algoritmo(String algoritmo) {
    this.algoritmo = algoritmo;
    return this;
  }

 /**
   * Get pcks11Tipo
   * @return pcks11Tipo
  **/
  @JsonProperty("pcks11_tipo")
  @Valid
 @Size(max=255)  public String getPcks11Tipo() {
    return this.pcks11Tipo;
  }

  public void setPcks11Tipo(String pcks11Tipo) {
    this.pcks11Tipo = pcks11Tipo;
  }

  public ConnettoreConfigurazioneHttpsServer pcks11Tipo(String pcks11Tipo) {
    this.pcks11Tipo = pcks11Tipo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreConfigurazioneHttpsServer {\n");
    sb.append("    ").append(ConnettoreConfigurazioneHttpsServer.toIndentedString(super.toString())).append("\n");
    sb.append("    truststoreTipo: ").append(ConnettoreConfigurazioneHttpsServer.toIndentedString(this.truststoreTipo)).append("\n");
    sb.append("    algoritmo: ").append(ConnettoreConfigurazioneHttpsServer.toIndentedString(this.algoritmo)).append("\n");
    sb.append("    pcks11Tipo: ").append(ConnettoreConfigurazioneHttpsServer.toIndentedString(this.pcks11Tipo)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private static String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

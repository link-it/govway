/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

/**
  * URL di invocazione dell'erogazione dell'API di callback
 **/
@Schema(description="URL di invocazione dell'erogazione dell'API di callback")
public class FruizioneModIScambioAsincronoUrlCallbackErogazione  implements OneOfFruizioneModIScambioAsincronoUrlCallback {
  
  @Schema(required = true, description = "")
  private ModIScambioAsincronoUrlCallbackSorgenteEnum sorgente = null;
 /**
   * Get sorgente
   * @return sorgente
  **/
  @Override
@JsonProperty("sorgente")
  @NotNull
  @Valid
  public ModIScambioAsincronoUrlCallbackSorgenteEnum getSorgente() {
    return this.sorgente;
  }

  public void setSorgente(ModIScambioAsincronoUrlCallbackSorgenteEnum sorgente) {
    this.sorgente = sorgente;
  }

  public FruizioneModIScambioAsincronoUrlCallbackErogazione sorgente(ModIScambioAsincronoUrlCallbackSorgenteEnum sorgente) {
    this.sorgente = sorgente;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FruizioneModIScambioAsincronoUrlCallbackErogazione {\n");
    
    sb.append("    sorgente: ").append(FruizioneModIScambioAsincronoUrlCallbackErogazione.toIndentedString(this.sorgente)).append("\n");
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

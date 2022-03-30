/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ApiModI  {
  
  @Schema(required = true, description = "")
  private ApiModISicurezzaCanale sicurezzaCanale = null;
  
  @Schema(required = true, description = "")
  private ApiModISicurezzaMessaggio sicurezzaMessaggio = null;
 /**
   * Get sicurezzaCanale
   * @return sicurezzaCanale
  **/
  @JsonProperty("sicurezza_canale")
  @NotNull
  @Valid
  public ApiModISicurezzaCanale getSicurezzaCanale() {
    return this.sicurezzaCanale;
  }

  public void setSicurezzaCanale(ApiModISicurezzaCanale sicurezzaCanale) {
    this.sicurezzaCanale = sicurezzaCanale;
  }

  public ApiModI sicurezzaCanale(ApiModISicurezzaCanale sicurezzaCanale) {
    this.sicurezzaCanale = sicurezzaCanale;
    return this;
  }

 /**
   * Get sicurezzaMessaggio
   * @return sicurezzaMessaggio
  **/
  @JsonProperty("sicurezza_messaggio")
  @NotNull
  @Valid
  public ApiModISicurezzaMessaggio getSicurezzaMessaggio() {
    return this.sicurezzaMessaggio;
  }

  public void setSicurezzaMessaggio(ApiModISicurezzaMessaggio sicurezzaMessaggio) {
    this.sicurezzaMessaggio = sicurezzaMessaggio;
  }

  public ApiModI sicurezzaMessaggio(ApiModISicurezzaMessaggio sicurezzaMessaggio) {
    this.sicurezzaMessaggio = sicurezzaMessaggio;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiModI {\n");
    
    sb.append("    sicurezzaCanale: ").append(ApiModI.toIndentedString(this.sicurezzaCanale)).append("\n");
    sb.append("    sicurezzaMessaggio: ").append(ApiModI.toIndentedString(this.sicurezzaMessaggio)).append("\n");
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

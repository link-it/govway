/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

public class ConfigurazioneApiCanale  {
  
  @Schema(required = true, description = "")
  private ConfigurazioneCanaleEnum configurazione = null;
  
  @Schema(description = "")
  private String canale = null;
 /**
   * Get configurazione
   * @return configurazione
  **/
  @JsonProperty("configurazione")
  @NotNull
  @Valid
  public ConfigurazioneCanaleEnum getConfigurazione() {
    return this.configurazione;
  }

  public void setConfigurazione(ConfigurazioneCanaleEnum configurazione) {
    this.configurazione = configurazione;
  }

  public ConfigurazioneApiCanale configurazione(ConfigurazioneCanaleEnum configurazione) {
    this.configurazione = configurazione;
    return this;
  }

 /**
   * Get canale
   * @return canale
  **/
  @JsonProperty("canale")
  @Valid
 @Pattern(regexp="^[^\\s]+$") @Size(max=255)  public String getCanale() {
    return this.canale;
  }

  public void setCanale(String canale) {
    this.canale = canale;
  }

  public ConfigurazioneApiCanale canale(String canale) {
    this.canale = canale;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConfigurazioneApiCanale {\n");
    
    sb.append("    configurazione: ").append(ConfigurazioneApiCanale.toIndentedString(this.configurazione)).append("\n");
    sb.append("    canale: ").append(ConfigurazioneApiCanale.toIndentedString(this.canale)).append("\n");
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

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

public class FruizioneModISoapRichiesta  {
  
  @Schema(required = true, description = "")
  private FruizioneModISoapRichiestaSicurezzaMessaggio sicurezzaMessaggio = null;
  
  @Schema(description = "")
  private BaseFruizioneModIOAuth oauth = null;
 /**
   * Get sicurezzaMessaggio
   * @return sicurezzaMessaggio
  **/
  @JsonProperty("sicurezza_messaggio")
  @NotNull
  @Valid
  public FruizioneModISoapRichiestaSicurezzaMessaggio getSicurezzaMessaggio() {
    return this.sicurezzaMessaggio;
  }

  public void setSicurezzaMessaggio(FruizioneModISoapRichiestaSicurezzaMessaggio sicurezzaMessaggio) {
    this.sicurezzaMessaggio = sicurezzaMessaggio;
  }

  public FruizioneModISoapRichiesta sicurezzaMessaggio(FruizioneModISoapRichiestaSicurezzaMessaggio sicurezzaMessaggio) {
    this.sicurezzaMessaggio = sicurezzaMessaggio;
    return this;
  }

 /**
   * Get oauth
   * @return oauth
  **/
  @JsonProperty("oauth")
  @Valid
  public BaseFruizioneModIOAuth getOauth() {
    return this.oauth;
  }

  public void setOauth(BaseFruizioneModIOAuth oauth) {
    this.oauth = oauth;
  }

  public FruizioneModISoapRichiesta oauth(BaseFruizioneModIOAuth oauth) {
    this.oauth = oauth;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FruizioneModISoapRichiesta {\n");
    
    sb.append("    sicurezzaMessaggio: ").append(FruizioneModISoapRichiesta.toIndentedString(this.sicurezzaMessaggio)).append("\n");
    sb.append("    oauth: ").append(FruizioneModISoapRichiesta.toIndentedString(this.oauth)).append("\n");
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

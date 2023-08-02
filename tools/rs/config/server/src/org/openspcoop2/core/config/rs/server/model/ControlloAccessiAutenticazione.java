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

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ControlloAccessiAutenticazione extends ApiImplConfigurazioneStato {
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "tipo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = APIImplAutenticazioneDisabilitata.class, name = "disabilitato"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = APIImplAutenticazioneBasic.class, name = "http-basic"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = APIImplAutenticazioneHttps.class, name = "https"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = APIImplAutenticazionePrincipal.class, name = "principal"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = APIImplAutenticazioneApiKey.class, name = "api-key"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = APIImplAutenticazioneCustom.class, name = "custom")  })
  private OneOfControlloAccessiAutenticazioneAutenticazione autenticazione = null;
  
  @Schema(description = "")
  private ControlloAccessiAutenticazioneToken token = null;
 /**
   * Get autenticazione
   * @return autenticazione
  **/
  @JsonProperty("autenticazione")
  @Valid
  public OneOfControlloAccessiAutenticazioneAutenticazione getAutenticazione() {
    return this.autenticazione;
  }

  public void setAutenticazione(OneOfControlloAccessiAutenticazioneAutenticazione autenticazione) {
    this.autenticazione = autenticazione;
  }

  public ControlloAccessiAutenticazione autenticazione(OneOfControlloAccessiAutenticazioneAutenticazione autenticazione) {
    this.autenticazione = autenticazione;
    return this;
  }

 /**
   * Get token
   * @return token
  **/
  @JsonProperty("token")
  @Valid
  public ControlloAccessiAutenticazioneToken getToken() {
    return this.token;
  }

  public void setToken(ControlloAccessiAutenticazioneToken token) {
    this.token = token;
  }

  public ControlloAccessiAutenticazione token(ControlloAccessiAutenticazioneToken token) {
    this.token = token;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ControlloAccessiAutenticazione {\n");
    sb.append("    ").append(ControlloAccessiAutenticazione.toIndentedString(super.toString())).append("\n");
    sb.append("    autenticazione: ").append(ControlloAccessiAutenticazione.toIndentedString(this.autenticazione)).append("\n");
    sb.append("    token: ").append(ControlloAccessiAutenticazione.toIndentedString(this.token)).append("\n");
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

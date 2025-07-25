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

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ApiModIAzioneSoap  {
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "")
  private ApiModIPatternInterazioneSoap interazione = null;
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "stato", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ApiModISicurezzaMessaggioOperazione.class, name = "api"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ApiModISicurezzaMessaggioOperazioneRidefinito.class, name = "ridefinito")  })
  private OneOfApiModIAzioneSoapSicurezzaMessaggio sicurezzaMessaggio = null;
 /**
   * Get interazione
   * @return interazione
  **/
  @JsonProperty("interazione")
  @NotNull
  @Valid
  public ApiModIPatternInterazioneSoap getInterazione() {
    return this.interazione;
  }

  public void setInterazione(ApiModIPatternInterazioneSoap interazione) {
    this.interazione = interazione;
  }

  public ApiModIAzioneSoap interazione(ApiModIPatternInterazioneSoap interazione) {
    this.interazione = interazione;
    return this;
  }

 /**
   * Get sicurezzaMessaggio
   * @return sicurezzaMessaggio
  **/
  @JsonProperty("sicurezza_messaggio")
  @NotNull
  @Valid
  public OneOfApiModIAzioneSoapSicurezzaMessaggio getSicurezzaMessaggio() {
    return this.sicurezzaMessaggio;
  }

  public void setSicurezzaMessaggio(OneOfApiModIAzioneSoapSicurezzaMessaggio sicurezzaMessaggio) {
    this.sicurezzaMessaggio = sicurezzaMessaggio;
  }

  public ApiModIAzioneSoap sicurezzaMessaggio(OneOfApiModIAzioneSoapSicurezzaMessaggio sicurezzaMessaggio) {
    this.sicurezzaMessaggio = sicurezzaMessaggio;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiModIAzioneSoap {\n");
    
    sb.append("    interazione: ").append(ApiModIAzioneSoap.toIndentedString(this.interazione)).append("\n");
    sb.append("    sicurezzaMessaggio: ").append(ApiModIAzioneSoap.toIndentedString(this.sicurezzaMessaggio)).append("\n");
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

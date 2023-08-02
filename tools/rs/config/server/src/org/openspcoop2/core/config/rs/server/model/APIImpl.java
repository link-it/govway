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

public class APIImpl extends APIBaseImpl {
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "tipo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = APIImplAutenticazioneDisabilitata.class, name = "disabilitato"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = APIImplAutenticazioneBasic.class, name = "http-basic"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = APIImplAutenticazioneHttps.class, name = "https"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = APIImplAutenticazionePrincipal.class, name = "principal"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = APIImplAutenticazioneApiKey.class, name = "api-key")  })
  private OneOfAPIImplAutenticazione autenticazione = null;
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "tipo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = APIImplAutorizzazioneDisabilitata.class, name = "disabilitato"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = APIImplAutorizzazioneAbilitataNew.class, name = "abilitato"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = APIImplAutorizzazioneXACML.class, name = "xacml-Policy")  })
  private OneOfAPIImplAutorizzazione autorizzazione = null;
  
  @Schema(required = true, description = "")
  private BaseConnettoreHttp connettore = null;
 /**
   * Get autenticazione
   * @return autenticazione
  **/
  @JsonProperty("autenticazione")
  @Valid
  public OneOfAPIImplAutenticazione getAutenticazione() {
    return this.autenticazione;
  }

  public void setAutenticazione(OneOfAPIImplAutenticazione autenticazione) {
    this.autenticazione = autenticazione;
  }

  public APIImpl autenticazione(OneOfAPIImplAutenticazione autenticazione) {
    this.autenticazione = autenticazione;
    return this;
  }

 /**
   * Get autorizzazione
   * @return autorizzazione
  **/
  @JsonProperty("autorizzazione")
  @Valid
  public OneOfAPIImplAutorizzazione getAutorizzazione() {
    return this.autorizzazione;
  }

  public void setAutorizzazione(OneOfAPIImplAutorizzazione autorizzazione) {
    this.autorizzazione = autorizzazione;
  }

  public APIImpl autorizzazione(OneOfAPIImplAutorizzazione autorizzazione) {
    this.autorizzazione = autorizzazione;
    return this;
  }

 /**
   * Get connettore
   * @return connettore
  **/
  @JsonProperty("connettore")
  @NotNull
  @Valid
  public BaseConnettoreHttp getConnettore() {
    return this.connettore;
  }

  public void setConnettore(BaseConnettoreHttp connettore) {
    this.connettore = connettore;
  }

  public APIImpl connettore(BaseConnettoreHttp connettore) {
    this.connettore = connettore;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImpl {\n");
    sb.append("    ").append(APIImpl.toIndentedString(super.toString())).append("\n");
    sb.append("    autenticazione: ").append(APIImpl.toIndentedString(this.autenticazione)).append("\n");
    sb.append("    autorizzazione: ").append(APIImpl.toIndentedString(this.autorizzazione)).append("\n");
    sb.append("    connettore: ").append(APIImpl.toIndentedString(this.connettore)).append("\n");
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

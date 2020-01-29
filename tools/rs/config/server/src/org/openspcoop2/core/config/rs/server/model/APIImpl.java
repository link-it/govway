/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 *
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.rs.server.model.APIBaseImpl;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazioneNew;
import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazioneNew;
import org.openspcoop2.core.config.rs.server.model.Connettore;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class APIImpl extends APIBaseImpl {
  
  @Schema(description = "")
  private APIImplAutenticazioneNew autenticazione = null;
  
  @Schema(description = "")
  private APIImplAutorizzazioneNew autorizzazione = null;
  
  @Schema(required = true, description = "")
  private Connettore connettore = null;
 /**
   * Get autenticazione
   * @return autenticazione
  **/
  @JsonProperty("autenticazione")
  @Valid
  public APIImplAutenticazioneNew getAutenticazione() {
    return this.autenticazione;
  }

  public void setAutenticazione(APIImplAutenticazioneNew autenticazione) {
    this.autenticazione = autenticazione;
  }

  public APIImpl autenticazione(APIImplAutenticazioneNew autenticazione) {
    this.autenticazione = autenticazione;
    return this;
  }

 /**
   * Get autorizzazione
   * @return autorizzazione
  **/
  @JsonProperty("autorizzazione")
  @Valid
  public APIImplAutorizzazioneNew getAutorizzazione() {
    return this.autorizzazione;
  }

  public void setAutorizzazione(APIImplAutorizzazioneNew autorizzazione) {
    this.autorizzazione = autorizzazione;
  }

  public APIImpl autorizzazione(APIImplAutorizzazioneNew autorizzazione) {
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
  public Connettore getConnettore() {
    return this.connettore;
  }

  public void setConnettore(Connettore connettore) {
    this.connettore = connettore;
  }

  public APIImpl connettore(Connettore connettore) {
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

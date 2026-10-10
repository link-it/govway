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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ApiModIScambioAsincronoCallback  implements OneOfApiModIScambioAsincrono {
  
  @Schema(required = true, description = "")
  private ModIScambioAsincronoRuoloEnum ruolo = null;
  
  @Schema(required = true, description = "")
  private ApiModIScambioAsincronoApiErogazioneDati apiErogazioneDati = null;
 /**
   * Get ruolo
   * @return ruolo
  **/
  @Override
@JsonProperty("ruolo")
  @NotNull
  @Valid
  public ModIScambioAsincronoRuoloEnum getRuolo() {
    return this.ruolo;
  }

  public void setRuolo(ModIScambioAsincronoRuoloEnum ruolo) {
    this.ruolo = ruolo;
  }

  public ApiModIScambioAsincronoCallback ruolo(ModIScambioAsincronoRuoloEnum ruolo) {
    this.ruolo = ruolo;
    return this;
  }

 /**
   * Get apiErogazioneDati
   * @return apiErogazioneDati
  **/
  @JsonProperty("api_erogazione_dati")
  @NotNull
  @Valid
  public ApiModIScambioAsincronoApiErogazioneDati getApiErogazioneDati() {
    return this.apiErogazioneDati;
  }

  public void setApiErogazioneDati(ApiModIScambioAsincronoApiErogazioneDati apiErogazioneDati) {
    this.apiErogazioneDati = apiErogazioneDati;
  }

  public ApiModIScambioAsincronoCallback apiErogazioneDati(ApiModIScambioAsincronoApiErogazioneDati apiErogazioneDati) {
    this.apiErogazioneDati = apiErogazioneDati;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiModIScambioAsincronoCallback {\n");
    
    sb.append("    ruolo: ").append(ApiModIScambioAsincronoCallback.toIndentedString(this.ruolo)).append("\n");
    sb.append("    apiErogazioneDati: ").append(ApiModIScambioAsincronoCallback.toIndentedString(this.apiErogazioneDati)).append("\n");
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

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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ApiModISicurezzaMessaggioOperazioneRidefinito  implements OneOfApiModIAzioneSoapSicurezzaMessaggio, OneOfApiModIRisorsaRestSicurezzaMessaggio {
  
  @Schema(required = true, description = "")
  private ModISicurezzaMessaggioOperazioneEnum stato = null;
  
  @Schema(required = true, description = "")
  private ApiModISicurezzaMessaggio configurazione = null;
 /**
   * Get stato
   * @return stato
  **/
  @Override
@JsonProperty("stato")
  @NotNull
  @Valid
  public ModISicurezzaMessaggioOperazioneEnum getStato() {
    return this.stato;
  }

  public void setStato(ModISicurezzaMessaggioOperazioneEnum stato) {
    this.stato = stato;
  }

  public ApiModISicurezzaMessaggioOperazioneRidefinito stato(ModISicurezzaMessaggioOperazioneEnum stato) {
    this.stato = stato;
    return this;
  }

 /**
   * Get configurazione
   * @return configurazione
  **/
  @JsonProperty("configurazione")
  @NotNull
  @Valid
  public ApiModISicurezzaMessaggio getConfigurazione() {
    return this.configurazione;
  }

  public void setConfigurazione(ApiModISicurezzaMessaggio configurazione) {
    this.configurazione = configurazione;
  }

  public ApiModISicurezzaMessaggioOperazioneRidefinito configurazione(ApiModISicurezzaMessaggio configurazione) {
    this.configurazione = configurazione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiModISicurezzaMessaggioOperazioneRidefinito {\n");
    
    sb.append("    stato: ").append(ApiModISicurezzaMessaggioOperazioneRidefinito.toIndentedString(this.stato)).append("\n");
    sb.append("    configurazione: ").append(ApiModISicurezzaMessaggioOperazioneRidefinito.toIndentedString(this.configurazione)).append("\n");
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

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

public class RateLimitingPolicyCriteri  implements OneOfRateLimitingPolicyBaseConIdentificazioneConfigurazione {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private RateLimitingIdentificazionePolicyEnum identificazione = null;
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private RateLimitingCriteriMetricaEnum metrica = null;
  
  @Schema(description = "")
  private RateLimitingCriteriIntervalloEnum intervallo = null;
  
  @Schema(example = "false", description = "")
  private Boolean congestione = false;
  
  @Schema(example = "false", description = "")
  private Boolean degrado = false;
 /**
   * Get identificazione
   * @return identificazione
  **/
  @Override
@JsonProperty("identificazione")
  @NotNull
  @Valid
  public RateLimitingIdentificazionePolicyEnum getIdentificazione() {
    return this.identificazione;
  }

  public void setIdentificazione(RateLimitingIdentificazionePolicyEnum identificazione) {
    this.identificazione = identificazione;
  }

  public RateLimitingPolicyCriteri identificazione(RateLimitingIdentificazionePolicyEnum identificazione) {
    this.identificazione = identificazione;
    return this;
  }

 /**
   * Get metrica
   * @return metrica
  **/
  @JsonProperty("metrica")
  @NotNull
  @Valid
  public RateLimitingCriteriMetricaEnum getMetrica() {
    return this.metrica;
  }

  public void setMetrica(RateLimitingCriteriMetricaEnum metrica) {
    this.metrica = metrica;
  }

  public RateLimitingPolicyCriteri metrica(RateLimitingCriteriMetricaEnum metrica) {
    this.metrica = metrica;
    return this;
  }

 /**
   * Get intervallo
   * @return intervallo
  **/
  @JsonProperty("intervallo")
  @Valid
  public RateLimitingCriteriIntervalloEnum getIntervallo() {
    return this.intervallo;
  }

  public void setIntervallo(RateLimitingCriteriIntervalloEnum intervallo) {
    this.intervallo = intervallo;
  }

  public RateLimitingPolicyCriteri intervallo(RateLimitingCriteriIntervalloEnum intervallo) {
    this.intervallo = intervallo;
    return this;
  }

 /**
   * Get congestione
   * @return congestione
  **/
  @JsonProperty("congestione")
  @Valid
  public Boolean isCongestione() {
    return this.congestione;
  }

  public void setCongestione(Boolean congestione) {
    this.congestione = congestione;
  }

  public RateLimitingPolicyCriteri congestione(Boolean congestione) {
    this.congestione = congestione;
    return this;
  }

 /**
   * Get degrado
   * @return degrado
  **/
  @JsonProperty("degrado")
  @Valid
  public Boolean isDegrado() {
    return this.degrado;
  }

  public void setDegrado(Boolean degrado) {
    this.degrado = degrado;
  }

  public RateLimitingPolicyCriteri degrado(Boolean degrado) {
    this.degrado = degrado;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateLimitingPolicyCriteri {\n");
    
    sb.append("    identificazione: ").append(RateLimitingPolicyCriteri.toIndentedString(this.identificazione)).append("\n");
    sb.append("    metrica: ").append(RateLimitingPolicyCriteri.toIndentedString(this.metrica)).append("\n");
    sb.append("    intervallo: ").append(RateLimitingPolicyCriteri.toIndentedString(this.intervallo)).append("\n");
    sb.append("    congestione: ").append(RateLimitingPolicyCriteri.toIndentedString(this.congestione)).append("\n");
    sb.append("    degrado: ").append(RateLimitingPolicyCriteri.toIndentedString(this.degrado)).append("\n");
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

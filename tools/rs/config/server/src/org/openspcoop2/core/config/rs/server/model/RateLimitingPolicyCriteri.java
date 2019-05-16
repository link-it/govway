/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.core.config.rs.server.model.RateLimitingCriteriIntervalloEnum;
import org.openspcoop2.core.config.rs.server.model.RateLimitingCriteriRisorsaEnum;
import org.openspcoop2.core.config.rs.server.model.RateLimitingCriteriRisorsaEsitiEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RateLimitingPolicyCriteri  {
  
  @Schema(required = true, description = "")
  private RateLimitingCriteriRisorsaEnum risorsa = null;
  
  @Schema(description = "")
  private RateLimitingCriteriRisorsaEsitiEnum esiti = null;
  
  @Schema(description = "")
  private RateLimitingCriteriIntervalloEnum intervallo = null;
  
  @Schema(description = "")
  private Boolean congestione = false;
  
  @Schema(description = "")
  private Boolean degrado = false;
 /**
   * Get risorsa
   * @return risorsa
  **/
  @JsonProperty("risorsa")
  @NotNull
  @Valid
  public RateLimitingCriteriRisorsaEnum getRisorsa() {
    return this.risorsa;
  }

  public void setRisorsa(RateLimitingCriteriRisorsaEnum risorsa) {
    this.risorsa = risorsa;
  }

  public RateLimitingPolicyCriteri risorsa(RateLimitingCriteriRisorsaEnum risorsa) {
    this.risorsa = risorsa;
    return this;
  }

 /**
   * Get esiti
   * @return esiti
  **/
  @JsonProperty("esiti")
  @Valid
  public RateLimitingCriteriRisorsaEsitiEnum getEsiti() {
    return this.esiti;
  }

  public void setEsiti(RateLimitingCriteriRisorsaEsitiEnum esiti) {
    this.esiti = esiti;
  }

  public RateLimitingPolicyCriteri esiti(RateLimitingCriteriRisorsaEsitiEnum esiti) {
    this.esiti = esiti;
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
    
    sb.append("    risorsa: ").append(RateLimitingPolicyCriteri.toIndentedString(this.risorsa)).append("\n");
    sb.append("    esiti: ").append(RateLimitingPolicyCriteri.toIndentedString(this.esiti)).append("\n");
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

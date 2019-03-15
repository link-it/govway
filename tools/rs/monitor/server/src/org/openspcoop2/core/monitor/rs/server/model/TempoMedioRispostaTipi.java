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
package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class TempoMedioRispostaTipi  {
  
  @Schema(description = "")
  private Boolean latenzaTotale = true;
  
  @Schema(description = "")
  private Boolean latenzaServizio = false;
  
  @Schema(description = "")
  private Boolean latenzaGateway = false;
 /**
   * Get latenzaTotale
   * @return latenzaTotale
  **/
  @JsonProperty("latenza_totale")
  @Valid
  public Boolean isLatenzaTotale() {
    return this.latenzaTotale;
  }

  public void setLatenzaTotale(Boolean latenzaTotale) {
    this.latenzaTotale = latenzaTotale;
  }

  public TempoMedioRispostaTipi latenzaTotale(Boolean latenzaTotale) {
    this.latenzaTotale = latenzaTotale;
    return this;
  }

 /**
   * Get latenzaServizio
   * @return latenzaServizio
  **/
  @JsonProperty("latenza_servizio")
  @Valid
  public Boolean isLatenzaServizio() {
    return this.latenzaServizio;
  }

  public void setLatenzaServizio(Boolean latenzaServizio) {
    this.latenzaServizio = latenzaServizio;
  }

  public TempoMedioRispostaTipi latenzaServizio(Boolean latenzaServizio) {
    this.latenzaServizio = latenzaServizio;
    return this;
  }

 /**
   * Get latenzaGateway
   * @return latenzaGateway
  **/
  @JsonProperty("latenza_gateway")
  @Valid
  public Boolean isLatenzaGateway() {
    return this.latenzaGateway;
  }

  public void setLatenzaGateway(Boolean latenzaGateway) {
    this.latenzaGateway = latenzaGateway;
  }

  public TempoMedioRispostaTipi latenzaGateway(Boolean latenzaGateway) {
    this.latenzaGateway = latenzaGateway;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TempoMedioRispostaTipi {\n");
    
    sb.append("    latenzaTotale: ").append(TempoMedioRispostaTipi.toIndentedString(this.latenzaTotale)).append("\n");
    sb.append("    latenzaServizio: ").append(TempoMedioRispostaTipi.toIndentedString(this.latenzaServizio)).append("\n");
    sb.append("    latenzaGateway: ").append(TempoMedioRispostaTipi.toIndentedString(this.latenzaGateway)).append("\n");
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

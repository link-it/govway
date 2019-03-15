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

public class OccupazioneBandaTipi  {
  
  @Schema(description = "")
  private Boolean bandaComplessiva = true;
  
  @Schema(description = "")
  private Boolean bandaInterna = false;
  
  @Schema(description = "")
  private Boolean bandaEsterna = false;
 /**
   * Get bandaComplessiva
   * @return bandaComplessiva
  **/
  @JsonProperty("banda_complessiva")
  @Valid
  public Boolean isBandaComplessiva() {
    return this.bandaComplessiva;
  }

  public void setBandaComplessiva(Boolean bandaComplessiva) {
    this.bandaComplessiva = bandaComplessiva;
  }

  public OccupazioneBandaTipi bandaComplessiva(Boolean bandaComplessiva) {
    this.bandaComplessiva = bandaComplessiva;
    return this;
  }

 /**
   * Get bandaInterna
   * @return bandaInterna
  **/
  @JsonProperty("banda_interna")
  @Valid
  public Boolean isBandaInterna() {
    return this.bandaInterna;
  }

  public void setBandaInterna(Boolean bandaInterna) {
    this.bandaInterna = bandaInterna;
  }

  public OccupazioneBandaTipi bandaInterna(Boolean bandaInterna) {
    this.bandaInterna = bandaInterna;
    return this;
  }

 /**
   * Get bandaEsterna
   * @return bandaEsterna
  **/
  @JsonProperty("banda_esterna")
  @Valid
  public Boolean isBandaEsterna() {
    return this.bandaEsterna;
  }

  public void setBandaEsterna(Boolean bandaEsterna) {
    this.bandaEsterna = bandaEsterna;
  }

  public OccupazioneBandaTipi bandaEsterna(Boolean bandaEsterna) {
    this.bandaEsterna = bandaEsterna;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OccupazioneBandaTipi {\n");
    
    sb.append("    bandaComplessiva: ").append(OccupazioneBandaTipi.toIndentedString(this.bandaComplessiva)).append("\n");
    sb.append("    bandaInterna: ").append(OccupazioneBandaTipi.toIndentedString(this.bandaInterna)).append("\n");
    sb.append("    bandaEsterna: ").append(OccupazioneBandaTipi.toIndentedString(this.bandaEsterna)).append("\n");
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

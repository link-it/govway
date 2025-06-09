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
package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class DetailsTracingPDND extends BaseTracingPDND {
  
  @Schema(description = "")
  private MethodTracingPDND metodo = null;
  
  @Schema(description = "")
  private String dettagliErrore = null;
 /**
   * Get metodo
   * @return metodo
  **/
  @JsonProperty("metodo")
  @Valid
  public MethodTracingPDND getMetodo() {
    return this.metodo;
  }

  public void setMetodo(MethodTracingPDND metodo) {
    this.metodo = metodo;
  }

  public DetailsTracingPDND metodo(MethodTracingPDND metodo) {
    this.metodo = metodo;
    return this;
  }

 /**
   * Get dettagliErrore
   * @return dettagliErrore
  **/
  @JsonProperty("dettagli_errore")
  @Valid
  public String getDettagliErrore() {
    return this.dettagliErrore;
  }

  public void setDettagliErrore(String dettagliErrore) {
    this.dettagliErrore = dettagliErrore;
  }

  public DetailsTracingPDND dettagliErrore(String dettagliErrore) {
    this.dettagliErrore = dettagliErrore;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DetailsTracingPDND {\n");
    sb.append("    ").append(DetailsTracingPDND.toIndentedString(super.toString())).append("\n");
    sb.append("    metodo: ").append(DetailsTracingPDND.toIndentedString(this.metodo)).append("\n");
    sb.append("    dettagliErrore: ").append(DetailsTracingPDND.toIndentedString(this.dettagliErrore)).append("\n");
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

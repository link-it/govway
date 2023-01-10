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

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RateLimitingPolicyFruizione extends RateLimitingPolicyBaseConIdentificazione {
  
  @Schema(description = "")
  private RateLimitingPolicyFiltroFruizione filtro = null;
  
  @Schema(description = "")
  private RateLimitingPolicyGroupBy raggruppamento = null;
 /**
   * Get filtro
   * @return filtro
  **/
  @JsonProperty("filtro")
  @Valid
  public RateLimitingPolicyFiltroFruizione getFiltro() {
    return this.filtro;
  }

  public void setFiltro(RateLimitingPolicyFiltroFruizione filtro) {
    this.filtro = filtro;
  }

  public RateLimitingPolicyFruizione filtro(RateLimitingPolicyFiltroFruizione filtro) {
    this.filtro = filtro;
    return this;
  }

 /**
   * Get raggruppamento
   * @return raggruppamento
  **/
  @JsonProperty("raggruppamento")
  @Valid
  public RateLimitingPolicyGroupBy getRaggruppamento() {
    return this.raggruppamento;
  }

  public void setRaggruppamento(RateLimitingPolicyGroupBy raggruppamento) {
    this.raggruppamento = raggruppamento;
  }

  public RateLimitingPolicyFruizione raggruppamento(RateLimitingPolicyGroupBy raggruppamento) {
    this.raggruppamento = raggruppamento;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateLimitingPolicyFruizione {\n");
    sb.append("    ").append(RateLimitingPolicyFruizione.toIndentedString(super.toString())).append("\n");
    sb.append("    filtro: ").append(RateLimitingPolicyFruizione.toIndentedString(this.filtro)).append("\n");
    sb.append("    raggruppamento: ").append(RateLimitingPolicyFruizione.toIndentedString(this.raggruppamento)).append("\n");
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

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

public class RateLimitingPolicyErogazioneUpdate extends RateLimitingPolicyBase {
  
  @Schema(description = "")
  private RateLimitingPolicyFiltroErogazione filtro = null;
  
  @Schema(description = "")
  private RateLimitingPolicyGroupBy raggruppamento = null;
 /**
   * Get filtro
   * @return filtro
  **/
  @JsonProperty("filtro")
  @Valid
  public RateLimitingPolicyFiltroErogazione getFiltro() {
    return this.filtro;
  }

  public void setFiltro(RateLimitingPolicyFiltroErogazione filtro) {
    this.filtro = filtro;
  }

  public RateLimitingPolicyErogazioneUpdate filtro(RateLimitingPolicyFiltroErogazione filtro) {
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

  public RateLimitingPolicyErogazioneUpdate raggruppamento(RateLimitingPolicyGroupBy raggruppamento) {
    this.raggruppamento = raggruppamento;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateLimitingPolicyErogazioneUpdate {\n");
    sb.append("    ").append(RateLimitingPolicyErogazioneUpdate.toIndentedString(super.toString())).append("\n");
    sb.append("    filtro: ").append(RateLimitingPolicyErogazioneUpdate.toIndentedString(this.filtro)).append("\n");
    sb.append("    raggruppamento: ").append(RateLimitingPolicyErogazioneUpdate.toIndentedString(this.raggruppamento)).append("\n");
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

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

public class RateLimitingPolicyFiltroErogazione extends RateLimitingPolicyFiltro {
  
  @Schema(description = "")
  private String soggettoFruitore = null;
 /**
   * Get soggettoFruitore
   * @return soggettoFruitore
  **/
  @JsonProperty("soggetto_fruitore")
  @Valid
 @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255)  public String getSoggettoFruitore() {
    return this.soggettoFruitore;
  }

  public void setSoggettoFruitore(String soggettoFruitore) {
    this.soggettoFruitore = soggettoFruitore;
  }

  public RateLimitingPolicyFiltroErogazione soggettoFruitore(String soggettoFruitore) {
    this.soggettoFruitore = soggettoFruitore;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateLimitingPolicyFiltroErogazione {\n");
    sb.append("    ").append(RateLimitingPolicyFiltroErogazione.toIndentedString(super.toString())).append("\n");
    sb.append("    soggettoFruitore: ").append(RateLimitingPolicyFiltroErogazione.toIndentedString(this.soggettoFruitore)).append("\n");
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

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

public class GruppoItem extends GruppoBase {
  
  @Schema(required = true, description = "")
  private Boolean predefinito = null;
 /**
   * Get predefinito
   * @return predefinito
  **/
  @JsonProperty("predefinito")
  @NotNull
  @Valid
  public Boolean isPredefinito() {
    return this.predefinito;
  }

  public void setPredefinito(Boolean predefinito) {
    this.predefinito = predefinito;
  }

  public GruppoItem predefinito(Boolean predefinito) {
    this.predefinito = predefinito;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GruppoItem {\n");
    sb.append("    ").append(GruppoItem.toIndentedString(super.toString())).append("\n");
    sb.append("    predefinito: ").append(GruppoItem.toIndentedString(this.predefinito)).append("\n");
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

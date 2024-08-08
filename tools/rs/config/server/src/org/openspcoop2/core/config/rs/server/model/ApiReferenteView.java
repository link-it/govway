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

import org.openspcoop2.utils.service.beans.BaseItem;
import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ApiReferenteView extends BaseItem {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private String referente = null;
 /**
   * Get referente
   * @return referente
  **/
  @JsonProperty("referente")
  @NotNull
  @Valid
 @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255)  public String getReferente() {
    return this.referente;
  }

  public void setReferente(String referente) {
    this.referente = referente;
  }

  public ApiReferenteView referente(String referente) {
    this.referente = referente;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiReferenteView {\n");
    sb.append("    ").append(ApiReferenteView.toIndentedString(super.toString())).append("\n");
    sb.append("    referente: ").append(ApiReferenteView.toIndentedString(this.referente)).append("\n");
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

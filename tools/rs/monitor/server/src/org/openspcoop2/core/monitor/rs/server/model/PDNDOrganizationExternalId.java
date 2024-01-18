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
package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class PDNDOrganizationExternalId  {
  
  @Schema(description = "")
  private String codice = null;
  
  @Schema(description = "")
  private String origine = null;
 /**
   * Get codice
   * @return codice
  **/
  @JsonProperty("codice")
  @Valid
  public String getCodice() {
    return this.codice;
  }

  public void setCodice(String codice) {
    this.codice = codice;
  }

  public PDNDOrganizationExternalId codice(String codice) {
    this.codice = codice;
    return this;
  }

 /**
   * Get origine
   * @return origine
  **/
  @JsonProperty("origine")
  @Valid
  public String getOrigine() {
    return this.origine;
  }

  public void setOrigine(String origine) {
    this.origine = origine;
  }

  public PDNDOrganizationExternalId origine(String origine) {
    this.origine = origine;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PDNDOrganizationExternalId {\n");
    
    sb.append("    codice: ").append(PDNDOrganizationExternalId.toIndentedString(this.codice)).append("\n");
    sb.append("    origine: ").append(PDNDOrganizationExternalId.toIndentedString(this.origine)).append("\n");
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

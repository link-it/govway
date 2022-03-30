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

public class ApiInterfacciaRest  implements OneOfApiBaseTipoInterfaccia, OneOfApiInterfacciaViewTipoInterfaccia {
  
  @Schema(required = true, description = "")
  private TipoApiEnum protocollo = null;
  
  @Schema(required = true, description = "")
  private FormatoRestEnum formato = null;
 /**
   * Get protocollo
   * @return protocollo
  **/
  @Override
@JsonProperty("protocollo")
  @NotNull
  @Valid
  public TipoApiEnum getProtocollo() {
    return this.protocollo;
  }

  public void setProtocollo(TipoApiEnum protocollo) {
    this.protocollo = protocollo;
  }

  public ApiInterfacciaRest protocollo(TipoApiEnum protocollo) {
    this.protocollo = protocollo;
    return this;
  }

 /**
   * Get formato
   * @return formato
  **/
  @JsonProperty("formato")
  @NotNull
  @Valid
  public FormatoRestEnum getFormato() {
    return this.formato;
  }

  public void setFormato(FormatoRestEnum formato) {
    this.formato = formato;
  }

  public ApiInterfacciaRest formato(FormatoRestEnum formato) {
    this.formato = formato;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiInterfacciaRest {\n");
    
    sb.append("    protocollo: ").append(ApiInterfacciaRest.toIndentedString(this.protocollo)).append("\n");
    sb.append("    formato: ").append(ApiInterfacciaRest.toIndentedString(this.formato)).append("\n");
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

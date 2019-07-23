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
package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.utils.service.beans.BaseItem;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ApiInterfacciaView extends BaseItem {
  
  @Schema(required = true, description = "")
  private byte[] interfaccia = null;
  
  @Schema(required = true, description = "")
  private TipoApiEnum tipo = null;
  
  @Schema(example = "{\"formato\":\"OpenApi3.0\"}", required = true, description = "")
  private String formato = null;
 /**
   * Get interfaccia
   * @return interfaccia
  **/
  @JsonProperty("interfaccia")
  @NotNull
  @Valid
  public byte[] getInterfaccia() {
    return this.interfaccia;
  }

  public void setInterfaccia(byte[] interfaccia) {
    this.interfaccia = interfaccia;
  }

  public ApiInterfacciaView interfaccia(byte[] interfaccia) {
    this.interfaccia = interfaccia;
    return this;
  }

 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoApiEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoApiEnum tipo) {
    this.tipo = tipo;
  }

  public ApiInterfacciaView tipo(TipoApiEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get formato
   * @return formato
  **/
  @JsonProperty("formato")
  @NotNull
  @Valid
  public String getFormato() {
    return this.formato;
  }

  public void setFormato(String formato) {
    this.formato = formato;
  }

  public ApiInterfacciaView formato(String formato) {
    this.formato = formato;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiInterfacciaView {\n");
    sb.append("    ").append(ApiInterfacciaView.toIndentedString(super.toString())).append("\n");
    sb.append("    interfaccia: ").append(ApiInterfacciaView.toIndentedString(this.interfaccia)).append("\n");
    sb.append("    tipo: ").append(ApiInterfacciaView.toIndentedString(this.tipo)).append("\n");
    sb.append("    formato: ").append(ApiInterfacciaView.toIndentedString(this.formato)).append("\n");
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

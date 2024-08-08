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

public class ApiInterfacciaView extends BaseItem {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private byte[] interfaccia = null;
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "protocollo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ApiInterfacciaRest.class, name = "rest"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ApiInterfacciaSoap.class, name = "soap")  })
  private OneOfApiInterfacciaViewTipoInterfaccia tipoInterfaccia = null;
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
   * Get tipoInterfaccia
   * @return tipoInterfaccia
  **/
  @JsonProperty("tipo_interfaccia")
  @NotNull
  @Valid
  public OneOfApiInterfacciaViewTipoInterfaccia getTipoInterfaccia() {
    return this.tipoInterfaccia;
  }

  public void setTipoInterfaccia(OneOfApiInterfacciaViewTipoInterfaccia tipoInterfaccia) {
    this.tipoInterfaccia = tipoInterfaccia;
  }

  public ApiInterfacciaView tipoInterfaccia(OneOfApiInterfacciaViewTipoInterfaccia tipoInterfaccia) {
    this.tipoInterfaccia = tipoInterfaccia;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiInterfacciaView {\n");
    sb.append("    ").append(ApiInterfacciaView.toIndentedString(super.toString())).append("\n");
    sb.append("    interfaccia: ").append(ApiInterfacciaView.toIndentedString(this.interfaccia)).append("\n");
    sb.append("    tipoInterfaccia: ").append(ApiInterfacciaView.toIndentedString(this.tipoInterfaccia)).append("\n");
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

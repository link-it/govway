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
package org.openspcoop2.core.config.rs.server.model;

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ConnettoreApplicativoServer  implements OneOfApplicativoServerConnettore, OneOfConnettoreErogazioneConnettore {
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "")
  private ConnettoreEnum tipo = null;
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "")
  private String applicativo = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @Override
@JsonProperty("tipo")
  @NotNull
  @Valid
  public ConnettoreEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(ConnettoreEnum tipo) {
    this.tipo = tipo;
  }

  public ConnettoreApplicativoServer tipo(ConnettoreEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get applicativo
   * @return applicativo
  **/
  @JsonProperty("applicativo")
  @NotNull
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getApplicativo() {
    return this.applicativo;
  }

  public void setApplicativo(String applicativo) {
    this.applicativo = applicativo;
  }

  public ConnettoreApplicativoServer applicativo(String applicativo) {
    this.applicativo = applicativo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreApplicativoServer {\n");
    
    sb.append("    tipo: ").append(ConnettoreApplicativoServer.toIndentedString(this.tipo)).append("\n");
    sb.append("    applicativo: ").append(ConnettoreApplicativoServer.toIndentedString(this.applicativo)).append("\n");
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

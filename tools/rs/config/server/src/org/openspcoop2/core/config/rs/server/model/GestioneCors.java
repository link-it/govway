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

public class GestioneCors  {
  
  @Schema(required = true, description = "")
  private Boolean ridefinito = false;
  
  @Schema(description = "")
  private TipoGestioneCorsEnum tipo = null;
  
  @Schema(description = "")
  private GestioneCorsAccessControl accessControl = null;
 /**
   * Get ridefinito
   * @return ridefinito
  **/
  @JsonProperty("ridefinito")
  @NotNull
  @Valid
  public Boolean isRidefinito() {
    return this.ridefinito;
  }

  public void setRidefinito(Boolean ridefinito) {
    this.ridefinito = ridefinito;
  }

  public GestioneCors ridefinito(Boolean ridefinito) {
    this.ridefinito = ridefinito;
    return this;
  }

 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @Valid
  public TipoGestioneCorsEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoGestioneCorsEnum tipo) {
    this.tipo = tipo;
  }

  public GestioneCors tipo(TipoGestioneCorsEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get accessControl
   * @return accessControl
  **/
  @JsonProperty("access_control")
  @Valid
  public GestioneCorsAccessControl getAccessControl() {
    return this.accessControl;
  }

  public void setAccessControl(GestioneCorsAccessControl accessControl) {
    this.accessControl = accessControl;
  }

  public GestioneCors accessControl(GestioneCorsAccessControl accessControl) {
    this.accessControl = accessControl;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GestioneCors {\n");
    
    sb.append("    ridefinito: ").append(GestioneCors.toIndentedString(this.ridefinito)).append("\n");
    sb.append("    tipo: ").append(GestioneCors.toIndentedString(this.tipo)).append("\n");
    sb.append("    accessControl: ").append(GestioneCors.toIndentedString(this.accessControl)).append("\n");
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

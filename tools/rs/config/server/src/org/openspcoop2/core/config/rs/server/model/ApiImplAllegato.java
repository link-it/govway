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

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ApiImplAllegato  {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "ruolo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ApiImplAllegatoGenerico.class, name = "allegato"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ApiImplAllegatoSpecificaSemiformale.class, name = "specificaSemiFormale"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ApiImplAllegatoSpecificaSicurezza.class, name = "specificaSicurezza"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ApiImplAllegatoSpecificaLivelloServizio.class, name = "specificaLivelloServizio")  })
  private OneOfApiImplAllegatoAllegato allegato = null;
 /**
   * Get allegato
   * @return allegato
  **/
  @JsonProperty("allegato")
  @NotNull
  @Valid
  public OneOfApiImplAllegatoAllegato getAllegato() {
    return this.allegato;
  }

  public void setAllegato(OneOfApiImplAllegatoAllegato allegato) {
    this.allegato = allegato;
  }

  public ApiImplAllegato allegato(OneOfApiImplAllegatoAllegato allegato) {
    this.allegato = allegato;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiImplAllegato {\n");
    
    sb.append("    allegato: ").append(ApiImplAllegato.toIndentedString(this.allegato)).append("\n");
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

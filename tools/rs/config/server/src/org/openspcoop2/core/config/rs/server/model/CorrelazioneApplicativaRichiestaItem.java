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

public class CorrelazioneApplicativaRichiestaItem  {
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "")
  private String elemento = null;
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "")
  private CorrelazioneApplicativaRichiestaEnum identificazioneTipo = null;
 /**
   * Get elemento
   * @return elemento
  **/
  @JsonProperty("elemento")
  @NotNull
  @Valid
 @Size(max=255)  public String getElemento() {
    return this.elemento;
  }

  public void setElemento(String elemento) {
    this.elemento = elemento;
  }

  public CorrelazioneApplicativaRichiestaItem elemento(String elemento) {
    this.elemento = elemento;
    return this;
  }

 /**
   * Get identificazioneTipo
   * @return identificazioneTipo
  **/
  @JsonProperty("identificazione_tipo")
  @NotNull
  @Valid
  public CorrelazioneApplicativaRichiestaEnum getIdentificazioneTipo() {
    return this.identificazioneTipo;
  }

  public void setIdentificazioneTipo(CorrelazioneApplicativaRichiestaEnum identificazioneTipo) {
    this.identificazioneTipo = identificazioneTipo;
  }

  public CorrelazioneApplicativaRichiestaItem identificazioneTipo(CorrelazioneApplicativaRichiestaEnum identificazioneTipo) {
    this.identificazioneTipo = identificazioneTipo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CorrelazioneApplicativaRichiestaItem {\n");
    
    sb.append("    elemento: ").append(CorrelazioneApplicativaRichiestaItem.toIndentedString(this.elemento)).append("\n");
    sb.append("    identificazioneTipo: ").append(CorrelazioneApplicativaRichiestaItem.toIndentedString(this.identificazioneTipo)).append("\n");
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

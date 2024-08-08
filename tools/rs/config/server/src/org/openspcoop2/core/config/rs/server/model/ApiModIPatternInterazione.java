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

public class ApiModIPatternInterazione  {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private ModIPatternInterazioneEnum pattern = null;
  
  @Schema(description = "")
  private ModIPatternInterazioneTipoEnum tipo = null;
  
  @Schema(description = "")
  private ModIPatternInterazioneFunzioneEnum funzione = null;
 /**
   * Get pattern
   * @return pattern
  **/
  @JsonProperty("pattern")
  @NotNull
  @Valid
  public ModIPatternInterazioneEnum getPattern() {
    return this.pattern;
  }

  public void setPattern(ModIPatternInterazioneEnum pattern) {
    this.pattern = pattern;
  }

  public ApiModIPatternInterazione pattern(ModIPatternInterazioneEnum pattern) {
    this.pattern = pattern;
    return this;
  }

 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @Valid
  public ModIPatternInterazioneTipoEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(ModIPatternInterazioneTipoEnum tipo) {
    this.tipo = tipo;
  }

  public ApiModIPatternInterazione tipo(ModIPatternInterazioneTipoEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get funzione
   * @return funzione
  **/
  @JsonProperty("funzione")
  @Valid
  public ModIPatternInterazioneFunzioneEnum getFunzione() {
    return this.funzione;
  }

  public void setFunzione(ModIPatternInterazioneFunzioneEnum funzione) {
    this.funzione = funzione;
  }

  public ApiModIPatternInterazione funzione(ModIPatternInterazioneFunzioneEnum funzione) {
    this.funzione = funzione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiModIPatternInterazione {\n");
    
    sb.append("    pattern: ").append(ApiModIPatternInterazione.toIndentedString(this.pattern)).append("\n");
    sb.append("    tipo: ").append(ApiModIPatternInterazione.toIndentedString(this.tipo)).append("\n");
    sb.append("    funzione: ").append(ApiModIPatternInterazione.toIndentedString(this.funzione)).append("\n");
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

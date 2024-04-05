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

public class CorrelazioneApplicativaBase extends ApiImplConfigurazioneStato {
  
  @Schema(description = "")
  private String elemento = null;
  
  @Schema(description = "")
  private Boolean generazioneErroreIdentificazioneFallita = true;
 /**
   * Get elemento
   * @return elemento
  **/
  @JsonProperty("elemento")
  @Valid
 @Size(max=255)  public String getElemento() {
    return this.elemento;
  }

  public void setElemento(String elemento) {
    this.elemento = elemento;
  }

  public CorrelazioneApplicativaBase elemento(String elemento) {
    this.elemento = elemento;
    return this;
  }

 /**
   * Get generazioneErroreIdentificazioneFallita
   * @return generazioneErroreIdentificazioneFallita
  **/
  @JsonProperty("generazione_errore_identificazione_fallita")
  @Valid
  public Boolean isGenerazioneErroreIdentificazioneFallita() {
    return this.generazioneErroreIdentificazioneFallita;
  }

  public void setGenerazioneErroreIdentificazioneFallita(Boolean generazioneErroreIdentificazioneFallita) {
    this.generazioneErroreIdentificazioneFallita = generazioneErroreIdentificazioneFallita;
  }

  public CorrelazioneApplicativaBase generazioneErroreIdentificazioneFallita(Boolean generazioneErroreIdentificazioneFallita) {
    this.generazioneErroreIdentificazioneFallita = generazioneErroreIdentificazioneFallita;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CorrelazioneApplicativaBase {\n");
    sb.append("    ").append(CorrelazioneApplicativaBase.toIndentedString(super.toString())).append("\n");
    sb.append("    elemento: ").append(CorrelazioneApplicativaBase.toIndentedString(this.elemento)).append("\n");
    sb.append("    generazioneErroreIdentificazioneFallita: ").append(CorrelazioneApplicativaBase.toIndentedString(this.generazioneErroreIdentificazioneFallita)).append("\n");
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

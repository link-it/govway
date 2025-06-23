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

public class CorrelazioneApplicativaRisposta extends CorrelazioneApplicativaBase {
  
  @Schema(required = true, description = "")
  private CorrelazioneApplicativaRispostaEnum identificazioneTipo = null;
  
  @Schema(description = "La semantica cambia in funzione del tipo:   * header-based: nome dell'header   * content-based: xpath o json path da applicare sul contenuto   * input-based: questo field non deve essere valorizzato poichè l'informazione applicativa viene estratta dagli header di integrazione   * template: template con parti dinamiche risolte a runtime da GovWay   * freemarker-template: freemarker template   * velocity-template: velocity template   * disabilitato:  questo field non deve essere valorizzato poichè la funzionalità di estrazione è disabilitata")
 /**
   * La semantica cambia in funzione del tipo:   * header-based: nome dell'header   * content-based: xpath o json path da applicare sul contenuto   * input-based: questo field non deve essere valorizzato poichè l'informazione applicativa viene estratta dagli header di integrazione   * template: template con parti dinamiche risolte a runtime da GovWay   * freemarker-template: freemarker template   * velocity-template: velocity template   * disabilitato:  questo field non deve essere valorizzato poichè la funzionalità di estrazione è disabilitata  
  **/
  private String identificazione = null;
 /**
   * Get identificazioneTipo
   * @return identificazioneTipo
  **/
  @JsonProperty("identificazione_tipo")
  @NotNull
  @Valid
  public CorrelazioneApplicativaRispostaEnum getIdentificazioneTipo() {
    return this.identificazioneTipo;
  }

  public void setIdentificazioneTipo(CorrelazioneApplicativaRispostaEnum identificazioneTipo) {
    this.identificazioneTipo = identificazioneTipo;
  }

  public CorrelazioneApplicativaRisposta identificazioneTipo(CorrelazioneApplicativaRispostaEnum identificazioneTipo) {
    this.identificazioneTipo = identificazioneTipo;
    return this;
  }

 /**
   * La semantica cambia in funzione del tipo:   * header-based: nome dell'header   * content-based: xpath o json path da applicare sul contenuto   * input-based: questo field non deve essere valorizzato poichè l'informazione applicativa viene estratta dagli header di integrazione   * template: template con parti dinamiche risolte a runtime da GovWay   * freemarker-template: freemarker template   * velocity-template: velocity template   * disabilitato:  questo field non deve essere valorizzato poichè la funzionalità di estrazione è disabilitata
   * @return identificazione
  **/
  @JsonProperty("identificazione")
  @Valid
  public String getIdentificazione() {
    return this.identificazione;
  }

  public void setIdentificazione(String identificazione) {
    this.identificazione = identificazione;
  }

  public CorrelazioneApplicativaRisposta identificazione(String identificazione) {
    this.identificazione = identificazione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CorrelazioneApplicativaRisposta {\n");
    sb.append("    ").append(CorrelazioneApplicativaRisposta.toIndentedString(super.toString())).append("\n");
    sb.append("    identificazioneTipo: ").append(CorrelazioneApplicativaRisposta.toIndentedString(this.identificazioneTipo)).append("\n");
    sb.append("    identificazione: ").append(CorrelazioneApplicativaRisposta.toIndentedString(this.identificazione)).append("\n");
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

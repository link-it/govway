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

public class ApiModISicurezzaMessaggioApplicabilitaCustom  {
  
  @Schema(required = true, description = "")
  private ModISicurezzaMessaggioApplicabilitaEnum applicabilita = null;
  
  @Schema(required = true, description = "")
  private ModISicurezzaMessaggioApplicabilitaCustomEnum richiesta = null;
  
  @Schema(description = "")
  private String richiestaContentType = null;
  
  @Schema(required = true, description = "")
  private ModISicurezzaMessaggioApplicabilitaCustomEnum risposta = null;
  
  @Schema(description = "")
  private String rispostaContentType = null;
  
  @Schema(description = "")
  private String rispostaCodice = null;
 /**
   * Get applicabilita
   * @return applicabilita
  **/
  @JsonProperty("applicabilita")
  @NotNull
  @Valid
  public ModISicurezzaMessaggioApplicabilitaEnum getApplicabilita() {
    return this.applicabilita;
  }

  public void setApplicabilita(ModISicurezzaMessaggioApplicabilitaEnum applicabilita) {
    this.applicabilita = applicabilita;
  }

  public ApiModISicurezzaMessaggioApplicabilitaCustom applicabilita(ModISicurezzaMessaggioApplicabilitaEnum applicabilita) {
    this.applicabilita = applicabilita;
    return this;
  }

 /**
   * Get richiesta
   * @return richiesta
  **/
  @JsonProperty("richiesta")
  @NotNull
  @Valid
  public ModISicurezzaMessaggioApplicabilitaCustomEnum getRichiesta() {
    return this.richiesta;
  }

  public void setRichiesta(ModISicurezzaMessaggioApplicabilitaCustomEnum richiesta) {
    this.richiesta = richiesta;
  }

  public ApiModISicurezzaMessaggioApplicabilitaCustom richiesta(ModISicurezzaMessaggioApplicabilitaCustomEnum richiesta) {
    this.richiesta = richiesta;
    return this;
  }

 /**
   * Get richiestaContentType
   * @return richiestaContentType
  **/
  @JsonProperty("richiesta_content_type")
  @Valid
 @Size(max=4000)  public String getRichiestaContentType() {
    return this.richiestaContentType;
  }

  public void setRichiestaContentType(String richiestaContentType) {
    this.richiestaContentType = richiestaContentType;
  }

  public ApiModISicurezzaMessaggioApplicabilitaCustom richiestaContentType(String richiestaContentType) {
    this.richiestaContentType = richiestaContentType;
    return this;
  }

 /**
   * Get risposta
   * @return risposta
  **/
  @JsonProperty("risposta")
  @NotNull
  @Valid
  public ModISicurezzaMessaggioApplicabilitaCustomEnum getRisposta() {
    return this.risposta;
  }

  public void setRisposta(ModISicurezzaMessaggioApplicabilitaCustomEnum risposta) {
    this.risposta = risposta;
  }

  public ApiModISicurezzaMessaggioApplicabilitaCustom risposta(ModISicurezzaMessaggioApplicabilitaCustomEnum risposta) {
    this.risposta = risposta;
    return this;
  }

 /**
   * Get rispostaContentType
   * @return rispostaContentType
  **/
  @JsonProperty("risposta_content_type")
  @Valid
 @Size(max=4000)  public String getRispostaContentType() {
    return this.rispostaContentType;
  }

  public void setRispostaContentType(String rispostaContentType) {
    this.rispostaContentType = rispostaContentType;
  }

  public ApiModISicurezzaMessaggioApplicabilitaCustom rispostaContentType(String rispostaContentType) {
    this.rispostaContentType = rispostaContentType;
    return this;
  }

 /**
   * Get rispostaCodice
   * @return rispostaCodice
  **/
  @JsonProperty("risposta_codice")
  @Valid
 @Size(max=4000)  public String getRispostaCodice() {
    return this.rispostaCodice;
  }

  public void setRispostaCodice(String rispostaCodice) {
    this.rispostaCodice = rispostaCodice;
  }

  public ApiModISicurezzaMessaggioApplicabilitaCustom rispostaCodice(String rispostaCodice) {
    this.rispostaCodice = rispostaCodice;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiModISicurezzaMessaggioApplicabilitaCustom {\n");
    
    sb.append("    applicabilita: ").append(ApiModISicurezzaMessaggioApplicabilitaCustom.toIndentedString(this.applicabilita)).append("\n");
    sb.append("    richiesta: ").append(ApiModISicurezzaMessaggioApplicabilitaCustom.toIndentedString(this.richiesta)).append("\n");
    sb.append("    richiestaContentType: ").append(ApiModISicurezzaMessaggioApplicabilitaCustom.toIndentedString(this.richiestaContentType)).append("\n");
    sb.append("    risposta: ").append(ApiModISicurezzaMessaggioApplicabilitaCustom.toIndentedString(this.risposta)).append("\n");
    sb.append("    rispostaContentType: ").append(ApiModISicurezzaMessaggioApplicabilitaCustom.toIndentedString(this.rispostaContentType)).append("\n");
    sb.append("    rispostaCodice: ").append(ApiModISicurezzaMessaggioApplicabilitaCustom.toIndentedString(this.rispostaCodice)).append("\n");
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

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
package org.openspcoop2.utils.service.beans;

import org.joda.time.DateTime;
import org.openspcoop2.utils.service.beans.TransazioneExtDettaglioMessaggioBase;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtDettaglioRispostaBase", propOrder =
    { "dataUscita", "codiceRisposta", "dataIngresso", "codiceRispostaIngresso"
})


public class TransazioneExtDettaglioRispostaBase extends TransazioneExtDettaglioMessaggioBase {
  @XmlElement(name="data_uscita", required = true)
  
  @Schema(required = true, description = "")
  private DateTime dataUscita = null;
  @XmlElement(name="codice_risposta", required = true)
  
  @Schema(example = "200", required = true, description = "Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.")
 /**
   * Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.  
  **/
  private String codiceRisposta = null;
  @XmlElement(name="data_ingresso")
  
  @Schema(description = "")
  private DateTime dataIngresso = null;
  @XmlElement(name="codice_risposta_ingresso")
  
  @Schema(example = "200", description = "Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.")
 /**
   * Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.  
  **/
  private String codiceRispostaIngresso = null;
 /**
   * Get dataUscita
   * @return dataUscita
  **/
  @JsonProperty("data_uscita")
  @NotNull
  @Valid
  public DateTime getDataUscita() {
    return this.dataUscita;
  }

  public void setDataUscita(DateTime dataUscita) {
    this.dataUscita = dataUscita;
  }

  public TransazioneExtDettaglioRispostaBase dataUscita(DateTime dataUscita) {
    this.dataUscita = dataUscita;
    return this;
  }

 /**
   * Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.
   * @return codiceRisposta
  **/
  @JsonProperty("codice_risposta")
  @NotNull
  @Valid
  public String getCodiceRisposta() {
    return this.codiceRisposta;
  }

  public void setCodiceRisposta(String codiceRisposta) {
    this.codiceRisposta = codiceRisposta;
  }

  public TransazioneExtDettaglioRispostaBase codiceRisposta(String codiceRisposta) {
    this.codiceRisposta = codiceRisposta;
    return this;
  }

 /**
   * Get dataIngresso
   * @return dataIngresso
  **/
  @JsonProperty("data_ingresso")
  @Valid
  public DateTime getDataIngresso() {
    return this.dataIngresso;
  }

  public void setDataIngresso(DateTime dataIngresso) {
    this.dataIngresso = dataIngresso;
  }

  public TransazioneExtDettaglioRispostaBase dataIngresso(DateTime dataIngresso) {
    this.dataIngresso = dataIngresso;
    return this;
  }

 /**
   * Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.
   * @return codiceRispostaIngresso
  **/
  @JsonProperty("codice_risposta_ingresso")
  @Valid
  public String getCodiceRispostaIngresso() {
    return this.codiceRispostaIngresso;
  }

  public void setCodiceRispostaIngresso(String codiceRispostaIngresso) {
    this.codiceRispostaIngresso = codiceRispostaIngresso;
  }

  public TransazioneExtDettaglioRispostaBase codiceRispostaIngresso(String codiceRispostaIngresso) {
    this.codiceRispostaIngresso = codiceRispostaIngresso;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtDettaglioRispostaBase {\n");
    sb.append("    ").append(TransazioneExtDettaglioRispostaBase.toIndentedString(super.toString())).append("\n");
    sb.append("    dataUscita: ").append(TransazioneExtDettaglioRispostaBase.toIndentedString(this.dataUscita)).append("\n");
    sb.append("    codiceRisposta: ").append(TransazioneExtDettaglioRispostaBase.toIndentedString(this.codiceRisposta)).append("\n");
    sb.append("    dataIngresso: ").append(TransazioneExtDettaglioRispostaBase.toIndentedString(this.dataIngresso)).append("\n");
    sb.append("    codiceRispostaIngresso: ").append(TransazioneExtDettaglioRispostaBase.toIndentedString(this.codiceRispostaIngresso)).append("\n");
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

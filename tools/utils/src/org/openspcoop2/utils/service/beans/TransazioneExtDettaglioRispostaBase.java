/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
    { "dataRicezione", "dataConsegna", "esitoRicezione", "esitoConsegna"
})


public class TransazioneExtDettaglioRispostaBase extends TransazioneExtDettaglioMessaggioBase {
  @XmlElement(name="data_ricezione")
  
  @Schema(example = "2017-07-21T17:32:28Z", description = "")
  private DateTime dataRicezione = null;
  @XmlElement(name="data_consegna", required = true)
  
  @Schema(example = "2017-07-21T17:32:28Z", required = true, description = "")
  private DateTime dataConsegna = null;
  @XmlElement(name="esito_ricezione")
  
  @Schema(example = "200", description = "Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.")
 /**
   * Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.  
  **/
  private String esitoRicezione = null;
  @XmlElement(name="esito_consegna", required = true)
  
  @Schema(example = "200", required = true, description = "Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.")
 /**
   * Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.  
  **/
  private String esitoConsegna = null;
 /**
   * Get dataRicezione
   * @return dataRicezione
  **/
  @JsonProperty("data_ricezione")
  @Valid
  public DateTime getDataRicezione() {
    return this.dataRicezione;
  }

  public void setDataRicezione(DateTime dataRicezione) {
    this.dataRicezione = dataRicezione;
  }

  public TransazioneExtDettaglioRispostaBase dataRicezione(DateTime dataRicezione) {
    this.dataRicezione = dataRicezione;
    return this;
  }

 /**
   * Get dataConsegna
   * @return dataConsegna
  **/
  @JsonProperty("data_consegna")
  @NotNull
  @Valid
  public DateTime getDataConsegna() {
    return this.dataConsegna;
  }

  public void setDataConsegna(DateTime dataConsegna) {
    this.dataConsegna = dataConsegna;
  }

  public TransazioneExtDettaglioRispostaBase dataConsegna(DateTime dataConsegna) {
    this.dataConsegna = dataConsegna;
    return this;
  }

 /**
   * Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.
   * @return esitoRicezione
  **/
  @JsonProperty("esito_ricezione")
  @Valid
  public String getEsitoRicezione() {
    return this.esitoRicezione;
  }

  public void setEsitoRicezione(String esitoRicezione) {
    this.esitoRicezione = esitoRicezione;
  }

  public TransazioneExtDettaglioRispostaBase esitoRicezione(String esitoRicezione) {
    this.esitoRicezione = esitoRicezione;
    return this;
  }

 /**
   * Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.
   * @return esitoConsegna
  **/
  @JsonProperty("esito_consegna")
  @NotNull
  @Valid
  public String getEsitoConsegna() {
    return this.esitoConsegna;
  }

  public void setEsitoConsegna(String esitoConsegna) {
    this.esitoConsegna = esitoConsegna;
  }

  public TransazioneExtDettaglioRispostaBase esitoConsegna(String esitoConsegna) {
    this.esitoConsegna = esitoConsegna;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtDettaglioRispostaBase {\n");
    sb.append("    ").append(TransazioneExtDettaglioRispostaBase.toIndentedString(super.toString())).append("\n");
    sb.append("    dataRicezione: ").append(TransazioneExtDettaglioRispostaBase.toIndentedString(this.dataRicezione)).append("\n");
    sb.append("    dataConsegna: ").append(TransazioneExtDettaglioRispostaBase.toIndentedString(this.dataConsegna)).append("\n");
    sb.append("    esitoRicezione: ").append(TransazioneExtDettaglioRispostaBase.toIndentedString(this.esitoRicezione)).append("\n");
    sb.append("    esitoConsegna: ").append(TransazioneExtDettaglioRispostaBase.toIndentedString(this.esitoConsegna)).append("\n");
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

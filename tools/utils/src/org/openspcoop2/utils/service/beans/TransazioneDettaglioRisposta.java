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
package org.openspcoop2.utils.service.beans;

import java.util.List;
import org.joda.time.DateTime;
import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneDettaglioRisposta", propOrder =
    { "dataRicezione", "dataConsegna", "esitoRicezione", "esitoConsegna", "faultRicezione", "faultConsegna", "dettagliErrore", "contenuti"
})


public class TransazioneDettaglioRisposta extends TransazioneDettaglioMessaggio {
  @XmlElement(name="data_ricezione")
  
  @Schema(description = "")
  private DateTime dataRicezione = null;
  @XmlElement(name="data_consegna", required = true)
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private DateTime dataConsegna = null;
  @XmlElement(name="esito_ricezione")
  
  @Schema(example = "200", description = "Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.")
 /**
   * Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.  
  **/
  private String esitoRicezione = null;
  @XmlElement(name="esito_consegna")
  
  @Schema(example = "200", description = "Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.")
 /**
   * Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.  
  **/
  private String esitoConsegna = null;
  @XmlElement(name="fault_ricezione")
  
  @Schema(description = "")
  private byte[] faultRicezione = null;
  @XmlElement(name="fault_consegna")
  
  @Schema(description = "")
  private byte[] faultConsegna = null;
  @XmlElement(name="dettagli_errore")
  
  @Schema(description = "")
  private List<String> dettagliErrore = null;
  @XmlElement(name="contenuti")
  
  @Schema(description = "")
  private TransazioneContenutoMessaggio contenuti = null;
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

  public TransazioneDettaglioRisposta dataRicezione(DateTime dataRicezione) {
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

  public TransazioneDettaglioRisposta dataConsegna(DateTime dataConsegna) {
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

  public TransazioneDettaglioRisposta esitoRicezione(String esitoRicezione) {
    this.esitoRicezione = esitoRicezione;
    return this;
  }

 /**
   * Codice associato alla risposta. Può essere un http status per i protocolli basati su HTTP.
   * @return esitoConsegna
  **/
  @JsonProperty("esito_consegna")
  @Valid
  public String getEsitoConsegna() {
    return this.esitoConsegna;
  }

  public void setEsitoConsegna(String esitoConsegna) {
    this.esitoConsegna = esitoConsegna;
  }

  public TransazioneDettaglioRisposta esitoConsegna(String esitoConsegna) {
    this.esitoConsegna = esitoConsegna;
    return this;
  }

 /**
   * Get faultRicezione
   * @return faultRicezione
  **/
  @JsonProperty("fault_ricezione")
  @Valid
  public byte[] getFaultRicezione() {
    return this.faultRicezione;
  }

  public void setFaultRicezione(byte[] faultRicezione) {
    this.faultRicezione = faultRicezione;
  }

  public TransazioneDettaglioRisposta faultRicezione(byte[] faultRicezione) {
    this.faultRicezione = faultRicezione;
    return this;
  }

 /**
   * Get faultConsegna
   * @return faultConsegna
  **/
  @JsonProperty("fault_consegna")
  @Valid
  public byte[] getFaultConsegna() {
    return this.faultConsegna;
  }

  public void setFaultConsegna(byte[] faultConsegna) {
    this.faultConsegna = faultConsegna;
  }

  public TransazioneDettaglioRisposta faultConsegna(byte[] faultConsegna) {
    this.faultConsegna = faultConsegna;
    return this;
  }

 /**
   * Get dettagliErrore
   * @return dettagliErrore
  **/
  @JsonProperty("dettagli_errore")
  @Valid
  public List<String> getDettagliErrore() {
    return this.dettagliErrore;
  }

  public void setDettagliErrore(List<String> dettagliErrore) {
    this.dettagliErrore = dettagliErrore;
  }

  public TransazioneDettaglioRisposta dettagliErrore(List<String> dettagliErrore) {
    this.dettagliErrore = dettagliErrore;
    return this;
  }

  public TransazioneDettaglioRisposta addDettagliErroreItem(String dettagliErroreItem) {
    this.dettagliErrore.add(dettagliErroreItem);
    return this;
  }

 /**
   * Get contenuti
   * @return contenuti
  **/
  @JsonProperty("contenuti")
  @Valid
  public TransazioneContenutoMessaggio getContenuti() {
    return this.contenuti;
  }

  public void setContenuti(TransazioneContenutoMessaggio contenuti) {
    this.contenuti = contenuti;
  }

  public TransazioneDettaglioRisposta contenuti(TransazioneContenutoMessaggio contenuti) {
    this.contenuti = contenuti;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneDettaglioRisposta {\n");
    sb.append("    ").append(TransazioneDettaglioRisposta.toIndentedString(super.toString())).append("\n");
    sb.append("    dataRicezione: ").append(TransazioneDettaglioRisposta.toIndentedString(this.dataRicezione)).append("\n");
    sb.append("    dataConsegna: ").append(TransazioneDettaglioRisposta.toIndentedString(this.dataConsegna)).append("\n");
    sb.append("    esitoRicezione: ").append(TransazioneDettaglioRisposta.toIndentedString(this.esitoRicezione)).append("\n");
    sb.append("    esitoConsegna: ").append(TransazioneDettaglioRisposta.toIndentedString(this.esitoConsegna)).append("\n");
    sb.append("    faultRicezione: ").append(TransazioneDettaglioRisposta.toIndentedString(this.faultRicezione)).append("\n");
    sb.append("    faultConsegna: ").append(TransazioneDettaglioRisposta.toIndentedString(this.faultConsegna)).append("\n");
    sb.append("    dettagliErrore: ").append(TransazioneDettaglioRisposta.toIndentedString(this.dettagliErrore)).append("\n");
    sb.append("    contenuti: ").append(TransazioneDettaglioRisposta.toIndentedString(this.contenuti)).append("\n");
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

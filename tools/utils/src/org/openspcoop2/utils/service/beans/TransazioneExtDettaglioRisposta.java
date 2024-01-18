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
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtDettaglioRisposta", propOrder =
    { "contenutiIngresso", "contenutiUscita", "duplicatiMessaggio", "traccia", "faultRicezione", "faultConsegna", "dettagliErrore", "dataRicezioneAcquisita", "dataConsegnaEffettuata", "faultRicezioneFormato", "faultConsegnaFormato"
})


public class TransazioneExtDettaglioRisposta extends TransazioneExtDettaglioRispostaBase {
  @XmlElement(name="contenuti_ingresso")
  
  @Schema(description = "")
  private TransazioneExtContenutoMessaggio contenutiIngresso = null;
  @XmlElement(name="contenuti_uscita")
  
  @Schema(description = "")
  private TransazioneExtContenutoMessaggio contenutiUscita = null;
  @XmlElement(name="duplicati_messaggio")
  
  @Schema(description = "")
  private Integer duplicatiMessaggio = null;
  @XmlElement(name="traccia")
  
  @Schema(description = "")
  private String traccia = null;
  @XmlElement(name="fault_ricezione")
  
  @Schema(description = "")
  private byte[] faultRicezione = null;
  @XmlElement(name="fault_consegna")
  
  @Schema(description = "")
  private byte[] faultConsegna = null;
  @XmlElement(name="dettagli_errore")
  
  @Schema(description = "")
  private List<String> dettagliErrore = null;
  @XmlElement(name="data_ricezione_acquisita")
  
  @Schema(description = "")
  private DateTime dataRicezioneAcquisita = null;
  @XmlElement(name="data_consegna_effettuata")
  
  @Schema(description = "")
  private DateTime dataConsegnaEffettuata = null;
  @XmlElement(name="fault_ricezione_formato")
  
  @Schema(description = "")
  private TransazioneMessaggioFormatoEnum faultRicezioneFormato = null;
  @XmlElement(name="fault_consegna_formato")
  
  @Schema(description = "")
  private TransazioneMessaggioFormatoEnum faultConsegnaFormato = null;
 /**
   * Get contenutiIngresso
   * @return contenutiIngresso
  **/
  @JsonProperty("contenuti_ingresso")
  @Valid
  public TransazioneExtContenutoMessaggio getContenutiIngresso() {
    return this.contenutiIngresso;
  }

  public void setContenutiIngresso(TransazioneExtContenutoMessaggio contenutiIngresso) {
    this.contenutiIngresso = contenutiIngresso;
  }

  public TransazioneExtDettaglioRisposta contenutiIngresso(TransazioneExtContenutoMessaggio contenutiIngresso) {
    this.contenutiIngresso = contenutiIngresso;
    return this;
  }

 /**
   * Get contenutiUscita
   * @return contenutiUscita
  **/
  @JsonProperty("contenuti_uscita")
  @Valid
  public TransazioneExtContenutoMessaggio getContenutiUscita() {
    return this.contenutiUscita;
  }

  public void setContenutiUscita(TransazioneExtContenutoMessaggio contenutiUscita) {
    this.contenutiUscita = contenutiUscita;
  }

  public TransazioneExtDettaglioRisposta contenutiUscita(TransazioneExtContenutoMessaggio contenutiUscita) {
    this.contenutiUscita = contenutiUscita;
    return this;
  }

 /**
   * Get duplicatiMessaggio
   * @return duplicatiMessaggio
  **/
  @JsonProperty("duplicati_messaggio")
  @Valid
  public Integer getDuplicatiMessaggio() {
    return this.duplicatiMessaggio;
  }

  public void setDuplicatiMessaggio(Integer duplicatiMessaggio) {
    this.duplicatiMessaggio = duplicatiMessaggio;
  }

  public TransazioneExtDettaglioRisposta duplicatiMessaggio(Integer duplicatiMessaggio) {
    this.duplicatiMessaggio = duplicatiMessaggio;
    return this;
  }

 /**
   * Get traccia
   * @return traccia
  **/
  @JsonProperty("traccia")
  @Valid
  public String getTraccia() {
    return this.traccia;
  }

  public void setTraccia(String traccia) {
    this.traccia = traccia;
  }

  public TransazioneExtDettaglioRisposta traccia(String traccia) {
    this.traccia = traccia;
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

  public TransazioneExtDettaglioRisposta faultRicezione(byte[] faultRicezione) {
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

  public TransazioneExtDettaglioRisposta faultConsegna(byte[] faultConsegna) {
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

  public TransazioneExtDettaglioRisposta dettagliErrore(List<String> dettagliErrore) {
    this.dettagliErrore = dettagliErrore;
    return this;
  }

  public TransazioneExtDettaglioRisposta addDettagliErroreItem(String dettagliErroreItem) {
    this.dettagliErrore.add(dettagliErroreItem);
    return this;
  }

 /**
   * Get dataRicezioneAcquisita
   * @return dataRicezioneAcquisita
  **/
  @JsonProperty("data_ricezione_acquisita")
  @Valid
  public DateTime getDataRicezioneAcquisita() {
    return this.dataRicezioneAcquisita;
  }

  public void setDataRicezioneAcquisita(DateTime dataRicezioneAcquisita) {
    this.dataRicezioneAcquisita = dataRicezioneAcquisita;
  }

  public TransazioneExtDettaglioRisposta dataRicezioneAcquisita(DateTime dataRicezioneAcquisita) {
    this.dataRicezioneAcquisita = dataRicezioneAcquisita;
    return this;
  }

 /**
   * Get dataConsegnaEffettuata
   * @return dataConsegnaEffettuata
  **/
  @JsonProperty("data_consegna_effettuata")
  @Valid
  public DateTime getDataConsegnaEffettuata() {
    return this.dataConsegnaEffettuata;
  }

  public void setDataConsegnaEffettuata(DateTime dataConsegnaEffettuata) {
    this.dataConsegnaEffettuata = dataConsegnaEffettuata;
  }

  public TransazioneExtDettaglioRisposta dataConsegnaEffettuata(DateTime dataConsegnaEffettuata) {
    this.dataConsegnaEffettuata = dataConsegnaEffettuata;
    return this;
  }

 /**
   * Get faultRicezioneFormato
   * @return faultRicezioneFormato
  **/
  @JsonProperty("fault_ricezione_formato")
  @Valid
  public TransazioneMessaggioFormatoEnum getFaultRicezioneFormato() {
    return this.faultRicezioneFormato;
  }

  public void setFaultRicezioneFormato(TransazioneMessaggioFormatoEnum faultRicezioneFormato) {
    this.faultRicezioneFormato = faultRicezioneFormato;
  }

  public TransazioneExtDettaglioRisposta faultRicezioneFormato(TransazioneMessaggioFormatoEnum faultRicezioneFormato) {
    this.faultRicezioneFormato = faultRicezioneFormato;
    return this;
  }

 /**
   * Get faultConsegnaFormato
   * @return faultConsegnaFormato
  **/
  @JsonProperty("fault_consegna_formato")
  @Valid
  public TransazioneMessaggioFormatoEnum getFaultConsegnaFormato() {
    return this.faultConsegnaFormato;
  }

  public void setFaultConsegnaFormato(TransazioneMessaggioFormatoEnum faultConsegnaFormato) {
    this.faultConsegnaFormato = faultConsegnaFormato;
  }

  public TransazioneExtDettaglioRisposta faultConsegnaFormato(TransazioneMessaggioFormatoEnum faultConsegnaFormato) {
    this.faultConsegnaFormato = faultConsegnaFormato;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtDettaglioRisposta {\n");
    sb.append("    ").append(TransazioneExtDettaglioRisposta.toIndentedString(super.toString())).append("\n");
    sb.append("    contenutiIngresso: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.contenutiIngresso)).append("\n");
    sb.append("    contenutiUscita: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.contenutiUscita)).append("\n");
    sb.append("    duplicatiMessaggio: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.duplicatiMessaggio)).append("\n");
    sb.append("    traccia: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.traccia)).append("\n");
    sb.append("    faultRicezione: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.faultRicezione)).append("\n");
    sb.append("    faultConsegna: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.faultConsegna)).append("\n");
    sb.append("    dettagliErrore: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.dettagliErrore)).append("\n");
    sb.append("    dataRicezioneAcquisita: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.dataRicezioneAcquisita)).append("\n");
    sb.append("    dataConsegnaEffettuata: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.dataConsegnaEffettuata)).append("\n");
    sb.append("    faultRicezioneFormato: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.faultRicezioneFormato)).append("\n");
    sb.append("    faultConsegnaFormato: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.faultConsegnaFormato)).append("\n");
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

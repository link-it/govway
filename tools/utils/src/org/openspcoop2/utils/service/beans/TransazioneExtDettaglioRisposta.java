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

import java.util.List;
import org.openspcoop2.utils.service.beans.TransazioneExtContenutoMessaggio;
import org.openspcoop2.utils.service.beans.TransazioneExtDettaglioRispostaBase;
import org.openspcoop2.utils.service.beans.TransazioneMessaggioFormatoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtDettaglioRisposta", propOrder =
    { "contenutiIngresso", "contenutiUscita", "duplicatiMessaggio", "traccia", "fault", "dettagliErrore", "faultFormato", "faultIngresso", "faultIngressoFormato"
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
  @XmlElement(name="fault")
  
  @Schema(description = "")
  private byte[] fault = null;
  @XmlElement(name="dettagli_errore")
  
  @Schema(description = "")
  private List<String> dettagliErrore = null;
  @XmlElement(name="fault_formato")
  
  @Schema(description = "")
  private TransazioneMessaggioFormatoEnum faultFormato = null;
  @XmlElement(name="fault_ingresso")
  
  @Schema(description = "")
  private byte[] faultIngresso = null;
  @XmlElement(name="fault_ingresso_formato")
  
  @Schema(description = "")
  private TransazioneMessaggioFormatoEnum faultIngressoFormato = null;
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
   * Get fault
   * @return fault
  **/
  @JsonProperty("fault")
  @Valid
  public byte[] getFault() {
    return this.fault;
  }

  public void setFault(byte[] fault) {
    this.fault = fault;
  }

  public TransazioneExtDettaglioRisposta fault(byte[] fault) {
    this.fault = fault;
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
   * Get faultFormato
   * @return faultFormato
  **/
  @JsonProperty("fault_formato")
  @Valid
  public TransazioneMessaggioFormatoEnum getFaultFormato() {
    return this.faultFormato;
  }

  public void setFaultFormato(TransazioneMessaggioFormatoEnum faultFormato) {
    this.faultFormato = faultFormato;
  }

  public TransazioneExtDettaglioRisposta faultFormato(TransazioneMessaggioFormatoEnum faultFormato) {
    this.faultFormato = faultFormato;
    return this;
  }

 /**
   * Get faultIngresso
   * @return faultIngresso
  **/
  @JsonProperty("fault_ingresso")
  @Valid
  public byte[] getFaultIngresso() {
    return this.faultIngresso;
  }

  public void setFaultIngresso(byte[] faultIngresso) {
    this.faultIngresso = faultIngresso;
  }

  public TransazioneExtDettaglioRisposta faultIngresso(byte[] faultIngresso) {
    this.faultIngresso = faultIngresso;
    return this;
  }

 /**
   * Get faultIngressoFormato
   * @return faultIngressoFormato
  **/
  @JsonProperty("fault_ingresso_formato")
  @Valid
  public TransazioneMessaggioFormatoEnum getFaultIngressoFormato() {
    return this.faultIngressoFormato;
  }

  public void setFaultIngressoFormato(TransazioneMessaggioFormatoEnum faultIngressoFormato) {
    this.faultIngressoFormato = faultIngressoFormato;
  }

  public TransazioneExtDettaglioRisposta faultIngressoFormato(TransazioneMessaggioFormatoEnum faultIngressoFormato) {
    this.faultIngressoFormato = faultIngressoFormato;
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
    sb.append("    fault: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.fault)).append("\n");
    sb.append("    dettagliErrore: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.dettagliErrore)).append("\n");
    sb.append("    faultFormato: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.faultFormato)).append("\n");
    sb.append("    faultIngresso: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.faultIngresso)).append("\n");
    sb.append("    faultIngressoFormato: ").append(TransazioneExtDettaglioRisposta.toIndentedString(this.faultIngressoFormato)).append("\n");
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

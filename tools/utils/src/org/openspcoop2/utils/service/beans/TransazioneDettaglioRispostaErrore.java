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
import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneDettaglioRispostaErrore", propOrder =
    { "faultRicezione", "faultConsegna", "dettagliErrore"
})

@XmlRootElement(name="TransazioneDettaglioRispostaErrore")
public class TransazioneDettaglioRispostaErrore  {
  @XmlElement(name="fault_ricezione")
  
  @Schema(description = "")
  private byte[] faultRicezione = null;
  @XmlElement(name="fault_consegna")
  
  @Schema(description = "")
  private byte[] faultConsegna = null;
  @XmlElement(name="dettagli_errore")
  
  @Schema(description = "")
  private List<String> dettagliErrore = null;
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

  public TransazioneDettaglioRispostaErrore faultRicezione(byte[] faultRicezione) {
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

  public TransazioneDettaglioRispostaErrore faultConsegna(byte[] faultConsegna) {
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

  public TransazioneDettaglioRispostaErrore dettagliErrore(List<String> dettagliErrore) {
    this.dettagliErrore = dettagliErrore;
    return this;
  }

  public TransazioneDettaglioRispostaErrore addDettagliErroreItem(String dettagliErroreItem) {
    this.dettagliErrore.add(dettagliErroreItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneDettaglioRispostaErrore {\n");
    
    sb.append("    faultRicezione: ").append(TransazioneDettaglioRispostaErrore.toIndentedString(this.faultRicezione)).append("\n");
    sb.append("    faultConsegna: ").append(TransazioneDettaglioRispostaErrore.toIndentedString(this.faultConsegna)).append("\n");
    sb.append("    dettagliErrore: ").append(TransazioneDettaglioRispostaErrore.toIndentedString(this.dettagliErrore)).append("\n");
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

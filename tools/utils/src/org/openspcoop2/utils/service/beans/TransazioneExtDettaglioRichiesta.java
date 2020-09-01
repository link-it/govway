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

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtDettaglioRichiesta", propOrder =
    { "contenutiIngresso", "contenutiUscita", "duplicatiMessaggio", "traccia"
})


public class TransazioneExtDettaglioRichiesta extends TransazioneExtDettaglioRichiestaBase {
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

  public TransazioneExtDettaglioRichiesta contenutiIngresso(TransazioneExtContenutoMessaggio contenutiIngresso) {
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

  public TransazioneExtDettaglioRichiesta contenutiUscita(TransazioneExtContenutoMessaggio contenutiUscita) {
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

  public TransazioneExtDettaglioRichiesta duplicatiMessaggio(Integer duplicatiMessaggio) {
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

  public TransazioneExtDettaglioRichiesta traccia(String traccia) {
    this.traccia = traccia;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtDettaglioRichiesta {\n");
    sb.append("    ").append(TransazioneExtDettaglioRichiesta.toIndentedString(super.toString())).append("\n");
    sb.append("    contenutiIngresso: ").append(TransazioneExtDettaglioRichiesta.toIndentedString(this.contenutiIngresso)).append("\n");
    sb.append("    contenutiUscita: ").append(TransazioneExtDettaglioRichiesta.toIndentedString(this.contenutiUscita)).append("\n");
    sb.append("    duplicatiMessaggio: ").append(TransazioneExtDettaglioRichiesta.toIndentedString(this.duplicatiMessaggio)).append("\n");
    sb.append("    traccia: ").append(TransazioneExtDettaglioRichiesta.toIndentedString(this.traccia)).append("\n");
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

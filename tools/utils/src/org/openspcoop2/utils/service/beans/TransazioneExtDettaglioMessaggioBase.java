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
import org.openspcoop2.utils.service.beans.TransazioneDettaglioMessaggio;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtDettaglioMessaggioBase", propOrder =
    { "dataAccettazione", "bytesIngresso", "bytesUscita"
})


public class TransazioneExtDettaglioMessaggioBase extends TransazioneDettaglioMessaggio {
  @XmlElement(name="data_accettazione")
  
  @Schema(example = "2017-07-21T17:32:28Z", description = "")
  private DateTime dataAccettazione = null;
  @XmlElement(name="bytes_ingresso")
  
  @Schema(description = "")
  private Long bytesIngresso = null;
  @XmlElement(name="bytes_uscita")
  
  @Schema(description = "")
  private Long bytesUscita = null;
 /**
   * Get dataAccettazione
   * @return dataAccettazione
  **/
  @JsonProperty("data_accettazione")
  @Valid
  public DateTime getDataAccettazione() {
    return this.dataAccettazione;
  }

  public void setDataAccettazione(DateTime dataAccettazione) {
    this.dataAccettazione = dataAccettazione;
  }

  public TransazioneExtDettaglioMessaggioBase dataAccettazione(DateTime dataAccettazione) {
    this.dataAccettazione = dataAccettazione;
    return this;
  }

 /**
   * Get bytesIngresso
   * @return bytesIngresso
  **/
  @JsonProperty("bytes_ingresso")
  @Valid
  public Long getBytesIngresso() {
    return this.bytesIngresso;
  }

  public void setBytesIngresso(Long bytesIngresso) {
    this.bytesIngresso = bytesIngresso;
  }

  public TransazioneExtDettaglioMessaggioBase bytesIngresso(Long bytesIngresso) {
    this.bytesIngresso = bytesIngresso;
    return this;
  }

 /**
   * Get bytesUscita
   * @return bytesUscita
  **/
  @JsonProperty("bytes_uscita")
  @Valid
  public Long getBytesUscita() {
    return this.bytesUscita;
  }

  public void setBytesUscita(Long bytesUscita) {
    this.bytesUscita = bytesUscita;
  }

  public TransazioneExtDettaglioMessaggioBase bytesUscita(Long bytesUscita) {
    this.bytesUscita = bytesUscita;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtDettaglioMessaggioBase {\n");
    sb.append("    ").append(TransazioneExtDettaglioMessaggioBase.toIndentedString(super.toString())).append("\n");
    sb.append("    dataAccettazione: ").append(TransazioneExtDettaglioMessaggioBase.toIndentedString(this.dataAccettazione)).append("\n");
    sb.append("    bytesIngresso: ").append(TransazioneExtDettaglioMessaggioBase.toIndentedString(this.bytesIngresso)).append("\n");
    sb.append("    bytesUscita: ").append(TransazioneExtDettaglioMessaggioBase.toIndentedString(this.bytesUscita)).append("\n");
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

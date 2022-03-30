/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtDettaglioRichiestaBase", propOrder =
    { "dataRicezione", "dataConsegna", "tipo", "urlInvocazione", "connettore"
})


public class TransazioneExtDettaglioRichiestaBase extends TransazioneExtDettaglioMessaggioBase {
  @XmlElement(name="data_ricezione", required = true)
  
  @Schema(required = true, description = "")
  private DateTime dataRicezione = null;
  @XmlElement(name="data_consegna")
  
  @Schema(description = "")
  private DateTime dataConsegna = null;
  @XmlElement(name="tipo")
  
  @Schema(description = "")
  private HttpMethodEnum tipo = null;
  @XmlElement(name="url_invocazione")
  
  @Schema(example = "/govway/in/Ente/PetStore/v2/pet", description = "")
  private String urlInvocazione = null;
  @XmlElement(name="connettore")
  
  @Schema(example = "http://backend.api", description = "")
  private String connettore = null;
 /**
   * Get dataRicezione
   * @return dataRicezione
  **/
  @JsonProperty("data_ricezione")
  @NotNull
  @Valid
  public DateTime getDataRicezione() {
    return this.dataRicezione;
  }

  public void setDataRicezione(DateTime dataRicezione) {
    this.dataRicezione = dataRicezione;
  }

  public TransazioneExtDettaglioRichiestaBase dataRicezione(DateTime dataRicezione) {
    this.dataRicezione = dataRicezione;
    return this;
  }

 /**
   * Get dataConsegna
   * @return dataConsegna
  **/
  @JsonProperty("data_consegna")
  @Valid
  public DateTime getDataConsegna() {
    return this.dataConsegna;
  }

  public void setDataConsegna(DateTime dataConsegna) {
    this.dataConsegna = dataConsegna;
  }

  public TransazioneExtDettaglioRichiestaBase dataConsegna(DateTime dataConsegna) {
    this.dataConsegna = dataConsegna;
    return this;
  }

 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @Valid
  public HttpMethodEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(HttpMethodEnum tipo) {
    this.tipo = tipo;
  }

  public TransazioneExtDettaglioRichiestaBase tipo(HttpMethodEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get urlInvocazione
   * @return urlInvocazione
  **/
  @JsonProperty("url_invocazione")
  @Valid
  public String getUrlInvocazione() {
    return this.urlInvocazione;
  }

  public void setUrlInvocazione(String urlInvocazione) {
    this.urlInvocazione = urlInvocazione;
  }

  public TransazioneExtDettaglioRichiestaBase urlInvocazione(String urlInvocazione) {
    this.urlInvocazione = urlInvocazione;
    return this;
  }

 /**
   * Get connettore
   * @return connettore
  **/
  @JsonProperty("connettore")
  @Valid
  public String getConnettore() {
    return this.connettore;
  }

  public void setConnettore(String connettore) {
    this.connettore = connettore;
  }

  public TransazioneExtDettaglioRichiestaBase connettore(String connettore) {
    this.connettore = connettore;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtDettaglioRichiestaBase {\n");
    sb.append("    ").append(TransazioneExtDettaglioRichiestaBase.toIndentedString(super.toString())).append("\n");
    sb.append("    dataRicezione: ").append(TransazioneExtDettaglioRichiestaBase.toIndentedString(this.dataRicezione)).append("\n");
    sb.append("    dataConsegna: ").append(TransazioneExtDettaglioRichiestaBase.toIndentedString(this.dataConsegna)).append("\n");
    sb.append("    tipo: ").append(TransazioneExtDettaglioRichiestaBase.toIndentedString(this.tipo)).append("\n");
    sb.append("    urlInvocazione: ").append(TransazioneExtDettaglioRichiestaBase.toIndentedString(this.urlInvocazione)).append("\n");
    sb.append("    connettore: ").append(TransazioneExtDettaglioRichiestaBase.toIndentedString(this.connettore)).append("\n");
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

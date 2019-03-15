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
import org.openspcoop2.utils.service.beans.HttpMethodEnum;
import org.openspcoop2.utils.service.beans.TransazioneContenutoMessaggio;
import org.openspcoop2.utils.service.beans.TransazioneDettaglioMessaggio;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneDettaglioRichiesta", propOrder =
    { "dataIngresso", "tipo", "urlInvocazione", "contenuti"
})


public class TransazioneDettaglioRichiesta extends TransazioneDettaglioMessaggio {
  @XmlElement(name="data_ingresso", required = true)
  
  @Schema(required = true, description = "")
  private DateTime dataIngresso = null;
  @XmlElement(name="tipo", required = true)
  
  @Schema(required = true, description = "")
  private HttpMethodEnum tipo = null;
  @XmlElement(name="url_invocazione", required = true)
  
  @Schema(example = "/govway/in/Ente/PetStore/v2/pet", required = true, description = "")
  private String urlInvocazione = null;
  @XmlElement(name="contenuti")
  
  @Schema(description = "")
  private TransazioneContenutoMessaggio contenuti = null;
 /**
   * Get dataIngresso
   * @return dataIngresso
  **/
  @JsonProperty("data_ingresso")
  @NotNull
  @Valid
  public DateTime getDataIngresso() {
    return this.dataIngresso;
  }

  public void setDataIngresso(DateTime dataIngresso) {
    this.dataIngresso = dataIngresso;
  }

  public TransazioneDettaglioRichiesta dataIngresso(DateTime dataIngresso) {
    this.dataIngresso = dataIngresso;
    return this;
  }

 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
  public HttpMethodEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(HttpMethodEnum tipo) {
    this.tipo = tipo;
  }

  public TransazioneDettaglioRichiesta tipo(HttpMethodEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get urlInvocazione
   * @return urlInvocazione
  **/
  @JsonProperty("url_invocazione")
  @NotNull
  @Valid
  public String getUrlInvocazione() {
    return this.urlInvocazione;
  }

  public void setUrlInvocazione(String urlInvocazione) {
    this.urlInvocazione = urlInvocazione;
  }

  public TransazioneDettaglioRichiesta urlInvocazione(String urlInvocazione) {
    this.urlInvocazione = urlInvocazione;
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

  public TransazioneDettaglioRichiesta contenuti(TransazioneContenutoMessaggio contenuti) {
    this.contenuti = contenuti;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneDettaglioRichiesta {\n");
    sb.append("    ").append(TransazioneDettaglioRichiesta.toIndentedString(super.toString())).append("\n");
    sb.append("    dataIngresso: ").append(TransazioneDettaglioRichiesta.toIndentedString(this.dataIngresso)).append("\n");
    sb.append("    tipo: ").append(TransazioneDettaglioRichiesta.toIndentedString(this.tipo)).append("\n");
    sb.append("    urlInvocazione: ").append(TransazioneDettaglioRichiesta.toIndentedString(this.urlInvocazione)).append("\n");
    sb.append("    contenuti: ").append(TransazioneDettaglioRichiesta.toIndentedString(this.contenuti)).append("\n");
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

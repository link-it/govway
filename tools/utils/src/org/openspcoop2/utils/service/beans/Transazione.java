/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "Transazione", propOrder =
    { "richiesta", "risposta", "api", "mittente"
})


public class Transazione extends TransazioneBase {
  @XmlElement(name="richiesta")
  
  @Schema(description = "")
  private TransazioneDettaglioRichiesta richiesta = null;
  @XmlElement(name="risposta")
  
  @Schema(description = "")
  private TransazioneDettaglioRisposta risposta = null;
  @XmlElement(name="api")
  
  @Schema(description = "")
  private TransazioneInformazioniApi api = null;
  @XmlElement(name="mittente")
  
  @Schema(description = "")
  private TransazioneInformazioniMittente mittente = null;
 /**
   * Get richiesta
   * @return richiesta
  **/
  @JsonProperty("richiesta")
  @Valid
  public TransazioneDettaglioRichiesta getRichiesta() {
    return this.richiesta;
  }

  public void setRichiesta(TransazioneDettaglioRichiesta richiesta) {
    this.richiesta = richiesta;
  }

  public Transazione richiesta(TransazioneDettaglioRichiesta richiesta) {
    this.richiesta = richiesta;
    return this;
  }

 /**
   * Get risposta
   * @return risposta
  **/
  @JsonProperty("risposta")
  @Valid
  public TransazioneDettaglioRisposta getRisposta() {
    return this.risposta;
  }

  public void setRisposta(TransazioneDettaglioRisposta risposta) {
    this.risposta = risposta;
  }

  public Transazione risposta(TransazioneDettaglioRisposta risposta) {
    this.risposta = risposta;
    return this;
  }

 /**
   * Get api
   * @return api
  **/
  @JsonProperty("api")
  @Valid
  public TransazioneInformazioniApi getApi() {
    return this.api;
  }

  public void setApi(TransazioneInformazioniApi api) {
    this.api = api;
  }

  public Transazione api(TransazioneInformazioniApi api) {
    this.api = api;
    return this;
  }

 /**
   * Get mittente
   * @return mittente
  **/
  @JsonProperty("mittente")
  @Valid
  public TransazioneInformazioniMittente getMittente() {
    return this.mittente;
  }

  public void setMittente(TransazioneInformazioniMittente mittente) {
    this.mittente = mittente;
  }

  public Transazione mittente(TransazioneInformazioniMittente mittente) {
    this.mittente = mittente;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Transazione {\n");
    sb.append("    ").append(Transazione.toIndentedString(super.toString())).append("\n");
    sb.append("    richiesta: ").append(Transazione.toIndentedString(this.richiesta)).append("\n");
    sb.append("    risposta: ").append(Transazione.toIndentedString(this.risposta)).append("\n");
    sb.append("    api: ").append(Transazione.toIndentedString(this.api)).append("\n");
    sb.append("    mittente: ").append(Transazione.toIndentedString(this.mittente)).append("\n");
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

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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneInformazioniMittente", propOrder =
    { "principal", "fruitore", "utente", "indirizzoClient", "indirizzoClientForwarded"
})

@XmlRootElement(name="TransazioneInformazioniMittente")
public class TransazioneInformazioniMittente  {
  @XmlElement(name="principal")
  
  @Schema(description = "")
  private String principal = null;
  @XmlElement(name="fruitore")
  
  @Schema(description = "")
  private String fruitore = null;
  @XmlElement(name="utente")
  
  @Schema(description = "")
  private String utente = null;
  @XmlElement(name="indirizzo_client")
  
  @Schema(description = "")
  private String indirizzoClient = null;
  @XmlElement(name="indirizzo_client_forwarded")
  
  @Schema(description = "")
  private String indirizzoClientForwarded = null;
 /**
   * Get principal
   * @return principal
  **/
  @JsonProperty("principal")
  @Valid
  public String getPrincipal() {
    return this.principal;
  }

  public void setPrincipal(String principal) {
    this.principal = principal;
  }

  public TransazioneInformazioniMittente principal(String principal) {
    this.principal = principal;
    return this;
  }

 /**
   * Get fruitore
   * @return fruitore
  **/
  @JsonProperty("fruitore")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getFruitore() {
    return this.fruitore;
  }

  public void setFruitore(String fruitore) {
    this.fruitore = fruitore;
  }

  public TransazioneInformazioniMittente fruitore(String fruitore) {
    this.fruitore = fruitore;
    return this;
  }

 /**
   * Get utente
   * @return utente
  **/
  @JsonProperty("utente")
  @Valid
  public String getUtente() {
    return this.utente;
  }

  public void setUtente(String utente) {
    this.utente = utente;
  }

  public TransazioneInformazioniMittente utente(String utente) {
    this.utente = utente;
    return this;
  }

 /**
   * Get indirizzoClient
   * @return indirizzoClient
  **/
  @JsonProperty("indirizzo_client")
  @Valid
  public String getIndirizzoClient() {
    return this.indirizzoClient;
  }

  public void setIndirizzoClient(String indirizzoClient) {
    this.indirizzoClient = indirizzoClient;
  }

  public TransazioneInformazioniMittente indirizzoClient(String indirizzoClient) {
    this.indirizzoClient = indirizzoClient;
    return this;
  }

 /**
   * Get indirizzoClientForwarded
   * @return indirizzoClientForwarded
  **/
  @JsonProperty("indirizzo_client_forwarded")
  @Valid
  public String getIndirizzoClientForwarded() {
    return this.indirizzoClientForwarded;
  }

  public void setIndirizzoClientForwarded(String indirizzoClientForwarded) {
    this.indirizzoClientForwarded = indirizzoClientForwarded;
  }

  public TransazioneInformazioniMittente indirizzoClientForwarded(String indirizzoClientForwarded) {
    this.indirizzoClientForwarded = indirizzoClientForwarded;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneInformazioniMittente {\n");
    
    sb.append("    principal: ").append(TransazioneInformazioniMittente.toIndentedString(this.principal)).append("\n");
    sb.append("    fruitore: ").append(TransazioneInformazioniMittente.toIndentedString(this.fruitore)).append("\n");
    sb.append("    utente: ").append(TransazioneInformazioniMittente.toIndentedString(this.utente)).append("\n");
    sb.append("    indirizzoClient: ").append(TransazioneInformazioniMittente.toIndentedString(this.indirizzoClient)).append("\n");
    sb.append("    indirizzoClientForwarded: ").append(TransazioneInformazioniMittente.toIndentedString(this.indirizzoClientForwarded)).append("\n");
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

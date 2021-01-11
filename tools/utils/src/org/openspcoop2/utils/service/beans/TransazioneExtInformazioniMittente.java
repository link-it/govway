/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
 @XmlType(name = "TransazioneExtInformazioniMittente", propOrder =
    { "credenziali", "token", "informazioniToken"
})


public class TransazioneExtInformazioniMittente extends TransazioneExtInformazioniMittenteBase {
  @XmlElement(name="credenziali")
  
  @Schema(description = "")
  private String credenziali = null;
  @XmlElement(name="token")
  
  @Schema(description = "")
  private byte[] token = null;
  @XmlElement(name="informazioni_token")
  
  @Schema(description = "")
  private TransazioneExtInformazioniToken informazioniToken = null;
 /**
   * Get credenziali
   * @return credenziali
  **/
  @JsonProperty("credenziali")
  @Valid
  public String getCredenziali() {
    return this.credenziali;
  }

  public void setCredenziali(String credenziali) {
    this.credenziali = credenziali;
  }

  public TransazioneExtInformazioniMittente credenziali(String credenziali) {
    this.credenziali = credenziali;
    return this;
  }

 /**
   * Get token
   * @return token
  **/
  @JsonProperty("token")
  @Valid
  public byte[] getToken() {
    return this.token;
  }

  public void setToken(byte[] token) {
    this.token = token;
  }

  public TransazioneExtInformazioniMittente token(byte[] token) {
    this.token = token;
    return this;
  }

 /**
   * Get informazioniToken
   * @return informazioniToken
  **/
  @JsonProperty("informazioni_token")
  @Valid
  public TransazioneExtInformazioniToken getInformazioniToken() {
    return this.informazioniToken;
  }

  public void setInformazioniToken(TransazioneExtInformazioniToken informazioniToken) {
    this.informazioniToken = informazioniToken;
  }

  public TransazioneExtInformazioniMittente informazioniToken(TransazioneExtInformazioniToken informazioniToken) {
    this.informazioniToken = informazioniToken;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtInformazioniMittente {\n");
    sb.append("    ").append(TransazioneExtInformazioniMittente.toIndentedString(super.toString())).append("\n");
    sb.append("    credenziali: ").append(TransazioneExtInformazioniMittente.toIndentedString(this.credenziali)).append("\n");
    sb.append("    token: ").append(TransazioneExtInformazioniMittente.toIndentedString(this.token)).append("\n");
    sb.append("    informazioniToken: ").append(TransazioneExtInformazioniMittente.toIndentedString(this.informazioniToken)).append("\n");
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

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtInformazioniToken", propOrder =
    { "tokenId", "clientId", "issuer", "subject", "username", "mail", "purposeId"
})

@XmlRootElement(name="TransazioneExtInformazioniToken")
public class TransazioneExtInformazioniToken  {
  @XmlElement(name="token_id")
  
  @Schema(description = "")
  private String tokenId = null;
  @XmlElement(name="client_id")
  
  @Schema(example = "407408718192.apps.googleusercontent.com", description = "")
  private String clientId = null;
  @XmlElement(name="issuer")
  
  @Schema(description = "")
  private String issuer = null;
  @XmlElement(name="subject")
  
  @Schema(example = "10623565759265497689", description = "")
  private String subject = null;
  @XmlElement(name="username")
  
  @Schema(description = "")
  private String username = null;
  @XmlElement(name="mail")
  
  @Schema(description = "")
  private String mail = null;
  @XmlElement(name="purpose_id")
  
  @Schema(description = "")
  private String purposeId = null;
 /**
   * Get tokenId
   * @return tokenId
  **/
  @JsonProperty("token_id")
  @Valid
  public String getTokenId() {
    return this.tokenId;
  }

  public void setTokenId(String tokenId) {
    this.tokenId = tokenId;
  }

  public TransazioneExtInformazioniToken tokenId(String tokenId) {
    this.tokenId = tokenId;
    return this;
  }

 /**
   * Get clientId
   * @return clientId
  **/
  @JsonProperty("client_id")
  @Valid
  public String getClientId() {
    return this.clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public TransazioneExtInformazioniToken clientId(String clientId) {
    this.clientId = clientId;
    return this;
  }

 /**
   * Get issuer
   * @return issuer
  **/
  @JsonProperty("issuer")
  @Valid
  public String getIssuer() {
    return this.issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public TransazioneExtInformazioniToken issuer(String issuer) {
    this.issuer = issuer;
    return this;
  }

 /**
   * Get subject
   * @return subject
  **/
  @JsonProperty("subject")
  @Valid
  public String getSubject() {
    return this.subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public TransazioneExtInformazioniToken subject(String subject) {
    this.subject = subject;
    return this;
  }

 /**
   * Get username
   * @return username
  **/
  @JsonProperty("username")
  @Valid
  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public TransazioneExtInformazioniToken username(String username) {
    this.username = username;
    return this;
  }

 /**
   * Get mail
   * @return mail
  **/
  @JsonProperty("mail")
  @Valid
  public String getMail() {
    return this.mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public TransazioneExtInformazioniToken mail(String mail) {
    this.mail = mail;
    return this;
  }

 /**
   * Get purposeId
   * @return purposeId
  **/
  @JsonProperty("purpose_id")
  @Valid
  public String getPurposeId() {
    return this.purposeId;
  }

  public void setPurposeId(String purposeId) {
    this.purposeId = purposeId;
  }

  public TransazioneExtInformazioniToken purposeId(String purposeId) {
    this.purposeId = purposeId;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtInformazioniToken {\n");
    
    sb.append("    tokenId: ").append(TransazioneExtInformazioniToken.toIndentedString(this.tokenId)).append("\n");
    sb.append("    clientId: ").append(TransazioneExtInformazioniToken.toIndentedString(this.clientId)).append("\n");
    sb.append("    issuer: ").append(TransazioneExtInformazioniToken.toIndentedString(this.issuer)).append("\n");
    sb.append("    subject: ").append(TransazioneExtInformazioniToken.toIndentedString(this.subject)).append("\n");
    sb.append("    username: ").append(TransazioneExtInformazioniToken.toIndentedString(this.username)).append("\n");
    sb.append("    mail: ").append(TransazioneExtInformazioniToken.toIndentedString(this.mail)).append("\n");
    sb.append("    purposeId: ").append(TransazioneExtInformazioniToken.toIndentedString(this.purposeId)).append("\n");
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

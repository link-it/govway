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
package org.openspcoop2.core.config.rs.server.model;

import java.util.List;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class AuthenticationHttps  implements OneOfBaseCredenzialiCredenziali {
  
  @Schema(required = true, description = "")
  private ModalitaAccessoEnum modalitaAccesso = null;
  
  @Schema(required = true, description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "tipo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = AuthenticationHttpsCertificato.class, name = "certificato"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = AuthenticationHttpsConfigurazioneManuale.class, name = "configurazione-manuale")  })
  private OneOfAuthenticationHttpsCertificato certificato = null;
  
  @Schema(description = "")
  private List<AuthenticationHttpsBaseCertificato> certificati = null;
  
  @Schema(description = "")
  private AuthenticationTokenBase token = null;
 /**
   * Get modalitaAccesso
   * @return modalitaAccesso
  **/
  @Override
@JsonProperty("modalita_accesso")
  @NotNull
  @Valid
  public ModalitaAccessoEnum getModalitaAccesso() {
    return this.modalitaAccesso;
  }

  public void setModalitaAccesso(ModalitaAccessoEnum modalitaAccesso) {
    this.modalitaAccesso = modalitaAccesso;
  }

  public AuthenticationHttps modalitaAccesso(ModalitaAccessoEnum modalitaAccesso) {
    this.modalitaAccesso = modalitaAccesso;
    return this;
  }

 /**
   * Get certificato
   * @return certificato
  **/
  @JsonProperty("certificato")
  @NotNull
  @Valid
  public OneOfAuthenticationHttpsCertificato getCertificato() {
    return this.certificato;
  }

  public void setCertificato(OneOfAuthenticationHttpsCertificato certificato) {
    this.certificato = certificato;
  }

  public AuthenticationHttps certificato(OneOfAuthenticationHttpsCertificato certificato) {
    this.certificato = certificato;
    return this;
  }

 /**
   * Get certificati
   * @return certificati
  **/
  @JsonProperty("certificati")
  @Valid
  public List<AuthenticationHttpsBaseCertificato> getCertificati() {
    return this.certificati;
  }

  public void setCertificati(List<AuthenticationHttpsBaseCertificato> certificati) {
    this.certificati = certificati;
  }

  public AuthenticationHttps certificati(List<AuthenticationHttpsBaseCertificato> certificati) {
    this.certificati = certificati;
    return this;
  }

  public AuthenticationHttps addCertificatiItem(AuthenticationHttpsBaseCertificato certificatiItem) {
    this.certificati.add(certificatiItem);
    return this;
  }

 /**
   * Get token
   * @return token
  **/
  @JsonProperty("token")
  @Valid
  public AuthenticationTokenBase getToken() {
    return this.token;
  }

  public void setToken(AuthenticationTokenBase token) {
    this.token = token;
  }

  public AuthenticationHttps token(AuthenticationTokenBase token) {
    this.token = token;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticationHttps {\n");
    
    sb.append("    modalitaAccesso: ").append(AuthenticationHttps.toIndentedString(this.modalitaAccesso)).append("\n");
    sb.append("    certificato: ").append(AuthenticationHttps.toIndentedString(this.certificato)).append("\n");
    sb.append("    certificati: ").append(AuthenticationHttps.toIndentedString(this.certificati)).append("\n");
    sb.append("    token: ").append(AuthenticationHttps.toIndentedString(this.token)).append("\n");
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

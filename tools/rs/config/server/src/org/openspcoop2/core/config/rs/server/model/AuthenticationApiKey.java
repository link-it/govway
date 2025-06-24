/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class AuthenticationApiKey  implements OneOfBaseCredenzialiCredenziali {
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "")
  private ModalitaAccessoEnum modalitaAccesso = null;
  
  @Schema(description = "")
  private Boolean appId = false;
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

  public AuthenticationApiKey modalitaAccesso(ModalitaAccessoEnum modalitaAccesso) {
    this.modalitaAccesso = modalitaAccesso;
    return this;
  }

 /**
   * Get appId
   * @return appId
  **/
  @JsonProperty("app_id")
  @Valid
  public Boolean isAppId() {
    return this.appId;
  }

  public void setAppId(Boolean appId) {
    this.appId = appId;
  }

  public AuthenticationApiKey appId(Boolean appId) {
    this.appId = appId;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticationApiKey {\n");
    
    sb.append("    modalitaAccesso: ").append(AuthenticationApiKey.toIndentedString(this.modalitaAccesso)).append("\n");
    sb.append("    appId: ").append(AuthenticationApiKey.toIndentedString(this.appId)).append("\n");
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

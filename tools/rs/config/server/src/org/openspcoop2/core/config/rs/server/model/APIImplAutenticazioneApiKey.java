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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class APIImplAutenticazioneApiKey  implements OneOfAPIImplAutenticazione, OneOfControlloAccessiAutenticazioneAutenticazione, OneOfGruppoNuovaConfigurazioneAutenticazione {
  
  @Schema(required = true, description = "")
  private TipoAutenticazioneEnum tipo = null;
  
  @Schema(description = "")
  private Boolean appId = false;
  
  @Schema(description = "")
  private APIImplAutenticazioneApiKeyPosizione posizione = null;
  
  @Schema(description = "")
  private APIImplAutenticazioneApiKeyConfig apiKeyNomi = null;
  
  @Schema(description = "")
  private APIImplAutenticazioneApiKeyConfig appIdNomi = null;
  
  @Schema(description = "")
  private Boolean apiKeyForward = false;
  
  @Schema(description = "")
  private Boolean appIdForward = false;
  
  @Schema(description = "")
  private Boolean opzionale = false;
 /**
   * Get tipo
   * @return tipo
  **/
  @Override
@JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoAutenticazioneEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoAutenticazioneEnum tipo) {
    this.tipo = tipo;
  }

  public APIImplAutenticazioneApiKey tipo(TipoAutenticazioneEnum tipo) {
    this.tipo = tipo;
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

  public APIImplAutenticazioneApiKey appId(Boolean appId) {
    this.appId = appId;
    return this;
  }

 /**
   * Get posizione
   * @return posizione
  **/
  @JsonProperty("posizione")
  @Valid
  public APIImplAutenticazioneApiKeyPosizione getPosizione() {
    return this.posizione;
  }

  public void setPosizione(APIImplAutenticazioneApiKeyPosizione posizione) {
    this.posizione = posizione;
  }

  public APIImplAutenticazioneApiKey posizione(APIImplAutenticazioneApiKeyPosizione posizione) {
    this.posizione = posizione;
    return this;
  }

 /**
   * Get apiKeyNomi
   * @return apiKeyNomi
  **/
  @JsonProperty("api_key_nomi")
  @Valid
  public APIImplAutenticazioneApiKeyConfig getApiKeyNomi() {
    return this.apiKeyNomi;
  }

  public void setApiKeyNomi(APIImplAutenticazioneApiKeyConfig apiKeyNomi) {
    this.apiKeyNomi = apiKeyNomi;
  }

  public APIImplAutenticazioneApiKey apiKeyNomi(APIImplAutenticazioneApiKeyConfig apiKeyNomi) {
    this.apiKeyNomi = apiKeyNomi;
    return this;
  }

 /**
   * Get appIdNomi
   * @return appIdNomi
  **/
  @JsonProperty("app_id_nomi")
  @Valid
  public APIImplAutenticazioneApiKeyConfig getAppIdNomi() {
    return this.appIdNomi;
  }

  public void setAppIdNomi(APIImplAutenticazioneApiKeyConfig appIdNomi) {
    this.appIdNomi = appIdNomi;
  }

  public APIImplAutenticazioneApiKey appIdNomi(APIImplAutenticazioneApiKeyConfig appIdNomi) {
    this.appIdNomi = appIdNomi;
    return this;
  }

 /**
   * Get apiKeyForward
   * @return apiKeyForward
  **/
  @JsonProperty("api_key_forward")
  @Valid
  public Boolean isApiKeyForward() {
    return this.apiKeyForward;
  }

  public void setApiKeyForward(Boolean apiKeyForward) {
    this.apiKeyForward = apiKeyForward;
  }

  public APIImplAutenticazioneApiKey apiKeyForward(Boolean apiKeyForward) {
    this.apiKeyForward = apiKeyForward;
    return this;
  }

 /**
   * Get appIdForward
   * @return appIdForward
  **/
  @JsonProperty("app_id_forward")
  @Valid
  public Boolean isAppIdForward() {
    return this.appIdForward;
  }

  public void setAppIdForward(Boolean appIdForward) {
    this.appIdForward = appIdForward;
  }

  public APIImplAutenticazioneApiKey appIdForward(Boolean appIdForward) {
    this.appIdForward = appIdForward;
    return this;
  }

 /**
   * Get opzionale
   * @return opzionale
  **/
  @JsonProperty("opzionale")
  @Valid
  public Boolean isOpzionale() {
    return this.opzionale;
  }

  public void setOpzionale(Boolean opzionale) {
    this.opzionale = opzionale;
  }

  public APIImplAutenticazioneApiKey opzionale(Boolean opzionale) {
    this.opzionale = opzionale;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImplAutenticazioneApiKey {\n");
    
    sb.append("    tipo: ").append(APIImplAutenticazioneApiKey.toIndentedString(this.tipo)).append("\n");
    sb.append("    appId: ").append(APIImplAutenticazioneApiKey.toIndentedString(this.appId)).append("\n");
    sb.append("    posizione: ").append(APIImplAutenticazioneApiKey.toIndentedString(this.posizione)).append("\n");
    sb.append("    apiKeyNomi: ").append(APIImplAutenticazioneApiKey.toIndentedString(this.apiKeyNomi)).append("\n");
    sb.append("    appIdNomi: ").append(APIImplAutenticazioneApiKey.toIndentedString(this.appIdNomi)).append("\n");
    sb.append("    apiKeyForward: ").append(APIImplAutenticazioneApiKey.toIndentedString(this.apiKeyForward)).append("\n");
    sb.append("    appIdForward: ").append(APIImplAutenticazioneApiKey.toIndentedString(this.appIdForward)).append("\n");
    sb.append("    opzionale: ").append(APIImplAutenticazioneApiKey.toIndentedString(this.opzionale)).append("\n");
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

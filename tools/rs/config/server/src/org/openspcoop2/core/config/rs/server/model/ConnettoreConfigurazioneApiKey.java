/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

public class ConnettoreConfigurazioneApiKey  {
  
  @Schema(required = true, description = "")
  private String apiKey = null;
  
  @Schema(description = "")
  private String apiKeyHeader = null;
  
  @Schema(description = "")
  private String appId = null;
  
  @Schema(description = "")
  private String appIdHeader = null;
 /**
   * Get apiKey
   * @return apiKey
  **/
  @JsonProperty("api_key")
  @NotNull
  @Valid
  public String getApiKey() {
    return this.apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public ConnettoreConfigurazioneApiKey apiKey(String apiKey) {
    this.apiKey = apiKey;
    return this;
  }

 /**
   * Get apiKeyHeader
   * @return apiKeyHeader
  **/
  @JsonProperty("api_key_header")
  @Valid
 @Size(max=255)  public String getApiKeyHeader() {
    return this.apiKeyHeader;
  }

  public void setApiKeyHeader(String apiKeyHeader) {
    this.apiKeyHeader = apiKeyHeader;
  }

  public ConnettoreConfigurazioneApiKey apiKeyHeader(String apiKeyHeader) {
    this.apiKeyHeader = apiKeyHeader;
    return this;
  }

 /**
   * Get appId
   * @return appId
  **/
  @JsonProperty("app_id")
  @Valid
  public String getAppId() {
    return this.appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public ConnettoreConfigurazioneApiKey appId(String appId) {
    this.appId = appId;
    return this;
  }

 /**
   * Get appIdHeader
   * @return appIdHeader
  **/
  @JsonProperty("app_id_header")
  @Valid
 @Size(max=255)  public String getAppIdHeader() {
    return this.appIdHeader;
  }

  public void setAppIdHeader(String appIdHeader) {
    this.appIdHeader = appIdHeader;
  }

  public ConnettoreConfigurazioneApiKey appIdHeader(String appIdHeader) {
    this.appIdHeader = appIdHeader;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreConfigurazioneApiKey {\n");
    
    sb.append("    apiKey: ").append(ConnettoreConfigurazioneApiKey.toIndentedString(this.apiKey)).append("\n");
    sb.append("    apiKeyHeader: ").append(ConnettoreConfigurazioneApiKey.toIndentedString(this.apiKeyHeader)).append("\n");
    sb.append("    appId: ").append(ConnettoreConfigurazioneApiKey.toIndentedString(this.appId)).append("\n");
    sb.append("    appIdHeader: ").append(ConnettoreConfigurazioneApiKey.toIndentedString(this.appIdHeader)).append("\n");
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

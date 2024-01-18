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

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class APIImplAutenticazioneApiKeyPosizione  {
  
  @Schema(description = "")
  private Boolean queryParameter = true;
  
  @Schema(description = "")
  private Boolean header = true;
  
  @Schema(description = "")
  private Boolean cookie = true;
 /**
   * Get queryParameter
   * @return queryParameter
  **/
  @JsonProperty("query_parameter")
  @Valid
  public Boolean isQueryParameter() {
    return this.queryParameter;
  }

  public void setQueryParameter(Boolean queryParameter) {
    this.queryParameter = queryParameter;
  }

  public APIImplAutenticazioneApiKeyPosizione queryParameter(Boolean queryParameter) {
    this.queryParameter = queryParameter;
    return this;
  }

 /**
   * Get header
   * @return header
  **/
  @JsonProperty("header")
  @Valid
  public Boolean isHeader() {
    return this.header;
  }

  public void setHeader(Boolean header) {
    this.header = header;
  }

  public APIImplAutenticazioneApiKeyPosizione header(Boolean header) {
    this.header = header;
    return this;
  }

 /**
   * Get cookie
   * @return cookie
  **/
  @JsonProperty("cookie")
  @Valid
  public Boolean isCookie() {
    return this.cookie;
  }

  public void setCookie(Boolean cookie) {
    this.cookie = cookie;
  }

  public APIImplAutenticazioneApiKeyPosizione cookie(Boolean cookie) {
    this.cookie = cookie;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImplAutenticazioneApiKeyPosizione {\n");
    
    sb.append("    queryParameter: ").append(APIImplAutenticazioneApiKeyPosizione.toIndentedString(this.queryParameter)).append("\n");
    sb.append("    header: ").append(APIImplAutenticazioneApiKeyPosizione.toIndentedString(this.header)).append("\n");
    sb.append("    cookie: ").append(APIImplAutenticazioneApiKeyPosizione.toIndentedString(this.cookie)).append("\n");
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

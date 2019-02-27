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
package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RegistrazioneMessaggiConfigurazioneRegola  {
  
  @Schema(required = true, description = "")
  private Boolean headers = null;
  
  @Schema(required = true, description = "")
  private Boolean body = null;
  
  @Schema(required = true, description = "")
  private Boolean attachments = null;
 /**
   * Get headers
   * @return headers
  **/
  @JsonProperty("headers")
  @NotNull
  @Valid
  public Boolean isHeaders() {
    return this.headers;
  }

  public void setHeaders(Boolean headers) {
    this.headers = headers;
  }

  public RegistrazioneMessaggiConfigurazioneRegola headers(Boolean headers) {
    this.headers = headers;
    return this;
  }

 /**
   * Get body
   * @return body
  **/
  @JsonProperty("body")
  @NotNull
  @Valid
  public Boolean isBody() {
    return this.body;
  }

  public void setBody(Boolean body) {
    this.body = body;
  }

  public RegistrazioneMessaggiConfigurazioneRegola body(Boolean body) {
    this.body = body;
    return this;
  }

 /**
   * Get attachments
   * @return attachments
  **/
  @JsonProperty("attachments")
  @NotNull
  @Valid
  public Boolean isAttachments() {
    return this.attachments;
  }

  public void setAttachments(Boolean attachments) {
    this.attachments = attachments;
  }

  public RegistrazioneMessaggiConfigurazioneRegola attachments(Boolean attachments) {
    this.attachments = attachments;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegistrazioneMessaggiConfigurazioneRegola {\n");
    
    sb.append("    headers: ").append(RegistrazioneMessaggiConfigurazioneRegola.toIndentedString(this.headers)).append("\n");
    sb.append("    body: ").append(RegistrazioneMessaggiConfigurazioneRegola.toIndentedString(this.body)).append("\n");
    sb.append("    attachments: ").append(RegistrazioneMessaggiConfigurazioneRegola.toIndentedString(this.attachments)).append("\n");
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

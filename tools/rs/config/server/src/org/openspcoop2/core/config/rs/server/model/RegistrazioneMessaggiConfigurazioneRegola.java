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

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class RegistrazioneMessaggiConfigurazioneRegola  {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private Boolean headers = null;
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private Boolean payload = null;
  
  @Schema(description = "")
  private Boolean payloadParsing = null;
  
  @Schema(description = "")
  private Boolean body = null;
  
  @Schema(description = "")
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
   * Get payload
   * @return payload
  **/
  @JsonProperty("payload")
  @NotNull
  @Valid
  public Boolean isPayload() {
    return this.payload;
  }

  public void setPayload(Boolean payload) {
    this.payload = payload;
  }

  public RegistrazioneMessaggiConfigurazioneRegola payload(Boolean payload) {
    this.payload = payload;
    return this;
  }

 /**
   * Get payloadParsing
   * @return payloadParsing
  **/
  @JsonProperty("payload_parsing")
  @Valid
  public Boolean isPayloadParsing() {
    return this.payloadParsing;
  }

  public void setPayloadParsing(Boolean payloadParsing) {
    this.payloadParsing = payloadParsing;
  }

  public RegistrazioneMessaggiConfigurazioneRegola payloadParsing(Boolean payloadParsing) {
    this.payloadParsing = payloadParsing;
    return this;
  }

 /**
   * Get body
   * @return body
  **/
  @JsonProperty("body")
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
    sb.append("    payload: ").append(RegistrazioneMessaggiConfigurazioneRegola.toIndentedString(this.payload)).append("\n");
    sb.append("    payloadParsing: ").append(RegistrazioneMessaggiConfigurazioneRegola.toIndentedString(this.payloadParsing)).append("\n");
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

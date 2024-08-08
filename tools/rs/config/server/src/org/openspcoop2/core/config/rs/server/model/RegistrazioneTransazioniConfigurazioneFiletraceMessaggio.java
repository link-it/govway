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

public class RegistrazioneTransazioniConfigurazioneFiletraceMessaggio  {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private Boolean headers = null;
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private Boolean payload = null;
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

  public RegistrazioneTransazioniConfigurazioneFiletraceMessaggio headers(Boolean headers) {
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

  public RegistrazioneTransazioniConfigurazioneFiletraceMessaggio payload(Boolean payload) {
    this.payload = payload;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegistrazioneTransazioniConfigurazioneFiletraceMessaggio {\n");
    
    sb.append("    headers: ").append(RegistrazioneTransazioniConfigurazioneFiletraceMessaggio.toIndentedString(this.headers)).append("\n");
    sb.append("    payload: ").append(RegistrazioneTransazioniConfigurazioneFiletraceMessaggio.toIndentedString(this.payload)).append("\n");
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

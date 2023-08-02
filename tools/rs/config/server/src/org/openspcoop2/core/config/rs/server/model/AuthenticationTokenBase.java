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

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class AuthenticationTokenBase  {
  
  @Schema(required = true, description = "")
  private String tokenPolicy = null;
  
  @Schema(required = true, description = "")
  private String identificativo = null;
 /**
   * Get tokenPolicy
   * @return tokenPolicy
  **/
  @JsonProperty("token_policy")
  @NotNull
  @Valid
 @Size(max=255)  public String getTokenPolicy() {
    return this.tokenPolicy;
  }

  public void setTokenPolicy(String tokenPolicy) {
    this.tokenPolicy = tokenPolicy;
  }

  public AuthenticationTokenBase tokenPolicy(String tokenPolicy) {
    this.tokenPolicy = tokenPolicy;
    return this;
  }

 /**
   * Get identificativo
   * @return identificativo
  **/
  @JsonProperty("identificativo")
  @NotNull
  @Valid
 @Size(max=2800)  public String getIdentificativo() {
    return this.identificativo;
  }

  public void setIdentificativo(String identificativo) {
    this.identificativo = identificativo;
  }

  public AuthenticationTokenBase identificativo(String identificativo) {
    this.identificativo = identificativo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticationTokenBase {\n");
    
    sb.append("    tokenPolicy: ").append(AuthenticationTokenBase.toIndentedString(this.tokenPolicy)).append("\n");
    sb.append("    identificativo: ").append(AuthenticationTokenBase.toIndentedString(this.identificativo)).append("\n");
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

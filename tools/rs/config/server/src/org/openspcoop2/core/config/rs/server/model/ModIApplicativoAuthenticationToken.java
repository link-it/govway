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

public class ModIApplicativoAuthenticationToken  {
  
  @Schema(description = "")
  private String tokenPolicy = null;
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private String identificativo = null;
  
  @Schema(description = "")
  private String kid = null;
 /**
   * Get tokenPolicy
   * @return tokenPolicy
  **/
  @JsonProperty("token_policy")
  @Valid
 @Size(max=255)  public String getTokenPolicy() {
    return this.tokenPolicy;
  }

  public void setTokenPolicy(String tokenPolicy) {
    this.tokenPolicy = tokenPolicy;
  }

  public ModIApplicativoAuthenticationToken tokenPolicy(String tokenPolicy) {
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

  public ModIApplicativoAuthenticationToken identificativo(String identificativo) {
    this.identificativo = identificativo;
    return this;
  }

 /**
   * Get kid
   * @return kid
  **/
  @JsonProperty("kid")
  @Valid
 @Size(max=4000)  public String getKid() {
    return this.kid;
  }

  public void setKid(String kid) {
    this.kid = kid;
  }

  public ModIApplicativoAuthenticationToken kid(String kid) {
    this.kid = kid;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModIApplicativoAuthenticationToken {\n");
    
    sb.append("    tokenPolicy: ").append(ModIApplicativoAuthenticationToken.toIndentedString(this.tokenPolicy)).append("\n");
    sb.append("    identificativo: ").append(ModIApplicativoAuthenticationToken.toIndentedString(this.identificativo)).append("\n");
    sb.append("    kid: ").append(ModIApplicativoAuthenticationToken.toIndentedString(this.kid)).append("\n");
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

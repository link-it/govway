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

public class RateLimitingPolicyIdentificativo  implements OneOfRateLimitingPolicyBaseConIdentificazioneConfigurazione {
  
  @Schema(required = true, description = "")
  private RateLimitingIdentificazionePolicyEnum identificazione = null;
  
  @Schema(required = true, description = "")
  private String policy = null;
 /**
   * Get identificazione
   * @return identificazione
  **/
  @Override
@JsonProperty("identificazione")
  @NotNull
  @Valid
  public RateLimitingIdentificazionePolicyEnum getIdentificazione() {
    return this.identificazione;
  }

  public void setIdentificazione(RateLimitingIdentificazionePolicyEnum identificazione) {
    this.identificazione = identificazione;
  }

  public RateLimitingPolicyIdentificativo identificazione(RateLimitingIdentificazionePolicyEnum identificazione) {
    this.identificazione = identificazione;
    return this;
  }

 /**
   * Get policy
   * @return policy
  **/
  @JsonProperty("policy")
  @NotNull
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getPolicy() {
    return this.policy;
  }

  public void setPolicy(String policy) {
    this.policy = policy;
  }

  public RateLimitingPolicyIdentificativo policy(String policy) {
    this.policy = policy;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateLimitingPolicyIdentificativo {\n");
    
    sb.append("    identificazione: ").append(RateLimitingPolicyIdentificativo.toIndentedString(this.identificazione)).append("\n");
    sb.append("    policy: ").append(RateLimitingPolicyIdentificativo.toIndentedString(this.policy)).append("\n");
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

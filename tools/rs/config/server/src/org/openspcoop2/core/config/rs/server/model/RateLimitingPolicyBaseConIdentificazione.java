/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.rs.server.model.RateLimitingIdentificazionePolicyEnum;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyBase;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RateLimitingPolicyBaseConIdentificazione extends RateLimitingPolicyBase {
  
  @Schema(required = true, description = "")
  private RateLimitingIdentificazionePolicyEnum identificazione = null;
  
  @Schema(description = "")
  private Object configurazione = null;
 /**
   * Get identificazione
   * @return identificazione
  **/
  @JsonProperty("identificazione")
  @NotNull
  @Valid
  public RateLimitingIdentificazionePolicyEnum getIdentificazione() {
    return this.identificazione;
  }

  public void setIdentificazione(RateLimitingIdentificazionePolicyEnum identificazione) {
    this.identificazione = identificazione;
  }

  public RateLimitingPolicyBaseConIdentificazione identificazione(RateLimitingIdentificazionePolicyEnum identificazione) {
    this.identificazione = identificazione;
    return this;
  }

 /**
   * Get configurazione
   * @return configurazione
  **/
  @JsonProperty("configurazione")
  @Valid
  public Object getConfigurazione() {
    return this.configurazione;
  }

  public void setConfigurazione(Object configurazione) {
    this.configurazione = configurazione;
  }

  public RateLimitingPolicyBaseConIdentificazione configurazione(Object configurazione) {
    this.configurazione = configurazione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateLimitingPolicyBaseConIdentificazione {\n");
    sb.append("    ").append(RateLimitingPolicyBaseConIdentificazione.toIndentedString(super.toString())).append("\n");
    sb.append("    identificazione: ").append(RateLimitingPolicyBaseConIdentificazione.toIndentedString(this.identificazione)).append("\n");
    sb.append("    configurazione: ").append(RateLimitingPolicyBaseConIdentificazione.toIndentedString(this.configurazione)).append("\n");
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

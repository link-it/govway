/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

public class BaseCredenziali  {
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "modalita_accesso", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = AuthenticationHttpBasic.class, name = "http-basic"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = AuthenticationHttps.class, name = "https"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = AuthenticationPrincipal.class, name = "principal"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = AuthenticationApiKey.class, name = "api-key"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = AuthenticationToken.class, name = "token")  })
  private OneOfBaseCredenzialiCredenziali credenziali = null;
 /**
   * Get credenziali
   * @return credenziali
  **/
  @JsonProperty("credenziali")
  @Valid
  public OneOfBaseCredenzialiCredenziali getCredenziali() {
    return this.credenziali;
  }

  public void setCredenziali(OneOfBaseCredenzialiCredenziali credenziali) {
    this.credenziali = credenziali;
  }

  public BaseCredenziali credenziali(OneOfBaseCredenzialiCredenziali credenziali) {
    this.credenziali = credenziali;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BaseCredenziali {\n");
    
    sb.append("    credenziali: ").append(BaseCredenziali.toIndentedString(this.credenziali)).append("\n");
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

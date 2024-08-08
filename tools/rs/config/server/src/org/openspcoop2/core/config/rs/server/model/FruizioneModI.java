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

public class FruizioneModI  {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "protocollo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FruizioneModIOAuth.class, name = "oauth"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FruizioneModISoap.class, name = "soap"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FruizioneModIRest.class, name = "rest")  })
  private OneOfFruizioneModIModi modi = null;
 /**
   * Get modi
   * @return modi
  **/
  @JsonProperty("modi")
  @NotNull
  @Valid
  public OneOfFruizioneModIModi getModi() {
    return this.modi;
  }

  public void setModi(OneOfFruizioneModIModi modi) {
    this.modi = modi;
  }

  public FruizioneModI modi(OneOfFruizioneModIModi modi) {
    this.modi = modi;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FruizioneModI {\n");
    
    sb.append("    modi: ").append(FruizioneModI.toIndentedString(this.modi)).append("\n");
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

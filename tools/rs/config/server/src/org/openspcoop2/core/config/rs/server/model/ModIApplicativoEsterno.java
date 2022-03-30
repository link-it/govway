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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ModIApplicativoEsterno  implements OneOfApplicativoModi {
  
  @Schema(required = true, description = "")
  private DominioEnum dominio = null;
  
  @Schema(description = "")
  private String replyAudience = null;
 /**
   * Get dominio
   * @return dominio
  **/
  @Override
@JsonProperty("dominio")
  @NotNull
  @Valid
  public DominioEnum getDominio() {
    return this.dominio;
  }

  public void setDominio(DominioEnum dominio) {
    this.dominio = dominio;
  }

  public ModIApplicativoEsterno dominio(DominioEnum dominio) {
    this.dominio = dominio;
    return this;
  }

 /**
   * Get replyAudience
   * @return replyAudience
  **/
  @JsonProperty("reply_audience")
  @Valid
 @Size(max=4000)  public String getReplyAudience() {
    return this.replyAudience;
  }

  public void setReplyAudience(String replyAudience) {
    this.replyAudience = replyAudience;
  }

  public ModIApplicativoEsterno replyAudience(String replyAudience) {
    this.replyAudience = replyAudience;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModIApplicativoEsterno {\n");
    
    sb.append("    dominio: ").append(ModIApplicativoEsterno.toIndentedString(this.dominio)).append("\n");
    sb.append("    replyAudience: ").append(ModIApplicativoEsterno.toIndentedString(this.replyAudience)).append("\n");
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

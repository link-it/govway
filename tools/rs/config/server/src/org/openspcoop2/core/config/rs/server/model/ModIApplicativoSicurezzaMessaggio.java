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

public class ModIApplicativoSicurezzaMessaggio  {
  
  @Schema(required = true, description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "tipologia", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModIKeyStoreArchive.class, name = "archivio"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModIKeyStoreFileApplicativo.class, name = "filesystem"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModIKeyStoreHSMApplicativo.class, name = "hsm")  })
  private OneOfModIApplicativoSicurezzaMessaggioKeystore keystore = null;
  
  @Schema(description = "")
  private String replyAudience = null;
  
  @Schema(description = "")
  private String urlX5u = null;
 /**
   * Get keystore
   * @return keystore
  **/
  @JsonProperty("keystore")
  @NotNull
  @Valid
  public OneOfModIApplicativoSicurezzaMessaggioKeystore getKeystore() {
    return this.keystore;
  }

  public void setKeystore(OneOfModIApplicativoSicurezzaMessaggioKeystore keystore) {
    this.keystore = keystore;
  }

  public ModIApplicativoSicurezzaMessaggio keystore(OneOfModIApplicativoSicurezzaMessaggioKeystore keystore) {
    this.keystore = keystore;
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

  public ModIApplicativoSicurezzaMessaggio replyAudience(String replyAudience) {
    this.replyAudience = replyAudience;
    return this;
  }

 /**
   * Get urlX5u
   * @return urlX5u
  **/
  @JsonProperty("url_x5u")
  @Valid
 @Size(max=4000)  public String getUrlX5u() {
    return this.urlX5u;
  }

  public void setUrlX5u(String urlX5u) {
    this.urlX5u = urlX5u;
  }

  public ModIApplicativoSicurezzaMessaggio urlX5u(String urlX5u) {
    this.urlX5u = urlX5u;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModIApplicativoSicurezzaMessaggio {\n");
    
    sb.append("    keystore: ").append(ModIApplicativoSicurezzaMessaggio.toIndentedString(this.keystore)).append("\n");
    sb.append("    replyAudience: ").append(ModIApplicativoSicurezzaMessaggio.toIndentedString(this.replyAudience)).append("\n");
    sb.append("    urlX5u: ").append(ModIApplicativoSicurezzaMessaggio.toIndentedString(this.urlX5u)).append("\n");
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

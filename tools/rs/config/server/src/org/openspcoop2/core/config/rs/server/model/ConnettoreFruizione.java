/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

public class ConnettoreFruizione  {
  
  @Schema(required = true, description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "tipo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreHttp.class, name = "http"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreFile.class, name = "file"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreJms.class, name = "jms"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreNull.class, name = "null"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreEcho.class, name = "echo"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettorePlugin.class, name = "plugin")  })
  private OneOfConnettoreFruizioneConnettore connettore = null;
 /**
   * Get connettore
   * @return connettore
  **/
  @JsonProperty("connettore")
  @NotNull
  @Valid
  public OneOfConnettoreFruizioneConnettore getConnettore() {
    return this.connettore;
  }

  public void setConnettore(OneOfConnettoreFruizioneConnettore connettore) {
    this.connettore = connettore;
  }

  public ConnettoreFruizione connettore(OneOfConnettoreFruizioneConnettore connettore) {
    this.connettore = connettore;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreFruizione {\n");
    
    sb.append("    connettore: ").append(ConnettoreFruizione.toIndentedString(this.connettore)).append("\n");
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

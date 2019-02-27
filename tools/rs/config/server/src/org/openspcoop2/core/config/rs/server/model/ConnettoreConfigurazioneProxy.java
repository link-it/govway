/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

public class ConnettoreConfigurazioneProxy  {
  
  @Schema(example = "proxy.ente.it", required = true, description = "")
  private String hostname = null;
  
  @Schema(example = "8080", required = true, description = "")
  private Integer porta = null;
  
  @Schema(example = "user", description = "")
  private String username = null;
  
  @Schema(example = "pwd", description = "")
  private String password = null;
 /**
   * Get hostname
   * @return hostname
  **/
  @JsonProperty("hostname")
  @NotNull
  @Valid
 @Size(max=255)  public String getHostname() {
    return this.hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public ConnettoreConfigurazioneProxy hostname(String hostname) {
    this.hostname = hostname;
    return this;
  }

 /**
   * Get porta
   * minimum: 1
   * maximum: 65535
   * @return porta
  **/
  @JsonProperty("porta")
  @NotNull
  @Valid
 @Min(1) @Max(65535)  public Integer getPorta() {
    return this.porta;
  }

  public void setPorta(Integer porta) {
    this.porta = porta;
  }

  public ConnettoreConfigurazioneProxy porta(Integer porta) {
    this.porta = porta;
    return this;
  }

 /**
   * Get username
   * @return username
  **/
  @JsonProperty("username")
  @Valid
 @Size(max=255)  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public ConnettoreConfigurazioneProxy username(String username) {
    this.username = username;
    return this;
  }

 /**
   * Get password
   * @return password
  **/
  @JsonProperty("password")
  @Valid
  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public ConnettoreConfigurazioneProxy password(String password) {
    this.password = password;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreConfigurazioneProxy {\n");
    
    sb.append("    hostname: ").append(ConnettoreConfigurazioneProxy.toIndentedString(this.hostname)).append("\n");
    sb.append("    porta: ").append(ConnettoreConfigurazioneProxy.toIndentedString(this.porta)).append("\n");
    sb.append("    username: ").append(ConnettoreConfigurazioneProxy.toIndentedString(this.username)).append("\n");
    sb.append("    password: ").append(ConnettoreConfigurazioneProxy.toIndentedString(this.password)).append("\n");
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

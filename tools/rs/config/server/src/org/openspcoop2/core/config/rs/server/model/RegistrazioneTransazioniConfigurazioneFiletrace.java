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

public class RegistrazioneTransazioniConfigurazioneFiletrace extends RegistrazioneTransazioniConfigurazioneBase {
  
  @Schema(description = "")
  private String configPath = null;
  
  @Schema(description = "")
  private RegistrazioneTransazioniConfigurazioneFiletraceMessaggio dumpClient = null;
  
  @Schema(description = "")
  private RegistrazioneTransazioniConfigurazioneFiletraceMessaggio dumpServer = null;
 /**
   * Get configPath
   * @return configPath
  **/
  @JsonProperty("config_path")
  @Valid
 @Size(max=255)  public String getConfigPath() {
    return this.configPath;
  }

  public void setConfigPath(String configPath) {
    this.configPath = configPath;
  }

  public RegistrazioneTransazioniConfigurazioneFiletrace configPath(String configPath) {
    this.configPath = configPath;
    return this;
  }

 /**
   * Get dumpClient
   * @return dumpClient
  **/
  @JsonProperty("dump_client")
  @Valid
  public RegistrazioneTransazioniConfigurazioneFiletraceMessaggio getDumpClient() {
    return this.dumpClient;
  }

  public void setDumpClient(RegistrazioneTransazioniConfigurazioneFiletraceMessaggio dumpClient) {
    this.dumpClient = dumpClient;
  }

  public RegistrazioneTransazioniConfigurazioneFiletrace dumpClient(RegistrazioneTransazioniConfigurazioneFiletraceMessaggio dumpClient) {
    this.dumpClient = dumpClient;
    return this;
  }

 /**
   * Get dumpServer
   * @return dumpServer
  **/
  @JsonProperty("dump_server")
  @Valid
  public RegistrazioneTransazioniConfigurazioneFiletraceMessaggio getDumpServer() {
    return this.dumpServer;
  }

  public void setDumpServer(RegistrazioneTransazioniConfigurazioneFiletraceMessaggio dumpServer) {
    this.dumpServer = dumpServer;
  }

  public RegistrazioneTransazioniConfigurazioneFiletrace dumpServer(RegistrazioneTransazioniConfigurazioneFiletraceMessaggio dumpServer) {
    this.dumpServer = dumpServer;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegistrazioneTransazioniConfigurazioneFiletrace {\n");
    sb.append("    ").append(RegistrazioneTransazioniConfigurazioneFiletrace.toIndentedString(super.toString())).append("\n");
    sb.append("    configPath: ").append(RegistrazioneTransazioniConfigurazioneFiletrace.toIndentedString(this.configPath)).append("\n");
    sb.append("    dumpClient: ").append(RegistrazioneTransazioniConfigurazioneFiletrace.toIndentedString(this.dumpClient)).append("\n");
    sb.append("    dumpServer: ").append(RegistrazioneTransazioniConfigurazioneFiletrace.toIndentedString(this.dumpServer)).append("\n");
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

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

import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttpsClient;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttpsServer;
import org.openspcoop2.core.config.rs.server.model.SslTipologiaEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ConnettoreConfigurazioneHttps  {
  
  @Schema(required = true, description = "")
  private SslTipologiaEnum tipologia = null;
  
  @Schema(example = "false", description = "")
  private Boolean hostnameVerifier = true;
  
  @Schema(example = "false", description = "")
  private Boolean trustAllServerCerts = false;
  
  @Schema(description = "")
  private ConnettoreConfigurazioneHttpsServer server = null;
  
  @Schema(description = "")
  private ConnettoreConfigurazioneHttpsClient client = null;
 /**
   * Get tipologia
   * @return tipologia
  **/
  @JsonProperty("tipologia")
  @NotNull
  @Valid
  public SslTipologiaEnum getTipologia() {
    return this.tipologia;
  }

  public void setTipologia(SslTipologiaEnum tipologia) {
    this.tipologia = tipologia;
  }

  public ConnettoreConfigurazioneHttps tipologia(SslTipologiaEnum tipologia) {
    this.tipologia = tipologia;
    return this;
  }

 /**
   * Get hostnameVerifier
   * @return hostnameVerifier
  **/
  @JsonProperty("hostname_verifier")
  @Valid
  public Boolean isHostnameVerifier() {
    return this.hostnameVerifier;
  }

  public void setHostnameVerifier(Boolean hostnameVerifier) {
    this.hostnameVerifier = hostnameVerifier;
  }

  public ConnettoreConfigurazioneHttps hostnameVerifier(Boolean hostnameVerifier) {
    this.hostnameVerifier = hostnameVerifier;
    return this;
  }

 /**
   * Get trustAllServerCerts
   * @return trustAllServerCerts
  **/
  @JsonProperty("trust_all_server_certs")
  @Valid
  public Boolean isTrustAllServerCerts() {
    return this.trustAllServerCerts;
  }

  public void setTrustAllServerCerts(Boolean trustAllServerCerts) {
    this.trustAllServerCerts = trustAllServerCerts;
  }

  public ConnettoreConfigurazioneHttps trustAllServerCerts(Boolean trustAllServerCerts) {
    this.trustAllServerCerts = trustAllServerCerts;
    return this;
  }

 /**
   * Get server
   * @return server
  **/
  @JsonProperty("server")
  @Valid
  public ConnettoreConfigurazioneHttpsServer getServer() {
    return this.server;
  }

  public void setServer(ConnettoreConfigurazioneHttpsServer server) {
    this.server = server;
  }

  public ConnettoreConfigurazioneHttps server(ConnettoreConfigurazioneHttpsServer server) {
    this.server = server;
    return this;
  }

 /**
   * Get client
   * @return client
  **/
  @JsonProperty("client")
  @Valid
  public ConnettoreConfigurazioneHttpsClient getClient() {
    return this.client;
  }

  public void setClient(ConnettoreConfigurazioneHttpsClient client) {
    this.client = client;
  }

  public ConnettoreConfigurazioneHttps client(ConnettoreConfigurazioneHttpsClient client) {
    this.client = client;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreConfigurazioneHttps {\n");
    
    sb.append("    tipologia: ").append(ConnettoreConfigurazioneHttps.toIndentedString(this.tipologia)).append("\n");
    sb.append("    hostnameVerifier: ").append(ConnettoreConfigurazioneHttps.toIndentedString(this.hostnameVerifier)).append("\n");
    sb.append("    trustAllServerCerts: ").append(ConnettoreConfigurazioneHttps.toIndentedString(this.trustAllServerCerts)).append("\n");
    sb.append("    server: ").append(ConnettoreConfigurazioneHttps.toIndentedString(this.server)).append("\n");
    sb.append("    client: ").append(ConnettoreConfigurazioneHttps.toIndentedString(this.client)).append("\n");
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

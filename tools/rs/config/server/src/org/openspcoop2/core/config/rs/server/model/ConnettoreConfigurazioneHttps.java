package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttpsClient;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttpsServer;
import org.openspcoop2.core.config.rs.server.model.SslTipologiaEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConnettoreConfigurazioneHttps  {
  
  @Schema(required = true, description = "")
  private SslTipologiaEnum tipologia = null;
  
  @Schema(description = "")
  private Boolean hostnameVerifier = true;
  
  @Schema(required = true, description = "")
  private ConnettoreConfigurazioneHttpsServer server = null;
  
  @Schema(description = "")
  private ConnettoreConfigurazioneHttpsClient client = null;
 /**
   * Get tipologia
   * @return tipologia
  **/
  @JsonProperty("tipologia")
  @NotNull
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
   * Get server
   * @return server
  **/
  @JsonProperty("server")
  @NotNull
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

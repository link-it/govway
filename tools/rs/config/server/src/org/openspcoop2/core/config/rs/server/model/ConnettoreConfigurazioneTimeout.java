package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConnettoreConfigurazioneTimeout  {
  
  @Schema(example = "10000", required = true, description = "tempo massimo (in millisecondi) per instaurare una connessione")
 /**
   * tempo massimo (in millisecondi) per instaurare una connessione  
  **/
  private Integer connectionTimeout = null;
  
  @Schema(example = "150000", required = true, description = "tempo massimo (in millisecondi) per ricevere una risposta")
 /**
   * tempo massimo (in millisecondi) per ricevere una risposta  
  **/
  private Integer connectionReadTimeout = null;
  
  @Schema(example = "10000", required = true, description = "tempo medio (in millisecondi) atteso per ricevere una risposta")
 /**
   * tempo medio (in millisecondi) atteso per ricevere una risposta  
  **/
  private Integer tempoMedioRisposta = null;
 /**
   * tempo massimo (in millisecondi) per instaurare una connessione
   * @return connectionTimeout
  **/
  @JsonProperty("connection_timeout")
  @NotNull
  public Integer getConnectionTimeout() {
    return this.connectionTimeout;
  }

  public void setConnectionTimeout(Integer connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public ConnettoreConfigurazioneTimeout connectionTimeout(Integer connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
    return this;
  }

 /**
   * tempo massimo (in millisecondi) per ricevere una risposta
   * @return connectionReadTimeout
  **/
  @JsonProperty("connection_read_timeout")
  @NotNull
  public Integer getConnectionReadTimeout() {
    return this.connectionReadTimeout;
  }

  public void setConnectionReadTimeout(Integer connectionReadTimeout) {
    this.connectionReadTimeout = connectionReadTimeout;
  }

  public ConnettoreConfigurazioneTimeout connectionReadTimeout(Integer connectionReadTimeout) {
    this.connectionReadTimeout = connectionReadTimeout;
    return this;
  }

 /**
   * tempo medio (in millisecondi) atteso per ricevere una risposta
   * @return tempoMedioRisposta
  **/
  @JsonProperty("tempo_medio_risposta")
  @NotNull
  public Integer getTempoMedioRisposta() {
    return this.tempoMedioRisposta;
  }

  public void setTempoMedioRisposta(Integer tempoMedioRisposta) {
    this.tempoMedioRisposta = tempoMedioRisposta;
  }

  public ConnettoreConfigurazioneTimeout tempoMedioRisposta(Integer tempoMedioRisposta) {
    this.tempoMedioRisposta = tempoMedioRisposta;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreConfigurazioneTimeout {\n");
    
    sb.append("    connectionTimeout: ").append(ConnettoreConfigurazioneTimeout.toIndentedString(this.connectionTimeout)).append("\n");
    sb.append("    connectionReadTimeout: ").append(ConnettoreConfigurazioneTimeout.toIndentedString(this.connectionReadTimeout)).append("\n");
    sb.append("    tempoMedioRisposta: ").append(ConnettoreConfigurazioneTimeout.toIndentedString(this.tempoMedioRisposta)).append("\n");
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

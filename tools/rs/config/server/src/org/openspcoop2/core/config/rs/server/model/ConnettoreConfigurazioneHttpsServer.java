package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.KeystoreEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ConnettoreConfigurazioneHttpsServer  {
  
  @Schema(example = "/path/to/truststore", required = true, description = "")
  private String truststorePath = null;
  
  @Schema(required = true, description = "")
  private KeystoreEnum truststoreTipo = null;
  
  @Schema(example = "pwd", required = true, description = "")
  private String truststorePassword = null;
  
  @Schema(example = "PKIX", description = "")
  private String algoritmo = "PKIX";
 /**
   * Get truststorePath
   * @return truststorePath
  **/
  @JsonProperty("truststore_path")
  @NotNull
  @Valid
 @Size(max=255)  public String getTruststorePath() {
    return this.truststorePath;
  }

  public void setTruststorePath(String truststorePath) {
    this.truststorePath = truststorePath;
  }

  public ConnettoreConfigurazioneHttpsServer truststorePath(String truststorePath) {
    this.truststorePath = truststorePath;
    return this;
  }

 /**
   * Get truststoreTipo
   * @return truststoreTipo
  **/
  @JsonProperty("truststore_tipo")
  @NotNull
  @Valid
  public KeystoreEnum getTruststoreTipo() {
    return this.truststoreTipo;
  }

  public void setTruststoreTipo(KeystoreEnum truststoreTipo) {
    this.truststoreTipo = truststoreTipo;
  }

  public ConnettoreConfigurazioneHttpsServer truststoreTipo(KeystoreEnum truststoreTipo) {
    this.truststoreTipo = truststoreTipo;
    return this;
  }

 /**
   * Get truststorePassword
   * @return truststorePassword
  **/
  @JsonProperty("truststore_password")
  @NotNull
  @Valid
  public String getTruststorePassword() {
    return this.truststorePassword;
  }

  public void setTruststorePassword(String truststorePassword) {
    this.truststorePassword = truststorePassword;
  }

  public ConnettoreConfigurazioneHttpsServer truststorePassword(String truststorePassword) {
    this.truststorePassword = truststorePassword;
    return this;
  }

 /**
   * Get algoritmo
   * @return algoritmo
  **/
  @JsonProperty("algoritmo")
  @Valid
 @Size(max=255)  public String getAlgoritmo() {
    return this.algoritmo;
  }

  public void setAlgoritmo(String algoritmo) {
    this.algoritmo = algoritmo;
  }

  public ConnettoreConfigurazioneHttpsServer algoritmo(String algoritmo) {
    this.algoritmo = algoritmo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreConfigurazioneHttpsServer {\n");
    
    sb.append("    truststorePath: ").append(ConnettoreConfigurazioneHttpsServer.toIndentedString(this.truststorePath)).append("\n");
    sb.append("    truststoreTipo: ").append(ConnettoreConfigurazioneHttpsServer.toIndentedString(this.truststoreTipo)).append("\n");
    sb.append("    truststorePassword: ").append(ConnettoreConfigurazioneHttpsServer.toIndentedString(this.truststorePassword)).append("\n");
    sb.append("    algoritmo: ").append(ConnettoreConfigurazioneHttpsServer.toIndentedString(this.algoritmo)).append("\n");
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

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

public class ConnettoreConfigurazioneHttpsClient  {
  
  @Schema(example = "/path/to/keystore", required = true, description = "")
  private String keystorePath = null;
  
  @Schema(required = true, description = "")
  private KeystoreEnum keystoreTipo = null;
  
  @Schema(example = "pwd", required = true, description = "")
  private String keystorePassword = null;
  
  @Schema(example = "pwd", required = true, description = "password della chiave privata")
 /**
   * password della chiave privata  
  **/
  private String keyPassword = null;
  
  @Schema(example = "pwd", description = "alias della chiave privata")
 /**
   * alias della chiave privata  
  **/
  private String keyAlias = null;
  
  @Schema(example = "SunX509", description = "")
  private String algoritmo = "SunX509";
 /**
   * Get keystorePath
   * @return keystorePath
  **/
  @JsonProperty("keystore_path")
  @NotNull
  @Valid
 @Size(max=255)  public String getKeystorePath() {
    return this.keystorePath;
  }

  public void setKeystorePath(String keystorePath) {
    this.keystorePath = keystorePath;
  }

  public ConnettoreConfigurazioneHttpsClient keystorePath(String keystorePath) {
    this.keystorePath = keystorePath;
    return this;
  }

 /**
   * Get keystoreTipo
   * @return keystoreTipo
  **/
  @JsonProperty("keystore_tipo")
  @NotNull
  @Valid
  public KeystoreEnum getKeystoreTipo() {
    return this.keystoreTipo;
  }

  public void setKeystoreTipo(KeystoreEnum keystoreTipo) {
    this.keystoreTipo = keystoreTipo;
  }

  public ConnettoreConfigurazioneHttpsClient keystoreTipo(KeystoreEnum keystoreTipo) {
    this.keystoreTipo = keystoreTipo;
    return this;
  }

 /**
   * Get keystorePassword
   * @return keystorePassword
  **/
  @JsonProperty("keystore_password")
  @NotNull
  @Valid
  public String getKeystorePassword() {
    return this.keystorePassword;
  }

  public void setKeystorePassword(String keystorePassword) {
    this.keystorePassword = keystorePassword;
  }

  public ConnettoreConfigurazioneHttpsClient keystorePassword(String keystorePassword) {
    this.keystorePassword = keystorePassword;
    return this;
  }

 /**
   * password della chiave privata
   * @return keyPassword
  **/
  @JsonProperty("key_password")
  @NotNull
  @Valid
  public String getKeyPassword() {
    return this.keyPassword;
  }

  public void setKeyPassword(String keyPassword) {
    this.keyPassword = keyPassword;
  }

  public ConnettoreConfigurazioneHttpsClient keyPassword(String keyPassword) {
    this.keyPassword = keyPassword;
    return this;
  }

 /**
   * alias della chiave privata
   * @return keyAlias
  **/
  @JsonProperty("key_alias")
  @Valid
 @Size(max=255)  public String getKeyAlias() {
    return this.keyAlias;
  }

  public void setKeyAlias(String keyAlias) {
    this.keyAlias = keyAlias;
  }

  public ConnettoreConfigurazioneHttpsClient keyAlias(String keyAlias) {
    this.keyAlias = keyAlias;
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

  public ConnettoreConfigurazioneHttpsClient algoritmo(String algoritmo) {
    this.algoritmo = algoritmo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreConfigurazioneHttpsClient {\n");
    
    sb.append("    keystorePath: ").append(ConnettoreConfigurazioneHttpsClient.toIndentedString(this.keystorePath)).append("\n");
    sb.append("    keystoreTipo: ").append(ConnettoreConfigurazioneHttpsClient.toIndentedString(this.keystoreTipo)).append("\n");
    sb.append("    keystorePassword: ").append(ConnettoreConfigurazioneHttpsClient.toIndentedString(this.keystorePassword)).append("\n");
    sb.append("    keyPassword: ").append(ConnettoreConfigurazioneHttpsClient.toIndentedString(this.keyPassword)).append("\n");
    sb.append("    keyAlias: ").append(ConnettoreConfigurazioneHttpsClient.toIndentedString(this.keyAlias)).append("\n");
    sb.append("    algoritmo: ").append(ConnettoreConfigurazioneHttpsClient.toIndentedString(this.algoritmo)).append("\n");
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

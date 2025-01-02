/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

public class BaseKeyStore  {
  
  @Schema(example = "pwd", requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private String keystorePassword = null;
  
  @Schema(example = "pwd", requiredMode = Schema.RequiredMode.REQUIRED, description = "password della chiave privata")
 /**
   * password della chiave privata  
  **/
  private String keyPassword = null;
  
  @Schema(example = "pwd", description = "alias della chiave privata")
 /**
   * alias della chiave privata  
  **/
  private String keyAlias = null;
  
  @Schema(description = "")
  private String keystoreByokPolicy = null;
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

  public BaseKeyStore keystorePassword(String keystorePassword) {
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

  public BaseKeyStore keyPassword(String keyPassword) {
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

  public BaseKeyStore keyAlias(String keyAlias) {
    this.keyAlias = keyAlias;
    return this;
  }

 /**
   * Get keystoreByokPolicy
   * @return keystoreByokPolicy
  **/
  @JsonProperty("keystore_byok_policy")
  @Valid
 @Size(max=255)  public String getKeystoreByokPolicy() {
    return this.keystoreByokPolicy;
  }

  public void setKeystoreByokPolicy(String keystoreByokPolicy) {
    this.keystoreByokPolicy = keystoreByokPolicy;
  }

  public BaseKeyStore keystoreByokPolicy(String keystoreByokPolicy) {
    this.keystoreByokPolicy = keystoreByokPolicy;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BaseKeyStore {\n");
    
    sb.append("    keystorePassword: ").append(BaseKeyStore.toIndentedString(this.keystorePassword)).append("\n");
    sb.append("    keyPassword: ").append(BaseKeyStore.toIndentedString(this.keyPassword)).append("\n");
    sb.append("    keyAlias: ").append(BaseKeyStore.toIndentedString(this.keyAlias)).append("\n");
    sb.append("    keystoreByokPolicy: ").append(BaseKeyStore.toIndentedString(this.keystoreByokPolicy)).append("\n");
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

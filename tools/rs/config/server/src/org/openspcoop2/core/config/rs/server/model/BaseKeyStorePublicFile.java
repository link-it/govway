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

public class BaseKeyStorePublicFile extends BaseKeyStore {
  
  @Schema(example = "/path/to/keystore", required = true, description = "")
  private String keystorePath = null;
  
  @Schema(example = "/path/to/public", description = "")
  private String publicKeyPath = null;
 /**
   * Get keystorePath
   * @return keystorePath
  **/
  @JsonProperty("keystore_path")
  @NotNull
  @Valid
 @Size(max=4000)  public String getKeystorePath() {
    return this.keystorePath;
  }

  public void setKeystorePath(String keystorePath) {
    this.keystorePath = keystorePath;
  }

  public BaseKeyStorePublicFile keystorePath(String keystorePath) {
    this.keystorePath = keystorePath;
    return this;
  }

 /**
   * Get publicKeyPath
   * @return publicKeyPath
  **/
  @JsonProperty("public_key_path")
  @Valid
 @Size(max=4000)  public String getPublicKeyPath() {
    return this.publicKeyPath;
  }

  public void setPublicKeyPath(String publicKeyPath) {
    this.publicKeyPath = publicKeyPath;
  }

  public BaseKeyStorePublicFile publicKeyPath(String publicKeyPath) {
    this.publicKeyPath = publicKeyPath;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BaseKeyStorePublicFile {\n");
    sb.append("    ").append(BaseKeyStorePublicFile.toIndentedString(super.toString())).append("\n");
    sb.append("    keystorePath: ").append(BaseKeyStorePublicFile.toIndentedString(this.keystorePath)).append("\n");
    sb.append("    publicKeyPath: ").append(BaseKeyStorePublicFile.toIndentedString(this.publicKeyPath)).append("\n");
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

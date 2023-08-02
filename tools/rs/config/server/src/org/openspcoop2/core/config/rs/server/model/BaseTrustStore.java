/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

public class BaseTrustStore  {
  
  @Schema(example = "/path/to/truststore", required = true, description = "")
  private String truststorePath = null;
  
  @Schema(example = "pwd", required = true, description = "")
  private String truststorePassword = null;
  
  @Schema(description = "")
  private String truststoreCrl = null;
  
  @Schema(description = "")
  private String truststoreOcspPolicy = null;
 /**
   * Get truststorePath
   * @return truststorePath
  **/
  @JsonProperty("truststore_path")
  @NotNull
  @Valid
 @Size(max=4000)  public String getTruststorePath() {
    return this.truststorePath;
  }

  public void setTruststorePath(String truststorePath) {
    this.truststorePath = truststorePath;
  }

  public BaseTrustStore truststorePath(String truststorePath) {
    this.truststorePath = truststorePath;
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

  public BaseTrustStore truststorePassword(String truststorePassword) {
    this.truststorePassword = truststorePassword;
    return this;
  }

 /**
   * Get truststoreCrl
   * @return truststoreCrl
  **/
  @JsonProperty("truststore_crl")
  @Valid
 @Size(max=4000)  public String getTruststoreCrl() {
    return this.truststoreCrl;
  }

  public void setTruststoreCrl(String truststoreCrl) {
    this.truststoreCrl = truststoreCrl;
  }

  public BaseTrustStore truststoreCrl(String truststoreCrl) {
    this.truststoreCrl = truststoreCrl;
    return this;
  }

 /**
   * Get truststoreOcspPolicy
   * @return truststoreOcspPolicy
  **/
  @JsonProperty("truststore_ocsp_policy")
  @Valid
 @Size(max=255)  public String getTruststoreOcspPolicy() {
    return this.truststoreOcspPolicy;
  }

  public void setTruststoreOcspPolicy(String truststoreOcspPolicy) {
    this.truststoreOcspPolicy = truststoreOcspPolicy;
  }

  public BaseTrustStore truststoreOcspPolicy(String truststoreOcspPolicy) {
    this.truststoreOcspPolicy = truststoreOcspPolicy;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BaseTrustStore {\n");
    
    sb.append("    truststorePath: ").append(BaseTrustStore.toIndentedString(this.truststorePath)).append("\n");
    sb.append("    truststorePassword: ").append(BaseTrustStore.toIndentedString(this.truststorePassword)).append("\n");
    sb.append("    truststoreCrl: ").append(BaseTrustStore.toIndentedString(this.truststoreCrl)).append("\n");
    sb.append("    truststoreOcspPolicy: ").append(BaseTrustStore.toIndentedString(this.truststoreOcspPolicy)).append("\n");
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

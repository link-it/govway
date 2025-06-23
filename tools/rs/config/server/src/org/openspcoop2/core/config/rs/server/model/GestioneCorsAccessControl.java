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

import java.util.List;
import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class GestioneCorsAccessControl  {
  
  @Schema(required = true, description = "")
  private Boolean allAllowOrigins = true;
  
  @Schema(description = "")
  private List<String> allowOrigins = null;
  
  @Schema(description = "")
  private Boolean allAllowHeaders = false;
  
  @Schema(description = "")
  private List<String> allowHeaders = null;
  
  @Schema(description = "")
  private Boolean allAllowMethods = false;
  
  @Schema(description = "")
  private List<HttpMethodEnum> allowMethods = null;
  
  @Schema(description = "")
  private Boolean allowCredentials = false;
  
  @Schema(description = "")
  private List<String> exposeHeaders = null;
  
  @Schema(description = "")
  private Boolean maxAge = false;
  
  @Schema(description = "")
  private Integer maxAgeSeconds = null;
 /**
   * Get allAllowOrigins
   * @return allAllowOrigins
  **/
  @JsonProperty("all_allow_origins")
  @NotNull
  @Valid
  public Boolean isAllAllowOrigins() {
    return this.allAllowOrigins;
  }

  public void setAllAllowOrigins(Boolean allAllowOrigins) {
    this.allAllowOrigins = allAllowOrigins;
  }

  public GestioneCorsAccessControl allAllowOrigins(Boolean allAllowOrigins) {
    this.allAllowOrigins = allAllowOrigins;
    return this;
  }

 /**
   * Get allowOrigins
   * @return allowOrigins
  **/
  @JsonProperty("allow_origins")
  @Valid
  public List<String> getAllowOrigins() {
    return this.allowOrigins;
  }

  public void setAllowOrigins(List<String> allowOrigins) {
    this.allowOrigins = allowOrigins;
  }

  public GestioneCorsAccessControl allowOrigins(List<String> allowOrigins) {
    this.allowOrigins = allowOrigins;
    return this;
  }

  public GestioneCorsAccessControl addAllowOriginsItem(String allowOriginsItem) {
    this.allowOrigins.add(allowOriginsItem);
    return this;
  }

 /**
   * Get allAllowHeaders
   * @return allAllowHeaders
  **/
  @JsonProperty("all_allow_headers")
  @Valid
  public Boolean isAllAllowHeaders() {
    return this.allAllowHeaders;
  }

  public void setAllAllowHeaders(Boolean allAllowHeaders) {
    this.allAllowHeaders = allAllowHeaders;
  }

  public GestioneCorsAccessControl allAllowHeaders(Boolean allAllowHeaders) {
    this.allAllowHeaders = allAllowHeaders;
    return this;
  }

 /**
   * Get allowHeaders
   * @return allowHeaders
  **/
  @JsonProperty("allow_headers")
  @Valid
  public List<String> getAllowHeaders() {
    return this.allowHeaders;
  }

  public void setAllowHeaders(List<String> allowHeaders) {
    this.allowHeaders = allowHeaders;
  }

  public GestioneCorsAccessControl allowHeaders(List<String> allowHeaders) {
    this.allowHeaders = allowHeaders;
    return this;
  }

  public GestioneCorsAccessControl addAllowHeadersItem(String allowHeadersItem) {
    this.allowHeaders.add(allowHeadersItem);
    return this;
  }

 /**
   * Get allAllowMethods
   * @return allAllowMethods
  **/
  @JsonProperty("all_allow_methods")
  @Valid
  public Boolean isAllAllowMethods() {
    return this.allAllowMethods;
  }

  public void setAllAllowMethods(Boolean allAllowMethods) {
    this.allAllowMethods = allAllowMethods;
  }

  public GestioneCorsAccessControl allAllowMethods(Boolean allAllowMethods) {
    this.allAllowMethods = allAllowMethods;
    return this;
  }

 /**
   * Get allowMethods
   * @return allowMethods
  **/
  @JsonProperty("allow_methods")
  @Valid
  public List<HttpMethodEnum> getAllowMethods() {
    return this.allowMethods;
  }

  public void setAllowMethods(List<HttpMethodEnum> allowMethods) {
    this.allowMethods = allowMethods;
  }

  public GestioneCorsAccessControl allowMethods(List<HttpMethodEnum> allowMethods) {
    this.allowMethods = allowMethods;
    return this;
  }

  public GestioneCorsAccessControl addAllowMethodsItem(HttpMethodEnum allowMethodsItem) {
    this.allowMethods.add(allowMethodsItem);
    return this;
  }

 /**
   * Get allowCredentials
   * @return allowCredentials
  **/
  @JsonProperty("allow_credentials")
  @Valid
  public Boolean isAllowCredentials() {
    return this.allowCredentials;
  }

  public void setAllowCredentials(Boolean allowCredentials) {
    this.allowCredentials = allowCredentials;
  }

  public GestioneCorsAccessControl allowCredentials(Boolean allowCredentials) {
    this.allowCredentials = allowCredentials;
    return this;
  }

 /**
   * Get exposeHeaders
   * @return exposeHeaders
  **/
  @JsonProperty("expose_headers")
  @Valid
  public List<String> getExposeHeaders() {
    return this.exposeHeaders;
  }

  public void setExposeHeaders(List<String> exposeHeaders) {
    this.exposeHeaders = exposeHeaders;
  }

  public GestioneCorsAccessControl exposeHeaders(List<String> exposeHeaders) {
    this.exposeHeaders = exposeHeaders;
    return this;
  }

  public GestioneCorsAccessControl addExposeHeadersItem(String exposeHeadersItem) {
    this.exposeHeaders.add(exposeHeadersItem);
    return this;
  }

 /**
   * Get maxAge
   * @return maxAge
  **/
  @JsonProperty("max_age")
  @Valid
  public Boolean isMaxAge() {
    return this.maxAge;
  }

  public void setMaxAge(Boolean maxAge) {
    this.maxAge = maxAge;
  }

  public GestioneCorsAccessControl maxAge(Boolean maxAge) {
    this.maxAge = maxAge;
    return this;
  }

 /**
   * Get maxAgeSeconds
   * @return maxAgeSeconds
  **/
  @JsonProperty("max_age_seconds")
  @Valid
  public Integer getMaxAgeSeconds() {
    return this.maxAgeSeconds;
  }

  public void setMaxAgeSeconds(Integer maxAgeSeconds) {
    this.maxAgeSeconds = maxAgeSeconds;
  }

  public GestioneCorsAccessControl maxAgeSeconds(Integer maxAgeSeconds) {
    this.maxAgeSeconds = maxAgeSeconds;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GestioneCorsAccessControl {\n");
    
    sb.append("    allAllowOrigins: ").append(GestioneCorsAccessControl.toIndentedString(this.allAllowOrigins)).append("\n");
    sb.append("    allowOrigins: ").append(GestioneCorsAccessControl.toIndentedString(this.allowOrigins)).append("\n");
    sb.append("    allAllowHeaders: ").append(GestioneCorsAccessControl.toIndentedString(this.allAllowHeaders)).append("\n");
    sb.append("    allowHeaders: ").append(GestioneCorsAccessControl.toIndentedString(this.allowHeaders)).append("\n");
    sb.append("    allAllowMethods: ").append(GestioneCorsAccessControl.toIndentedString(this.allAllowMethods)).append("\n");
    sb.append("    allowMethods: ").append(GestioneCorsAccessControl.toIndentedString(this.allowMethods)).append("\n");
    sb.append("    allowCredentials: ").append(GestioneCorsAccessControl.toIndentedString(this.allowCredentials)).append("\n");
    sb.append("    exposeHeaders: ").append(GestioneCorsAccessControl.toIndentedString(this.exposeHeaders)).append("\n");
    sb.append("    maxAge: ").append(GestioneCorsAccessControl.toIndentedString(this.maxAge)).append("\n");
    sb.append("    maxAgeSeconds: ").append(GestioneCorsAccessControl.toIndentedString(this.maxAgeSeconds)).append("\n");
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

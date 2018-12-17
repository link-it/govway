package org.openspcoop2.core.config.rs.server.model;

import java.util.List;
import org.openspcoop2.core.config.rs.server.model.HttpMethodEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GestioneCorsAccessControl  {
  
  @Schema(required = true, description = "")
  private Boolean allAllowOrigins = true;
  
  @Schema(description = "")
  private List<String> allowOrigins = null;
  
  @Schema(description = "")
  private List<String> allowHeaders = null;
  
  @Schema(description = "")
  private List<HttpMethodEnum> allowMethods = null;
  
  @Schema(description = "")
  private Boolean allowCredentials = false;
 /**
   * Get allAllowOrigins
   * @return allAllowOrigins
  **/
  @JsonProperty("all_allow_origins")
  @NotNull
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
   * Get allowHeaders
   * @return allowHeaders
  **/
  @JsonProperty("allow_headers")
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
   * Get allowMethods
   * @return allowMethods
  **/
  @JsonProperty("allow_methods")
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


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GestioneCorsAccessControl {\n");
    
    sb.append("    allAllowOrigins: ").append(GestioneCorsAccessControl.toIndentedString(this.allAllowOrigins)).append("\n");
    sb.append("    allowOrigins: ").append(GestioneCorsAccessControl.toIndentedString(this.allowOrigins)).append("\n");
    sb.append("    allowHeaders: ").append(GestioneCorsAccessControl.toIndentedString(this.allowHeaders)).append("\n");
    sb.append("    allowMethods: ").append(GestioneCorsAccessControl.toIndentedString(this.allowMethods)).append("\n");
    sb.append("    allowCredentials: ").append(GestioneCorsAccessControl.toIndentedString(this.allowCredentials)).append("\n");
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

package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationHttps  {
  
  @Schema(example = "cn=esterno", required = true, description = "")
  private String subject = null;
 /**
   * Get subject
   * @return subject
  **/
  @JsonProperty("subject")
  @NotNull
  public String getSubject() {
    return this.subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public AuthenticationHttps subject(String subject) {
    this.subject = subject;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticationHttps {\n");
    
    sb.append("    subject: ").append(AuthenticationHttps.toIndentedString(this.subject)).append("\n");
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

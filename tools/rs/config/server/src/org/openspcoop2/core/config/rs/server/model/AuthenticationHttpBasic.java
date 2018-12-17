package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationHttpBasic  {
  
  @Schema(example = "user", required = true, description = "")
  private String username = null;
  
  @Schema(example = "pwd", required = true, description = "")
  private String password = null;
 /**
   * Get username
   * @return username
  **/
  @JsonProperty("username")
  @NotNull
 @Size(max=255)  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public AuthenticationHttpBasic username(String username) {
    this.username = username;
    return this;
  }

 /**
   * Get password
   * @return password
  **/
  @JsonProperty("password")
  @NotNull
  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public AuthenticationHttpBasic password(String password) {
    this.password = password;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticationHttpBasic {\n");
    
    sb.append("    username: ").append(AuthenticationHttpBasic.toIndentedString(this.username)).append("\n");
    sb.append("    password: ").append(AuthenticationHttpBasic.toIndentedString(this.password)).append("\n");
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

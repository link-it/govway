package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationPrincipal  {
  
  @Schema(example = "idEsterno", required = true, description = "")
  private String userid = null;
 /**
   * Get userid
   * @return userid
  **/
  @JsonProperty("userid")
  @NotNull
 @Size(max=255)  public String getUserid() {
    return this.userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

  public AuthenticationPrincipal userid(String userid) {
    this.userid = userid;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticationPrincipal {\n");
    
    sb.append("    userid: ").append(AuthenticationPrincipal.toIndentedString(this.userid)).append("\n");
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

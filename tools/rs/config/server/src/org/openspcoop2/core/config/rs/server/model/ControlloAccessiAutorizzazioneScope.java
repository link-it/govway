package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ControlloAccessiAutorizzazioneScope  {
  
  @Schema(required = true, description = "")
  private String scope = null;
 /**
   * Get scope
   * @return scope
  **/
  @JsonProperty("scope")
  @NotNull
  public String getScope() {
    return this.scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public ControlloAccessiAutorizzazioneScope scope(String scope) {
    this.scope = scope;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ControlloAccessiAutorizzazioneScope {\n");
    
    sb.append("    scope: ").append(ControlloAccessiAutorizzazioneScope.toIndentedString(this.scope)).append("\n");
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

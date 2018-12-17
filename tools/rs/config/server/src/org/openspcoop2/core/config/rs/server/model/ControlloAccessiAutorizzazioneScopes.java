package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ControlloAccessiAutorizzazioneScopes  {
  
  @Schema(example = "[\"Scope1\",\"Scope2\",\"Scope3\"]", required = true, description = "")
  private List<String> scope = new ArrayList<String>();
 /**
   * Get scope
   * @return scope
  **/
  @JsonProperty("scope")
  @NotNull
  public List<String> getScope() {
    return this.scope;
  }

  public void setScope(List<String> scope) {
    this.scope = scope;
  }

  public ControlloAccessiAutorizzazioneScopes scope(List<String> scope) {
    this.scope = scope;
    return this;
  }

  public ControlloAccessiAutorizzazioneScopes addScopeItem(String scopeItem) {
    this.scope.add(scopeItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ControlloAccessiAutorizzazioneScopes {\n");
    
    sb.append("    scope: ").append(ControlloAccessiAutorizzazioneScopes.toIndentedString(this.scope)).append("\n");
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

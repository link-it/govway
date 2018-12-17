package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiImplStato  {
  
  @Schema(required = true, description = "")
  private Boolean abilitato = null;
 /**
   * Get abilitato
   * @return abilitato
  **/
  @JsonProperty("abilitato")
  @NotNull
  public Boolean isAbilitato() {
    return this.abilitato;
  }

  public void setAbilitato(Boolean abilitato) {
    this.abilitato = abilitato;
  }

  public ApiImplStato abilitato(Boolean abilitato) {
    this.abilitato = abilitato;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiImplStato {\n");
    
    sb.append("    abilitato: ").append(ApiImplStato.toIndentedString(this.abilitato)).append("\n");
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

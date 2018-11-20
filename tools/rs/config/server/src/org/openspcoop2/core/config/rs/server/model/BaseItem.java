package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseItem  {
  
  @Schema(description = "")
  private ProfiloEnum profilo = null;
 /**
   * Get profilo
   * @return profilo
  **/
  @JsonProperty("profilo")
  public ProfiloEnum getProfilo() {
    return this.profilo;
  }

  public void setProfilo(ProfiloEnum profilo) {
    this.profilo = profilo;
  }

  public BaseItem profilo(ProfiloEnum profilo) {
    this.profilo = profilo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BaseItem {\n");
    
    sb.append("    profilo: ").append(BaseItem.toIndentedString(this.profilo)).append("\n");
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

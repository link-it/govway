package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiInterfaccia  {
  
  @Schema(required = true, description = "")
  private byte[] interfaccia = null;
 /**
   * Get interfaccia
   * @return interfaccia
  **/
  @JsonProperty("interfaccia")
  @NotNull
  public byte[] getInterfaccia() {
    return this.interfaccia;
  }

  public void setInterfaccia(byte[] interfaccia) {
    this.interfaccia = interfaccia;
  }

  public ApiInterfaccia interfaccia(byte[] interfaccia) {
    this.interfaccia = interfaccia;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiInterfaccia {\n");
    
    sb.append("    interfaccia: ").append(ApiInterfaccia.toIndentedString(this.interfaccia)).append("\n");
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

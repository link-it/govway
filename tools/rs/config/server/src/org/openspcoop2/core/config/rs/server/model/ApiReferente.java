package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiReferente  {
  
  @Schema(required = true, description = "")
  private String referente = null;
 /**
   * Get referente
   * @return referente
  **/
  @JsonProperty("referente")
  @NotNull
  public String getReferente() {
    return this.referente;
  }

  public void setReferente(String referente) {
    this.referente = referente;
  }

  public ApiReferente referente(String referente) {
    this.referente = referente;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiReferente {\n");
    
    sb.append("    referente: ").append(ApiReferente.toIndentedString(this.referente)).append("\n");
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

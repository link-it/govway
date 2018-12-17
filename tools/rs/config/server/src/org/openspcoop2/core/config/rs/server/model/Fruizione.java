package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.APIImpl;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Fruizione extends APIImpl {
  
  @Schema(required = true, description = "")
  private String erogatore = null;
 /**
   * Get erogatore
   * @return erogatore
  **/
  @JsonProperty("erogatore")
  @NotNull
  public String getErogatore() {
    return this.erogatore;
  }

  public void setErogatore(String erogatore) {
    this.erogatore = erogatore;
  }

  public Fruizione erogatore(String erogatore) {
    this.erogatore = erogatore;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Fruizione {\n");
    sb.append("    ").append(Fruizione.toIndentedString(super.toString())).append("\n");
    sb.append("    erogatore: ").append(Fruizione.toIndentedString(this.erogatore)).append("\n");
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

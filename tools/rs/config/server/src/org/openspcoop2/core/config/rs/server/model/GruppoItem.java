package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.GruppoBase;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GruppoItem extends GruppoBase {
  
  @Schema(required = true, description = "")
  private Boolean predefinito = null;
 /**
   * Get predefinito
   * @return predefinito
  **/
  @JsonProperty("predefinito")
  @NotNull
  public Boolean isPredefinito() {
    return this.predefinito;
  }

  public void setPredefinito(Boolean predefinito) {
    this.predefinito = predefinito;
  }

  public GruppoItem predefinito(Boolean predefinito) {
    this.predefinito = predefinito;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GruppoItem {\n");
    sb.append("    ").append(GruppoItem.toIndentedString(super.toString())).append("\n");
    sb.append("    predefinito: ").append(GruppoItem.toIndentedString(this.predefinito)).append("\n");
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

package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.AllegatoGenericoItem;
import org.openspcoop2.core.config.rs.server.model.TipoSpecificaSemiformaleEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class AllegatoSpecificaSemiformaleItem extends AllegatoGenericoItem {
  
  @Schema(required = true, description = "")
  private TipoSpecificaSemiformaleEnum tipo = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoSpecificaSemiformaleEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoSpecificaSemiformaleEnum tipo) {
    this.tipo = tipo;
  }

  public AllegatoSpecificaSemiformaleItem tipo(TipoSpecificaSemiformaleEnum tipo) {
    this.tipo = tipo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AllegatoSpecificaSemiformaleItem {\n");
    sb.append("    ").append(AllegatoSpecificaSemiformaleItem.toIndentedString(super.toString())).append("\n");
    sb.append("    tipo: ").append(AllegatoSpecificaSemiformaleItem.toIndentedString(this.tipo)).append("\n");
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

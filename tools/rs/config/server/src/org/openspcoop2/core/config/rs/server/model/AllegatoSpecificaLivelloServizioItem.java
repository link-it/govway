package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.AllegatoGenericoItem;
import org.openspcoop2.core.config.rs.server.model.TipoSpecificaLivelloServizioEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class AllegatoSpecificaLivelloServizioItem extends AllegatoGenericoItem {
  
  @Schema(required = true, description = "")
  private TipoSpecificaLivelloServizioEnum tipo = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoSpecificaLivelloServizioEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoSpecificaLivelloServizioEnum tipo) {
    this.tipo = tipo;
  }

  public AllegatoSpecificaLivelloServizioItem tipo(TipoSpecificaLivelloServizioEnum tipo) {
    this.tipo = tipo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AllegatoSpecificaLivelloServizioItem {\n");
    sb.append("    ").append(AllegatoSpecificaLivelloServizioItem.toIndentedString(super.toString())).append("\n");
    sb.append("    tipo: ").append(AllegatoSpecificaLivelloServizioItem.toIndentedString(this.tipo)).append("\n");
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

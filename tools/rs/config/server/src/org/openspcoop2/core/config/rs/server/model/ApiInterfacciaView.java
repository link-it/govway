package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.BaseItem;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiInterfacciaView extends BaseItem {
  
  @Schema(required = true, description = "")
  private byte[] interfaccia = null;
  
  @Schema(required = true, description = "")
  private TipoApiEnum tipo = null;
  
  @Schema(example = "{\"formato\":\"OpenApi3.0\"}", required = true, description = "")
  private Object formato = null;
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

  public ApiInterfacciaView interfaccia(byte[] interfaccia) {
    this.interfaccia = interfaccia;
    return this;
  }

 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  public TipoApiEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoApiEnum tipo) {
    this.tipo = tipo;
  }

  public ApiInterfacciaView tipo(TipoApiEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get formato
   * @return formato
  **/
  @JsonProperty("formato")
  @NotNull
  public Object getFormato() {
    return this.formato;
  }

  public void setFormato(Object formato) {
    this.formato = formato;
  }

  public ApiInterfacciaView formato(Object formato) {
    this.formato = formato;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiInterfacciaView {\n");
    sb.append("    ").append(ApiInterfacciaView.toIndentedString(super.toString())).append("\n");
    sb.append("    interfaccia: ").append(ApiInterfacciaView.toIndentedString(this.interfaccia)).append("\n");
    sb.append("    tipo: ").append(ApiInterfacciaView.toIndentedString(this.tipo)).append("\n");
    sb.append("    formato: ").append(ApiInterfacciaView.toIndentedString(this.formato)).append("\n");
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

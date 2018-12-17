package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.GestioneCorsAccessControl;
import org.openspcoop2.core.config.rs.server.model.TipoGestioneCorsEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GestioneCors  {
  
  @Schema(required = true, description = "")
  private Boolean ridefinito = false;
  
  @Schema(description = "")
  private TipoGestioneCorsEnum tipo = null;
  
  @Schema(description = "")
  private GestioneCorsAccessControl accessControl = null;
 /**
   * Get ridefinito
   * @return ridefinito
  **/
  @JsonProperty("ridefinito")
  @NotNull
  public Boolean isRidefinito() {
    return this.ridefinito;
  }

  public void setRidefinito(Boolean ridefinito) {
    this.ridefinito = ridefinito;
  }

  public GestioneCors ridefinito(Boolean ridefinito) {
    this.ridefinito = ridefinito;
    return this;
  }

 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  public TipoGestioneCorsEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoGestioneCorsEnum tipo) {
    this.tipo = tipo;
  }

  public GestioneCors tipo(TipoGestioneCorsEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get accessControl
   * @return accessControl
  **/
  @JsonProperty("access_control")
  public GestioneCorsAccessControl getAccessControl() {
    return this.accessControl;
  }

  public void setAccessControl(GestioneCorsAccessControl accessControl) {
    this.accessControl = accessControl;
  }

  public GestioneCors accessControl(GestioneCorsAccessControl accessControl) {
    this.accessControl = accessControl;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GestioneCors {\n");
    
    sb.append("    ridefinito: ").append(GestioneCors.toIndentedString(this.ridefinito)).append("\n");
    sb.append("    tipo: ").append(GestioneCors.toIndentedString(this.tipo)).append("\n");
    sb.append("    accessControl: ").append(GestioneCors.toIndentedString(this.accessControl)).append("\n");
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

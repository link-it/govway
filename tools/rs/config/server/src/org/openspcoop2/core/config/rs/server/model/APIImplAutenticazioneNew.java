package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.TipoAutenticazioneNewEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class APIImplAutenticazioneNew  {
  
  @Schema(required = true, description = "")
  private TipoAutenticazioneNewEnum tipo = null;
  
  @Schema(description = "")
  private Boolean opzionale = false;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  public TipoAutenticazioneNewEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoAutenticazioneNewEnum tipo) {
    this.tipo = tipo;
  }

  public APIImplAutenticazioneNew tipo(TipoAutenticazioneNewEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get opzionale
   * @return opzionale
  **/
  @JsonProperty("opzionale")
  public Boolean isOpzionale() {
    return this.opzionale;
  }

  public void setOpzionale(Boolean opzionale) {
    this.opzionale = opzionale;
  }

  public APIImplAutenticazioneNew opzionale(Boolean opzionale) {
    this.opzionale = opzionale;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImplAutenticazioneNew {\n");
    
    sb.append("    tipo: ").append(APIImplAutenticazioneNew.toIndentedString(this.tipo)).append("\n");
    sb.append("    opzionale: ").append(APIImplAutenticazioneNew.toIndentedString(this.opzionale)).append("\n");
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

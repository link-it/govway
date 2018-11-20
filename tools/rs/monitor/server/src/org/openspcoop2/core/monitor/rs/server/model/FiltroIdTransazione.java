package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FiltroIdTransazione  {
  
  @Schema(example = "tr123", description = "")
  private String idTransazione = null;
 /**
   * Get idTransazione
   * @return idTransazione
  **/
  @JsonProperty("idTransazione")
  public String getIdTransazione() {
    return this.idTransazione;
  }

  public void setIdTransazione(String idTransazione) {
    this.idTransazione = idTransazione;
  }

  public FiltroIdTransazione idTransazione(String idTransazione) {
    this.idTransazione = idTransazione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroIdTransazione {\n");
    
    sb.append("    idTransazione: ").append(FiltroIdTransazione.toIndentedString(this.idTransazione)).append("\n");
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

package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazione;
import org.openspcoop2.core.config.rs.server.model.ApiImplConfigurazioneStato;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ControlloAccessiAutorizzazione extends ApiImplConfigurazioneStato {
  
  @Schema(description = "")
  private APIImplAutorizzazione autorizzazione = null;
 /**
   * Get autorizzazione
   * @return autorizzazione
  **/
  @JsonProperty("autorizzazione")
  @Valid
  public APIImplAutorizzazione getAutorizzazione() {
    return this.autorizzazione;
  }

  public void setAutorizzazione(APIImplAutorizzazione autorizzazione) {
    this.autorizzazione = autorizzazione;
  }

  public ControlloAccessiAutorizzazione autorizzazione(APIImplAutorizzazione autorizzazione) {
    this.autorizzazione = autorizzazione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ControlloAccessiAutorizzazione {\n");
    sb.append("    ").append(ControlloAccessiAutorizzazione.toIndentedString(super.toString())).append("\n");
    sb.append("    autorizzazione: ").append(ControlloAccessiAutorizzazione.toIndentedString(this.autorizzazione)).append("\n");
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

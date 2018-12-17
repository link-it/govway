package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.RuoloAllegatoAPI;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiAllegato  {
  
  @Schema(required = true, description = "")
  private RuoloAllegatoAPI ruolo = null;
  
  @Schema(required = true, description = "")
  private Object allegato = null;
 /**
   * Get ruolo
   * @return ruolo
  **/
  @JsonProperty("ruolo")
  @NotNull
  public RuoloAllegatoAPI getRuolo() {
    return this.ruolo;
  }

  public void setRuolo(RuoloAllegatoAPI ruolo) {
    this.ruolo = ruolo;
  }

  public ApiAllegato ruolo(RuoloAllegatoAPI ruolo) {
    this.ruolo = ruolo;
    return this;
  }

 /**
   * Get allegato
   * @return allegato
  **/
  @JsonProperty("allegato")
  @NotNull
  public Object getAllegato() {
    return this.allegato;
  }

  public void setAllegato(Object allegato) {
    this.allegato = allegato;
  }

  public ApiAllegato allegato(Object allegato) {
    this.allegato = allegato;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiAllegato {\n");
    
    sb.append("    ruolo: ").append(ApiAllegato.toIndentedString(this.ruolo)).append("\n");
    sb.append("    allegato: ").append(ApiAllegato.toIndentedString(this.allegato)).append("\n");
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

package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.RuoloAllegatoAPIImpl;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ApiImplAllegato  {
  
  @Schema(required = true, description = "")
  private RuoloAllegatoAPIImpl ruolo = null;
  
  @Schema(required = true, description = "")
  private Object allegato = null;
 /**
   * Get ruolo
   * @return ruolo
  **/
  @JsonProperty("ruolo")
  @NotNull
  @Valid
  public RuoloAllegatoAPIImpl getRuolo() {
    return this.ruolo;
  }

  public void setRuolo(RuoloAllegatoAPIImpl ruolo) {
    this.ruolo = ruolo;
  }

  public ApiImplAllegato ruolo(RuoloAllegatoAPIImpl ruolo) {
    this.ruolo = ruolo;
    return this;
  }

 /**
   * Get allegato
   * @return allegato
  **/
  @JsonProperty("allegato")
  @NotNull
  @Valid
  public Object getAllegato() {
    return this.allegato;
  }

  public void setAllegato(Object allegato) {
    this.allegato = allegato;
  }

  public ApiImplAllegato allegato(Object allegato) {
    this.allegato = allegato;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiImplAllegato {\n");
    
    sb.append("    ruolo: ").append(ApiImplAllegato.toIndentedString(this.ruolo)).append("\n");
    sb.append("    allegato: ").append(ApiImplAllegato.toIndentedString(this.allegato)).append("\n");
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

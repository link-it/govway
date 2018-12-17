package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazioneBaseConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class APIImplAutorizzazioneConfigNew extends APIImplAutorizzazioneBaseConfig {
  
  @Schema(description = "")
  private String soggetto = null;
  
  @Schema(description = "")
  private String ruolo = null;
 /**
   * Get soggetto
   * @return soggetto
  **/
  @JsonProperty("soggetto")
  public String getSoggetto() {
    return this.soggetto;
  }

  public void setSoggetto(String soggetto) {
    this.soggetto = soggetto;
  }

  public APIImplAutorizzazioneConfigNew soggetto(String soggetto) {
    this.soggetto = soggetto;
    return this;
  }

 /**
   * Get ruolo
   * @return ruolo
  **/
  @JsonProperty("ruolo")
  public String getRuolo() {
    return this.ruolo;
  }

  public void setRuolo(String ruolo) {
    this.ruolo = ruolo;
  }

  public APIImplAutorizzazioneConfigNew ruolo(String ruolo) {
    this.ruolo = ruolo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImplAutorizzazioneConfigNew {\n");
    sb.append("    ").append(APIImplAutorizzazioneConfigNew.toIndentedString(super.toString())).append("\n");
    sb.append("    soggetto: ").append(APIImplAutorizzazioneConfigNew.toIndentedString(this.soggetto)).append("\n");
    sb.append("    ruolo: ").append(APIImplAutorizzazioneConfigNew.toIndentedString(this.ruolo)).append("\n");
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

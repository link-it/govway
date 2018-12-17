package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ControlloAccessiAutorizzazioneSoggetti  {
  
  @Schema(example = "[\"Ente1\",\"Ente2\",\"Ente3\"]", required = true, description = "")
  private List<String> soggetti = new ArrayList<String>();
 /**
   * Get soggetti
   * @return soggetti
  **/
  @JsonProperty("soggetti")
  @NotNull
  public List<String> getSoggetti() {
    return this.soggetti;
  }

  public void setSoggetti(List<String> soggetti) {
    this.soggetti = soggetti;
  }

  public ControlloAccessiAutorizzazioneSoggetti soggetti(List<String> soggetti) {
    this.soggetti = soggetti;
    return this;
  }

  public ControlloAccessiAutorizzazioneSoggetti addSoggettiItem(String soggettiItem) {
    this.soggetti.add(soggettiItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ControlloAccessiAutorizzazioneSoggetti {\n");
    
    sb.append("    soggetti: ").append(ControlloAccessiAutorizzazioneSoggetti.toIndentedString(this.soggetti)).append("\n");
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

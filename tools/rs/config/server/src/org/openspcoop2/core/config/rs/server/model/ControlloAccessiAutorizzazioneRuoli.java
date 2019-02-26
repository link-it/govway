package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ControlloAccessiAutorizzazioneRuoli  {
  
  @Schema(example = "[\"Ruolo1\",\"Ruolo2\",\"Ruolo3\"]", required = true, description = "")
  private List<String> ruoli = new ArrayList<String>();
 /**
   * Get ruoli
   * @return ruoli
  **/
  @JsonProperty("ruoli")
  @NotNull
  @Valid
  public List<String> getRuoli() {
    return this.ruoli;
  }

  public void setRuoli(List<String> ruoli) {
    this.ruoli = ruoli;
  }

  public ControlloAccessiAutorizzazioneRuoli ruoli(List<String> ruoli) {
    this.ruoli = ruoli;
    return this;
  }

  public ControlloAccessiAutorizzazioneRuoli addRuoliItem(String ruoliItem) {
    this.ruoli.add(ruoliItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ControlloAccessiAutorizzazioneRuoli {\n");
    
    sb.append("    ruoli: ").append(ControlloAccessiAutorizzazioneRuoli.toIndentedString(this.ruoli)).append("\n");
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

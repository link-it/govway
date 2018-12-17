package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ControlloAccessiAutorizzazioneApplicativi  {
  
  @Schema(example = "[\"Applicativo1\",\"Applicativo2\",\"Applicativo3\"]", required = true, description = "")
  private List<String> applicativi = new ArrayList<String>();
 /**
   * Get applicativi
   * @return applicativi
  **/
  @JsonProperty("applicativi")
  @NotNull
  public List<String> getApplicativi() {
    return this.applicativi;
  }

  public void setApplicativi(List<String> applicativi) {
    this.applicativi = applicativi;
  }

  public ControlloAccessiAutorizzazioneApplicativi applicativi(List<String> applicativi) {
    this.applicativi = applicativi;
    return this;
  }

  public ControlloAccessiAutorizzazioneApplicativi addApplicativiItem(String applicativiItem) {
    this.applicativi.add(applicativiItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ControlloAccessiAutorizzazioneApplicativi {\n");
    
    sb.append("    applicativi: ").append(ControlloAccessiAutorizzazioneApplicativi.toIndentedString(this.applicativi)).append("\n");
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

package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportGraficoDatoY  {
  
  @Schema(example = "0", description = "")
  private String datoYSingolo = null;
  
  @Schema(example = "Ok, set/17, 0", description = "")
  private String datoYTooltip = null;
 /**
   * Get datoYSingolo
   * @return datoYSingolo
  **/
  @JsonProperty("datoY_singolo")
  public String getDatoYSingolo() {
    return this.datoYSingolo;
  }

  public void setDatoYSingolo(String datoYSingolo) {
    this.datoYSingolo = datoYSingolo;
  }

  public ReportGraficoDatoY datoYSingolo(String datoYSingolo) {
    this.datoYSingolo = datoYSingolo;
    return this;
  }

 /**
   * Get datoYTooltip
   * @return datoYTooltip
  **/
  @JsonProperty("datoY_tooltip")
  public String getDatoYTooltip() {
    return this.datoYTooltip;
  }

  public void setDatoYTooltip(String datoYTooltip) {
    this.datoYTooltip = datoYTooltip;
  }

  public ReportGraficoDatoY datoYTooltip(String datoYTooltip) {
    this.datoYTooltip = datoYTooltip;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportGraficoDatoY {\n");
    
    sb.append("    datoYSingolo: ").append(ReportGraficoDatoY.toIndentedString(this.datoYSingolo)).append("\n");
    sb.append("    datoYTooltip: ").append(ReportGraficoDatoY.toIndentedString(this.datoYTooltip)).append("\n");
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

package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ReportGraficoCategorie  {
  
  @Schema(example = "errore", description = "")
  private String key = null;
  
  @Schema(example = "Fallite", description = "")
  private String label = null;
  
  @Schema(example = "#CD4A50", description = "")
  private String colore = null;
 /**
   * Get key
   * @return key
  **/
  @JsonProperty("key")
  @Valid
  public String getKey() {
    return this.key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public ReportGraficoCategorie key(String key) {
    this.key = key;
    return this;
  }

 /**
   * Get label
   * @return label
  **/
  @JsonProperty("label")
  @Valid
  public String getLabel() {
    return this.label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public ReportGraficoCategorie label(String label) {
    this.label = label;
    return this;
  }

 /**
   * Get colore
   * @return colore
  **/
  @JsonProperty("colore")
  @Valid
  public String getColore() {
    return this.colore;
  }

  public void setColore(String colore) {
    this.colore = colore;
  }

  public ReportGraficoCategorie colore(String colore) {
    this.colore = colore;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportGraficoCategorie {\n");
    
    sb.append("    key: ").append(ReportGraficoCategorie.toIndentedString(this.key)).append("\n");
    sb.append("    label: ").append(ReportGraficoCategorie.toIndentedString(this.label)).append("\n");
    sb.append("    colore: ").append(ReportGraficoCategorie.toIndentedString(this.colore)).append("\n");
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

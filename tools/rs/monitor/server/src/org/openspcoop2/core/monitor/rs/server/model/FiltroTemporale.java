package org.openspcoop2.core.monitor.rs.server.model;

import org.joda.time.DateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class FiltroTemporale  {
  
  @Schema(description = "")
  private DateTime dataDa = null;
  
  @Schema(description = "")
  private DateTime dataA = null;
 /**
   * Get dataDa
   * @return dataDa
  **/
  @JsonProperty("dataDa")
  @Valid
  public DateTime getDataDa() {
    return this.dataDa;
  }

  public void setDataDa(DateTime dataDa) {
    this.dataDa = dataDa;
  }

  public FiltroTemporale dataDa(DateTime dataDa) {
    this.dataDa = dataDa;
    return this;
  }

 /**
   * Get dataA
   * @return dataA
  **/
  @JsonProperty("dataA")
  @Valid
  public DateTime getDataA() {
    return this.dataA;
  }

  public void setDataA(DateTime dataA) {
    this.dataA = dataA;
  }

  public FiltroTemporale dataA(DateTime dataA) {
    this.dataA = dataA;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroTemporale {\n");
    
    sb.append("    dataDa: ").append(FiltroTemporale.toIndentedString(this.dataDa)).append("\n");
    sb.append("    dataA: ").append(FiltroTemporale.toIndentedString(this.dataA)).append("\n");
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

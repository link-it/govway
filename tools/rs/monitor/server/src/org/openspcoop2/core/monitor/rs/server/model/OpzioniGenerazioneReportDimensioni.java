/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class OpzioniGenerazioneReportDimensioni extends OpzioniGenerazioneReport {
  
  @Schema(description = "")
  private DimensioniReportEnum dimensioni = null;
  
  @Schema(description = "")
  private DimensioniReportCustomEnum customInfo = null;
 /**
   * Get dimensioni
   * @return dimensioni
  **/
  @JsonProperty("dimensioni")
  @Valid
  public DimensioniReportEnum getDimensioni() {
    return this.dimensioni;
  }

  public void setDimensioni(DimensioniReportEnum dimensioni) {
    this.dimensioni = dimensioni;
  }

  public OpzioniGenerazioneReportDimensioni dimensioni(DimensioniReportEnum dimensioni) {
    this.dimensioni = dimensioni;
    return this;
  }

 /**
   * Get customInfo
   * @return customInfo
  **/
  @JsonProperty("custom_info")
  @Valid
  public DimensioniReportCustomEnum getCustomInfo() {
    return this.customInfo;
  }

  public void setCustomInfo(DimensioniReportCustomEnum customInfo) {
    this.customInfo = customInfo;
  }

  public OpzioniGenerazioneReportDimensioni customInfo(DimensioniReportCustomEnum customInfo) {
    this.customInfo = customInfo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OpzioniGenerazioneReportDimensioni {\n");
    sb.append("    ").append(OpzioniGenerazioneReportDimensioni.toIndentedString(super.toString())).append("\n");
    sb.append("    dimensioni: ").append(OpzioniGenerazioneReportDimensioni.toIndentedString(this.dimensioni)).append("\n");
    sb.append("    customInfo: ").append(OpzioniGenerazioneReportDimensioni.toIndentedString(this.customInfo)).append("\n");
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

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

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class TipoInformazioneReportMultiLineOccupazioneBanda  implements OneOfOpzioniGenerazioneReportMultiLineTipoInformazione {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private TipoInformazioneReportEnum tipo = null;
  
  @Schema(description = "")
  private OccupazioneBandaTipi occupazioneBanda = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @Override
@JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoInformazioneReportEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoInformazioneReportEnum tipo) {
    this.tipo = tipo;
  }

  public TipoInformazioneReportMultiLineOccupazioneBanda tipo(TipoInformazioneReportEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get occupazioneBanda
   * @return occupazioneBanda
  **/
  @JsonProperty("occupazione_banda")
  @Valid
  public OccupazioneBandaTipi getOccupazioneBanda() {
    return this.occupazioneBanda;
  }

  public void setOccupazioneBanda(OccupazioneBandaTipi occupazioneBanda) {
    this.occupazioneBanda = occupazioneBanda;
  }

  public TipoInformazioneReportMultiLineOccupazioneBanda occupazioneBanda(OccupazioneBandaTipi occupazioneBanda) {
    this.occupazioneBanda = occupazioneBanda;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TipoInformazioneReportMultiLineOccupazioneBanda {\n");
    
    sb.append("    tipo: ").append(TipoInformazioneReportMultiLineOccupazioneBanda.toIndentedString(this.tipo)).append("\n");
    sb.append("    occupazioneBanda: ").append(TipoInformazioneReportMultiLineOccupazioneBanda.toIndentedString(this.occupazioneBanda)).append("\n");
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

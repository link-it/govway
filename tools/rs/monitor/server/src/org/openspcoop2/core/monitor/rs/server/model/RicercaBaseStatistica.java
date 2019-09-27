/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.core.monitor.rs.server.model.FiltroRicercaRuoloTransazioneEnum;
import org.openspcoop2.core.monitor.rs.server.model.FiltroTemporale;
import org.openspcoop2.core.monitor.rs.server.model.UnitaTempoReportEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RicercaBaseStatistica  {
  
  @Schema(description = "")
  private UnitaTempoReportEnum unitaTempo = null;
  
  @Schema(required = true, description = "")
  private FiltroTemporale intervalloTemporale = null;
  
  @Schema(required = true, description = "")
  private FiltroRicercaRuoloTransazioneEnum tipo = null;
  
  @Schema(description = "")
  private String tag = null;
 /**
   * Get unitaTempo
   * @return unitaTempo
  **/
  @JsonProperty("unita_tempo")
  @Valid
  public UnitaTempoReportEnum getUnitaTempo() {
    return this.unitaTempo;
  }

  public void setUnitaTempo(UnitaTempoReportEnum unitaTempo) {
    this.unitaTempo = unitaTempo;
  }

  public RicercaBaseStatistica unitaTempo(UnitaTempoReportEnum unitaTempo) {
    this.unitaTempo = unitaTempo;
    return this;
  }

 /**
   * Get intervalloTemporale
   * @return intervalloTemporale
  **/
  @JsonProperty("intervallo_temporale")
  @NotNull
  @Valid
  public FiltroTemporale getIntervalloTemporale() {
    return this.intervalloTemporale;
  }

  public void setIntervalloTemporale(FiltroTemporale intervalloTemporale) {
    this.intervalloTemporale = intervalloTemporale;
  }

  public RicercaBaseStatistica intervalloTemporale(FiltroTemporale intervalloTemporale) {
    this.intervalloTemporale = intervalloTemporale;
    return this;
  }

 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
  public FiltroRicercaRuoloTransazioneEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(FiltroRicercaRuoloTransazioneEnum tipo) {
    this.tipo = tipo;
  }

  public RicercaBaseStatistica tipo(FiltroRicercaRuoloTransazioneEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get tag
   * @return tag
  **/
  @JsonProperty("tag")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getTag() {
    return this.tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public RicercaBaseStatistica tag(String tag) {
    this.tag = tag;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RicercaBaseStatistica {\n");
    
    sb.append("    unitaTempo: ").append(RicercaBaseStatistica.toIndentedString(this.unitaTempo)).append("\n");
    sb.append("    intervalloTemporale: ").append(RicercaBaseStatistica.toIndentedString(this.intervalloTemporale)).append("\n");
    sb.append("    tipo: ").append(RicercaBaseStatistica.toIndentedString(this.tipo)).append("\n");
    sb.append("    tag: ").append(RicercaBaseStatistica.toIndentedString(this.tag)).append("\n");
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

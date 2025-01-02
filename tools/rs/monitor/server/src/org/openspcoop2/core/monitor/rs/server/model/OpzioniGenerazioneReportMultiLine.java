/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
import javax.validation.Valid;

public class OpzioniGenerazioneReportMultiLine extends OpzioniGenerazioneReportBase {
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "tipo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = TipoInformazioneReportMultiLineNumeroTransazioni.class, name = "numero_transazioni"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = TipoInformazioneReportMultiLineOccupazioneBanda.class, name = "occupazione_banda"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = TipoInformazioneReportMultiLineTempoMedioRisposta.class, name = "tempo_medio_risposta")  })
  private OneOfOpzioniGenerazioneReportMultiLineTipoInformazione tipoInformazione = null;
 /**
   * Get tipoInformazione
   * @return tipoInformazione
  **/
  @JsonProperty("tipo_informazione")
  @Valid
  public OneOfOpzioniGenerazioneReportMultiLineTipoInformazione getTipoInformazione() {
    return this.tipoInformazione;
  }

  public void setTipoInformazione(OneOfOpzioniGenerazioneReportMultiLineTipoInformazione tipoInformazione) {
    this.tipoInformazione = tipoInformazione;
  }

  public OpzioniGenerazioneReportMultiLine tipoInformazione(OneOfOpzioniGenerazioneReportMultiLineTipoInformazione tipoInformazione) {
    this.tipoInformazione = tipoInformazione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OpzioniGenerazioneReportMultiLine {\n");
    sb.append("    ").append(OpzioniGenerazioneReportMultiLine.toIndentedString(super.toString())).append("\n");
    sb.append("    tipoInformazione: ").append(OpzioniGenerazioneReportMultiLine.toIndentedString(this.tipoInformazione)).append("\n");
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

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

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class TipoInformazioneReportTempoMedioRisposta  implements OneOfOpzioniGenerazioneReportTipoInformazione {
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "")
  private TipoInformazioneReportEnum tipo = null;
  
  @Schema(description = "")
  private TempoMedioRispostaEnum tempoMedioRisposta = null;
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

  public TipoInformazioneReportTempoMedioRisposta tipo(TipoInformazioneReportEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get tempoMedioRisposta
   * @return tempoMedioRisposta
  **/
  @JsonProperty("tempo_medio_risposta")
  @Valid
  public TempoMedioRispostaEnum getTempoMedioRisposta() {
    return this.tempoMedioRisposta;
  }

  public void setTempoMedioRisposta(TempoMedioRispostaEnum tempoMedioRisposta) {
    this.tempoMedioRisposta = tempoMedioRisposta;
  }

  public TipoInformazioneReportTempoMedioRisposta tempoMedioRisposta(TempoMedioRispostaEnum tempoMedioRisposta) {
    this.tempoMedioRisposta = tempoMedioRisposta;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TipoInformazioneReportTempoMedioRisposta {\n");
    
    sb.append("    tipo: ").append(TipoInformazioneReportTempoMedioRisposta.toIndentedString(this.tipo)).append("\n");
    sb.append("    tempoMedioRisposta: ").append(TipoInformazioneReportTempoMedioRisposta.toIndentedString(this.tempoMedioRisposta)).append("\n");
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

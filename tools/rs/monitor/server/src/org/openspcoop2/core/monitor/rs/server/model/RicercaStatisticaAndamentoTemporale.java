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

import org.openspcoop2.core.monitor.rs.server.model.FiltroEsito;
import org.openspcoop2.core.monitor.rs.server.model.OpzioniGenerazioneReportMultiLine;
import org.openspcoop2.core.monitor.rs.server.model.RicercaBaseStatisticaSoggetti;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RicercaStatisticaAndamentoTemporale extends RicercaBaseStatisticaSoggetti {
  
  @Schema(required = true, description = "")
  private OpzioniGenerazioneReportMultiLine report = null;
  
  @Schema(description = "")
  private String azione = null;
  
  @Schema(description = "")
  private Object mittente = null;
  
  @Schema(description = "")
  private FiltroEsito esito = null;
 /**
   * Get report
   * @return report
  **/
  @JsonProperty("report")
  @NotNull
  @Valid
  public OpzioniGenerazioneReportMultiLine getReport() {
    return this.report;
  }

  public void setReport(OpzioniGenerazioneReportMultiLine report) {
    this.report = report;
  }

  public RicercaStatisticaAndamentoTemporale report(OpzioniGenerazioneReportMultiLine report) {
    this.report = report;
    return this;
  }

 /**
   * Get azione
   * @return azione
  **/
  @JsonProperty("azione")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getAzione() {
    return this.azione;
  }

  public void setAzione(String azione) {
    this.azione = azione;
  }

  public RicercaStatisticaAndamentoTemporale azione(String azione) {
    this.azione = azione;
    return this;
  }

 /**
   * Get mittente
   * @return mittente
  **/
  @JsonProperty("mittente")
  @Valid
  public Object getMittente() {
    return this.mittente;
  }

  public void setMittente(Object mittente) {
    this.mittente = mittente;
  }

  public RicercaStatisticaAndamentoTemporale mittente(Object mittente) {
    this.mittente = mittente;
    return this;
  }

 /**
   * Get esito
   * @return esito
  **/
  @JsonProperty("esito")
  @Valid
  public FiltroEsito getEsito() {
    return this.esito;
  }

  public void setEsito(FiltroEsito esito) {
    this.esito = esito;
  }

  public RicercaStatisticaAndamentoTemporale esito(FiltroEsito esito) {
    this.esito = esito;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RicercaStatisticaAndamentoTemporale {\n");
    sb.append("    ").append(RicercaStatisticaAndamentoTemporale.toIndentedString(super.toString())).append("\n");
    sb.append("    report: ").append(RicercaStatisticaAndamentoTemporale.toIndentedString(this.report)).append("\n");
    sb.append("    azione: ").append(RicercaStatisticaAndamentoTemporale.toIndentedString(this.azione)).append("\n");
    sb.append("    mittente: ").append(RicercaStatisticaAndamentoTemporale.toIndentedString(this.mittente)).append("\n");
    sb.append("    esito: ").append(RicercaStatisticaAndamentoTemporale.toIndentedString(this.esito)).append("\n");
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

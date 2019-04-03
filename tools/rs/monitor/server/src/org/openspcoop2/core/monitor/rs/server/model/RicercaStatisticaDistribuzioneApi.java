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
import org.openspcoop2.core.monitor.rs.server.model.OpzioniGenerazioneReport;
import org.openspcoop2.core.monitor.rs.server.model.RicercaBaseStatistica;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RicercaStatisticaDistribuzioneApi extends RicercaBaseStatistica {
  
  @Schema(required = true, description = "")
  private OpzioniGenerazioneReport report = null;
  
  @Schema(description = "")
  private Object soggettoErogatore = null;
  
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
  public OpzioniGenerazioneReport getReport() {
    return this.report;
  }

  public void setReport(OpzioniGenerazioneReport report) {
    this.report = report;
  }

  public RicercaStatisticaDistribuzioneApi report(OpzioniGenerazioneReport report) {
    this.report = report;
    return this;
  }

 /**
   * Get soggettoErogatore
   * @return soggettoErogatore
  **/
  @JsonProperty("soggetto_erogatore")
  @Valid
  public Object getSoggettoErogatore() {
    return this.soggettoErogatore;
  }

  public void setSoggettoErogatore(Object soggettoErogatore) {
    this.soggettoErogatore = soggettoErogatore;
  }

  public RicercaStatisticaDistribuzioneApi soggettoErogatore(Object soggettoErogatore) {
    this.soggettoErogatore = soggettoErogatore;
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

  public RicercaStatisticaDistribuzioneApi mittente(Object mittente) {
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

  public RicercaStatisticaDistribuzioneApi esito(FiltroEsito esito) {
    this.esito = esito;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RicercaStatisticaDistribuzioneApi {\n");
    sb.append("    ").append(RicercaStatisticaDistribuzioneApi.toIndentedString(super.toString())).append("\n");
    sb.append("    report: ").append(RicercaStatisticaDistribuzioneApi.toIndentedString(this.report)).append("\n");
    sb.append("    soggettoErogatore: ").append(RicercaStatisticaDistribuzioneApi.toIndentedString(this.soggettoErogatore)).append("\n");
    sb.append("    mittente: ").append(RicercaStatisticaDistribuzioneApi.toIndentedString(this.mittente)).append("\n");
    sb.append("    esito: ").append(RicercaStatisticaDistribuzioneApi.toIndentedString(this.esito)).append("\n");
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

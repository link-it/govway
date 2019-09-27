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
import org.openspcoop2.core.monitor.rs.server.model.FiltroRicercaRuoloTransazioneEnum;
import org.openspcoop2.core.monitor.rs.server.model.FiltroTemporale;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RicercaBaseTransazione  {
  
  @Schema(description = "")
  private Integer offset = 0;
  
  @Schema(description = "")
  private Integer limit = null;
  
  @Schema(example = "+name", description = "")
  private String sort = null;
  
  @Schema(required = true, description = "")
  private FiltroTemporale intervalloTemporale = null;
  
  @Schema(required = true, description = "")
  private FiltroRicercaRuoloTransazioneEnum tipo = null;
  
  @Schema(description = "Identificativo del nodo su cui e' stata emessa la transazione")
 /**
   * Identificativo del nodo su cui e' stata emessa la transazione  
  **/
  private String idCluster = null;
  
  @Schema(description = "")
  private String tag = null;
  
  @Schema(description = "")
  private Object api = null;
  
  @Schema(description = "")
  private String azione = null;
  
  @Schema(description = "")
  private FiltroEsito esito = null;
  
  @Schema(description = "Evento da ricercare per una transazione")
 /**
   * Evento da ricercare per una transazione  
  **/
  private String evento = null;
 /**
   * Get offset
   * @return offset
  **/
  @JsonProperty("offset")
  @Valid
  public Integer getOffset() {
    return this.offset;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public RicercaBaseTransazione offset(Integer offset) {
    this.offset = offset;
    return this;
  }

 /**
   * Get limit
   * @return limit
  **/
  @JsonProperty("limit")
  @Valid
  public Integer getLimit() {
    return this.limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public RicercaBaseTransazione limit(Integer limit) {
    this.limit = limit;
    return this;
  }

 /**
   * Get sort
   * @return sort
  **/
  @JsonProperty("sort")
  @Valid
  public String getSort() {
    return this.sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public RicercaBaseTransazione sort(String sort) {
    this.sort = sort;
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

  public RicercaBaseTransazione intervalloTemporale(FiltroTemporale intervalloTemporale) {
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

  public RicercaBaseTransazione tipo(FiltroRicercaRuoloTransazioneEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Identificativo del nodo su cui e' stata emessa la transazione
   * @return idCluster
  **/
  @JsonProperty("id_cluster")
  @Valid
  public String getIdCluster() {
    return this.idCluster;
  }

  public void setIdCluster(String idCluster) {
    this.idCluster = idCluster;
  }

  public RicercaBaseTransazione idCluster(String idCluster) {
    this.idCluster = idCluster;
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

  public RicercaBaseTransazione tag(String tag) {
    this.tag = tag;
    return this;
  }

 /**
   * Get api
   * @return api
  **/
  @JsonProperty("api")
  @Valid
  public Object getApi() {
    return this.api;
  }

  public void setApi(Object api) {
    this.api = api;
  }

  public RicercaBaseTransazione api(Object api) {
    this.api = api;
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

  public RicercaBaseTransazione azione(String azione) {
    this.azione = azione;
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

  public RicercaBaseTransazione esito(FiltroEsito esito) {
    this.esito = esito;
    return this;
  }

 /**
   * Evento da ricercare per una transazione
   * @return evento
  **/
  @JsonProperty("evento")
  @Valid
  public String getEvento() {
    return this.evento;
  }

  public void setEvento(String evento) {
    this.evento = evento;
  }

  public RicercaBaseTransazione evento(String evento) {
    this.evento = evento;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RicercaBaseTransazione {\n");
    
    sb.append("    offset: ").append(RicercaBaseTransazione.toIndentedString(this.offset)).append("\n");
    sb.append("    limit: ").append(RicercaBaseTransazione.toIndentedString(this.limit)).append("\n");
    sb.append("    sort: ").append(RicercaBaseTransazione.toIndentedString(this.sort)).append("\n");
    sb.append("    intervalloTemporale: ").append(RicercaBaseTransazione.toIndentedString(this.intervalloTemporale)).append("\n");
    sb.append("    tipo: ").append(RicercaBaseTransazione.toIndentedString(this.tipo)).append("\n");
    sb.append("    idCluster: ").append(RicercaBaseTransazione.toIndentedString(this.idCluster)).append("\n");
    sb.append("    tag: ").append(RicercaBaseTransazione.toIndentedString(this.tag)).append("\n");
    sb.append("    api: ").append(RicercaBaseTransazione.toIndentedString(this.api)).append("\n");
    sb.append("    azione: ").append(RicercaBaseTransazione.toIndentedString(this.azione)).append("\n");
    sb.append("    esito: ").append(RicercaBaseTransazione.toIndentedString(this.esito)).append("\n");
    sb.append("    evento: ").append(RicercaBaseTransazione.toIndentedString(this.evento)).append("\n");
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

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

import java.util.UUID;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ItemTracingPDND  {
  
  @Schema(description = "")
  private LocalDate dataTracciamento = null;
  
  @Schema(description = "")
  private DateTime dataRegistrazione = null;
  
  @Schema(description = "")
  private Integer numeroTentativi = null;
  
  @Schema(description = "")
  private StatoTracing stato = null;
  
  @Schema(description = "")
  private StatoTracingPDND statoPdnd = null;
  
  @Schema(description = "")
  private UUID tracingId = null;
  
  @Schema(description = "")
  private MethodTracingPDND method = null;
 /**
   * Get dataTracciamento
   * @return dataTracciamento
  **/
  @JsonProperty("data_tracciamento")
  @Valid
  public LocalDate getDataTracciamento() {
    return this.dataTracciamento;
  }

  public void setDataTracciamento(LocalDate dataTracciamento) {
    this.dataTracciamento = dataTracciamento;
  }

  public ItemTracingPDND dataTracciamento(LocalDate dataTracciamento) {
    this.dataTracciamento = dataTracciamento;
    return this;
  }

 /**
   * Get dataRegistrazione
   * @return dataRegistrazione
  **/
  @JsonProperty("data_registrazione")
  @Valid
  public DateTime getDataRegistrazione() {
    return this.dataRegistrazione;
  }

  public void setDataRegistrazione(DateTime dataRegistrazione) {
    this.dataRegistrazione = dataRegistrazione;
  }

  public ItemTracingPDND dataRegistrazione(DateTime dataRegistrazione) {
    this.dataRegistrazione = dataRegistrazione;
    return this;
  }

 /**
   * Get numeroTentativi
   * @return numeroTentativi
  **/
  @JsonProperty("numero_tentativi")
  @Valid
  public Integer getNumeroTentativi() {
    return this.numeroTentativi;
  }

  public void setNumeroTentativi(Integer numeroTentativi) {
    this.numeroTentativi = numeroTentativi;
  }

  public ItemTracingPDND numeroTentativi(Integer numeroTentativi) {
    this.numeroTentativi = numeroTentativi;
    return this;
  }

 /**
   * Get stato
   * @return stato
  **/
  @JsonProperty("stato")
  @Valid
  public StatoTracing getStato() {
    return this.stato;
  }

  public void setStato(StatoTracing stato) {
    this.stato = stato;
  }

  public ItemTracingPDND stato(StatoTracing stato) {
    this.stato = stato;
    return this;
  }

 /**
   * Get statoPdnd
   * @return statoPdnd
  **/
  @JsonProperty("stato_pdnd")
  @Valid
  public StatoTracingPDND getStatoPdnd() {
    return this.statoPdnd;
  }

  public void setStatoPdnd(StatoTracingPDND statoPdnd) {
    this.statoPdnd = statoPdnd;
  }

  public ItemTracingPDND statoPdnd(StatoTracingPDND statoPdnd) {
    this.statoPdnd = statoPdnd;
    return this;
  }

 /**
   * Get tracingId
   * @return tracingId
  **/
  @JsonProperty("tracing_id")
  @Valid
  public UUID getTracingId() {
    return this.tracingId;
  }

  public void setTracingId(UUID tracingId) {
    this.tracingId = tracingId;
  }

  public ItemTracingPDND tracingId(UUID tracingId) {
    this.tracingId = tracingId;
    return this;
  }

 /**
   * Get method
   * @return method
  **/
  @JsonProperty("method")
  @Valid
  public MethodTracingPDND getMethod() {
    return this.method;
  }

  public void setMethod(MethodTracingPDND method) {
    this.method = method;
  }

  public ItemTracingPDND method(MethodTracingPDND method) {
    this.method = method;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ItemTracingPDND {\n");
    
    sb.append("    dataTracciamento: ").append(ItemTracingPDND.toIndentedString(this.dataTracciamento)).append("\n");
    sb.append("    dataRegistrazione: ").append(ItemTracingPDND.toIndentedString(this.dataRegistrazione)).append("\n");
    sb.append("    numeroTentativi: ").append(ItemTracingPDND.toIndentedString(this.numeroTentativi)).append("\n");
    sb.append("    stato: ").append(ItemTracingPDND.toIndentedString(this.stato)).append("\n");
    sb.append("    statoPdnd: ").append(ItemTracingPDND.toIndentedString(this.statoPdnd)).append("\n");
    sb.append("    tracingId: ").append(ItemTracingPDND.toIndentedString(this.tracingId)).append("\n");
    sb.append("    method: ").append(ItemTracingPDND.toIndentedString(this.method)).append("\n");
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

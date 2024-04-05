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

import org.joda.time.DateTime;
import org.openspcoop2.utils.service.beans.TransazioneExt;
import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class DetailTransazione extends TransazioneExt {
  
  @Schema(required = true, description = "")
  private DateTime data = null;
  
  @Schema(example = "8", description = "")
  private Long latenzaServizio = null;
  
  @Schema(example = "20", description = "")
  private Long latenzaTotale = null;
  
  @Schema(description = "")
  private PDNDOrganizationInfo pdndOrganization = null;
  
  @Schema(description = "")
  private String richiedente = null;
  
  @Schema(description = "")
  private String dettaglioErrore = null;
 /**
   * Get data
   * @return data
  **/
  @JsonProperty("data")
  @NotNull
  @Valid
  public DateTime getData() {
    return this.data;
  }

  public void setData(DateTime data) {
    this.data = data;
  }

  public DetailTransazione data(DateTime data) {
    this.data = data;
    return this;
  }

 /**
   * Get latenzaServizio
   * @return latenzaServizio
  **/
  @JsonProperty("latenza_servizio")
  @Valid
  public Long getLatenzaServizio() {
    return this.latenzaServizio;
  }

  public void setLatenzaServizio(Long latenzaServizio) {
    this.latenzaServizio = latenzaServizio;
  }

  public DetailTransazione latenzaServizio(Long latenzaServizio) {
    this.latenzaServizio = latenzaServizio;
    return this;
  }

 /**
   * Get latenzaTotale
   * @return latenzaTotale
  **/
  @JsonProperty("latenza_totale")
  @Valid
  public Long getLatenzaTotale() {
    return this.latenzaTotale;
  }

  public void setLatenzaTotale(Long latenzaTotale) {
    this.latenzaTotale = latenzaTotale;
  }

  public DetailTransazione latenzaTotale(Long latenzaTotale) {
    this.latenzaTotale = latenzaTotale;
    return this;
  }

 /**
   * Get pdndOrganization
   * @return pdndOrganization
  **/
  @JsonProperty("pdnd_organization")
  @Valid
  public PDNDOrganizationInfo getPdndOrganization() {
    return this.pdndOrganization;
  }

  public void setPdndOrganization(PDNDOrganizationInfo pdndOrganization) {
    this.pdndOrganization = pdndOrganization;
  }

  public DetailTransazione pdndOrganization(PDNDOrganizationInfo pdndOrganization) {
    this.pdndOrganization = pdndOrganization;
    return this;
  }

 /**
   * Get richiedente
   * @return richiedente
  **/
  @JsonProperty("richiedente")
  @Valid
  public String getRichiedente() {
    return this.richiedente;
  }

  public void setRichiedente(String richiedente) {
    this.richiedente = richiedente;
  }

  public DetailTransazione richiedente(String richiedente) {
    this.richiedente = richiedente;
    return this;
  }

 /**
   * Get dettaglioErrore
   * @return dettaglioErrore
  **/
  @JsonProperty("dettaglio_errore")
  @Valid
  public String getDettaglioErrore() {
    return this.dettaglioErrore;
  }

  public void setDettaglioErrore(String dettaglioErrore) {
    this.dettaglioErrore = dettaglioErrore;
  }

  public DetailTransazione dettaglioErrore(String dettaglioErrore) {
    this.dettaglioErrore = dettaglioErrore;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DetailTransazione {\n");
    sb.append("    ").append(DetailTransazione.toIndentedString(super.toString())).append("\n");
    sb.append("    data: ").append(DetailTransazione.toIndentedString(this.data)).append("\n");
    sb.append("    latenzaServizio: ").append(DetailTransazione.toIndentedString(this.latenzaServizio)).append("\n");
    sb.append("    latenzaTotale: ").append(DetailTransazione.toIndentedString(this.latenzaTotale)).append("\n");
    sb.append("    pdndOrganization: ").append(DetailTransazione.toIndentedString(this.pdndOrganization)).append("\n");
    sb.append("    richiedente: ").append(DetailTransazione.toIndentedString(this.richiedente)).append("\n");
    sb.append("    dettaglioErrore: ").append(DetailTransazione.toIndentedString(this.dettaglioErrore)).append("\n");
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

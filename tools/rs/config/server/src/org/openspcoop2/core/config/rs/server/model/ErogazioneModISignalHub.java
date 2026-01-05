/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ErogazioneModISignalHub  {
  
  @Schema(example = "/pseudonymization", required = true, description = "")
  private String risorsa = null;
  
  @Schema(example = "SHA-256", required = true, description = "")
  private String algoritmo = null;
  
  @Schema(example = "16", required = true, description = "")
  private Integer dimensioneSeme = null;
  
  @Schema(example = "15", required = true, description = "")
  private Integer giorniRotazione = null;
  
  @Schema(description = "")
  private String applicativo = null;
  
  @Schema(description = "")
  private String ruolo = null;
 /**
   * Get risorsa
   * @return risorsa
  **/
  @JsonProperty("risorsa")
  @NotNull
  @Valid
  public String getRisorsa() {
    return this.risorsa;
  }

  public void setRisorsa(String risorsa) {
    this.risorsa = risorsa;
  }

  public ErogazioneModISignalHub risorsa(String risorsa) {
    this.risorsa = risorsa;
    return this;
  }

 /**
   * Get algoritmo
   * @return algoritmo
  **/
  @JsonProperty("algoritmo")
  @NotNull
  @Valid
  public String getAlgoritmo() {
    return this.algoritmo;
  }

  public void setAlgoritmo(String algoritmo) {
    this.algoritmo = algoritmo;
  }

  public ErogazioneModISignalHub algoritmo(String algoritmo) {
    this.algoritmo = algoritmo;
    return this;
  }

 /**
   * Get dimensioneSeme
   * @return dimensioneSeme
  **/
  @JsonProperty("dimensione_seme")
  @NotNull
  @Valid
  public Integer getDimensioneSeme() {
    return this.dimensioneSeme;
  }

  public void setDimensioneSeme(Integer dimensioneSeme) {
    this.dimensioneSeme = dimensioneSeme;
  }

  public ErogazioneModISignalHub dimensioneSeme(Integer dimensioneSeme) {
    this.dimensioneSeme = dimensioneSeme;
    return this;
  }

 /**
   * Get giorniRotazione
   * @return giorniRotazione
  **/
  @JsonProperty("giorni_rotazione")
  @NotNull
  @Valid
  public Integer getGiorniRotazione() {
    return this.giorniRotazione;
  }

  public void setGiorniRotazione(Integer giorniRotazione) {
    this.giorniRotazione = giorniRotazione;
  }

  public ErogazioneModISignalHub giorniRotazione(Integer giorniRotazione) {
    this.giorniRotazione = giorniRotazione;
    return this;
  }

 /**
   * Get applicativo
   * @return applicativo
  **/
  @JsonProperty("applicativo")
  @Valid
  public String getApplicativo() {
    return this.applicativo;
  }

  public void setApplicativo(String applicativo) {
    this.applicativo = applicativo;
  }

  public ErogazioneModISignalHub applicativo(String applicativo) {
    this.applicativo = applicativo;
    return this;
  }

 /**
   * Get ruolo
   * @return ruolo
  **/
  @JsonProperty("ruolo")
  @Valid
  public String getRuolo() {
    return this.ruolo;
  }

  public void setRuolo(String ruolo) {
    this.ruolo = ruolo;
  }

  public ErogazioneModISignalHub ruolo(String ruolo) {
    this.ruolo = ruolo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErogazioneModISignalHub {\n");
    
    sb.append("    risorsa: ").append(ErogazioneModISignalHub.toIndentedString(this.risorsa)).append("\n");
    sb.append("    algoritmo: ").append(ErogazioneModISignalHub.toIndentedString(this.algoritmo)).append("\n");
    sb.append("    dimensioneSeme: ").append(ErogazioneModISignalHub.toIndentedString(this.dimensioneSeme)).append("\n");
    sb.append("    giorniRotazione: ").append(ErogazioneModISignalHub.toIndentedString(this.giorniRotazione)).append("\n");
    sb.append("    applicativo: ").append(ErogazioneModISignalHub.toIndentedString(this.applicativo)).append("\n");
    sb.append("    ruolo: ").append(ErogazioneModISignalHub.toIndentedString(this.ruolo)).append("\n");
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

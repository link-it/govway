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
package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;

public class ErogazioneModISignalHub  {
  
  @Schema(example = "/pseudonymization", description = "")
  private String risorsa = null;
  
  @Schema(example = "SHA-256", description = "")
  private String algoritmo = null;
  
  @Schema(example = "16", description = "")
  private Integer dimensioneSeme = null;
  
  @Schema(example = "15", description = "")
  private Integer giorniRotazione = null;
  
  @Schema(description = "")
  private String applicativo = null;
  
  @Schema(description = "")
  private String ruolo = null;
  
  @Schema(description = "")
  private Boolean pseudoanonimizzazione = true;
 /**
   * Get risorsa
   * @return risorsa
  **/
  @JsonProperty("risorsa")
  @Valid
  public String getRisorsa() {
    return risorsa;
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
  @Valid
  public String getAlgoritmo() {
    return algoritmo;
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
  @Valid
  public Integer getDimensioneSeme() {
    return dimensioneSeme;
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
  @Valid
  public Integer getGiorniRotazione() {
    return giorniRotazione;
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
    return applicativo;
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
    return ruolo;
  }

  public void setRuolo(String ruolo) {
    this.ruolo = ruolo;
  }

  public ErogazioneModISignalHub ruolo(String ruolo) {
    this.ruolo = ruolo;
    return this;
  }

 /**
   * Get pseudoanonimizzazione
   * @return pseudoanonimizzazione
  **/
  @JsonProperty("pseudoanonimizzazione")
  @Valid
  public Boolean isPseudoanonimizzazione() {
    return pseudoanonimizzazione;
  }

  public void setPseudoanonimizzazione(Boolean pseudoanonimizzazione) {
    this.pseudoanonimizzazione = pseudoanonimizzazione;
  }

  public ErogazioneModISignalHub pseudoanonimizzazione(Boolean pseudoanonimizzazione) {
    this.pseudoanonimizzazione = pseudoanonimizzazione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErogazioneModISignalHub {\n");
    
    sb.append("    risorsa: ").append(toIndentedString(risorsa)).append("\n");
    sb.append("    algoritmo: ").append(toIndentedString(algoritmo)).append("\n");
    sb.append("    dimensioneSeme: ").append(toIndentedString(dimensioneSeme)).append("\n");
    sb.append("    giorniRotazione: ").append(toIndentedString(giorniRotazione)).append("\n");
    sb.append("    applicativo: ").append(toIndentedString(applicativo)).append("\n");
    sb.append("    ruolo: ").append(toIndentedString(ruolo)).append("\n");
    sb.append("    pseudoanonimizzazione: ").append(toIndentedString(pseudoanonimizzazione)).append("\n");
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

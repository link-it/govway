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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class Riepilogo  {
  
  @Schema(example = "2", required = true, description = "Il numero di api per cui il soggetto è referente")
 /**
   * Il numero di api per cui il soggetto è referente  
  **/
  private Integer api = null;
  
  @Schema(description = "")
  private String soggetto = null;
  
  @Schema(example = "2", required = true, description = "Il numero di erogazioni del soggetto")
 /**
   * Il numero di erogazioni del soggetto  
  **/
  private Integer erogazioni = null;
  
  @Schema(example = "2", required = true, description = "Il numero di fruizioni del soggetto")
 /**
   * Il numero di fruizioni del soggetto  
  **/
  private Integer fruizioni = null;
  
  @Schema(required = true, description = "")
  private Integer soggettiDominioInterno = null;
  
  @Schema(required = true, description = "")
  private Integer soggettiDominioEsterno = null;
  
  @Schema(required = true, description = "")
  private Integer applicativi = null;
 /**
   * Il numero di api per cui il soggetto è referente
   * @return api
  **/
  @JsonProperty("api")
  @NotNull
  @Valid
  public Integer getApi() {
    return this.api;
  }

  public void setApi(Integer api) {
    this.api = api;
  }

  public Riepilogo api(Integer api) {
    this.api = api;
    return this;
  }

 /**
   * Get soggetto
   * @return soggetto
  **/
  @JsonProperty("soggetto")
  @Valid
 @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255)  public String getSoggetto() {
    return this.soggetto;
  }

  public void setSoggetto(String soggetto) {
    this.soggetto = soggetto;
  }

  public Riepilogo soggetto(String soggetto) {
    this.soggetto = soggetto;
    return this;
  }

 /**
   * Il numero di erogazioni del soggetto
   * @return erogazioni
  **/
  @JsonProperty("erogazioni")
  @NotNull
  @Valid
  public Integer getErogazioni() {
    return this.erogazioni;
  }

  public void setErogazioni(Integer erogazioni) {
    this.erogazioni = erogazioni;
  }

  public Riepilogo erogazioni(Integer erogazioni) {
    this.erogazioni = erogazioni;
    return this;
  }

 /**
   * Il numero di fruizioni del soggetto
   * @return fruizioni
  **/
  @JsonProperty("fruizioni")
  @NotNull
  @Valid
  public Integer getFruizioni() {
    return this.fruizioni;
  }

  public void setFruizioni(Integer fruizioni) {
    this.fruizioni = fruizioni;
  }

  public Riepilogo fruizioni(Integer fruizioni) {
    this.fruizioni = fruizioni;
    return this;
  }

 /**
   * Get soggettiDominioInterno
   * @return soggettiDominioInterno
  **/
  @JsonProperty("soggetti_dominio_interno")
  @NotNull
  @Valid
  public Integer getSoggettiDominioInterno() {
    return this.soggettiDominioInterno;
  }

  public void setSoggettiDominioInterno(Integer soggettiDominioInterno) {
    this.soggettiDominioInterno = soggettiDominioInterno;
  }

  public Riepilogo soggettiDominioInterno(Integer soggettiDominioInterno) {
    this.soggettiDominioInterno = soggettiDominioInterno;
    return this;
  }

 /**
   * Get soggettiDominioEsterno
   * @return soggettiDominioEsterno
  **/
  @JsonProperty("soggetti_dominio_esterno")
  @NotNull
  @Valid
  public Integer getSoggettiDominioEsterno() {
    return this.soggettiDominioEsterno;
  }

  public void setSoggettiDominioEsterno(Integer soggettiDominioEsterno) {
    this.soggettiDominioEsterno = soggettiDominioEsterno;
  }

  public Riepilogo soggettiDominioEsterno(Integer soggettiDominioEsterno) {
    this.soggettiDominioEsterno = soggettiDominioEsterno;
    return this;
  }

 /**
   * Get applicativi
   * @return applicativi
  **/
  @JsonProperty("applicativi")
  @NotNull
  @Valid
  public Integer getApplicativi() {
    return this.applicativi;
  }

  public void setApplicativi(Integer applicativi) {
    this.applicativi = applicativi;
  }

  public Riepilogo applicativi(Integer applicativi) {
    this.applicativi = applicativi;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Riepilogo {\n");
    
    sb.append("    api: ").append(Riepilogo.toIndentedString(this.api)).append("\n");
    sb.append("    soggetto: ").append(Riepilogo.toIndentedString(this.soggetto)).append("\n");
    sb.append("    erogazioni: ").append(Riepilogo.toIndentedString(this.erogazioni)).append("\n");
    sb.append("    fruizioni: ").append(Riepilogo.toIndentedString(this.fruizioni)).append("\n");
    sb.append("    soggettiDominioInterno: ").append(Riepilogo.toIndentedString(this.soggettiDominioInterno)).append("\n");
    sb.append("    soggettiDominioEsterno: ").append(Riepilogo.toIndentedString(this.soggettiDominioEsterno)).append("\n");
    sb.append("    applicativi: ").append(Riepilogo.toIndentedString(this.applicativi)).append("\n");
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

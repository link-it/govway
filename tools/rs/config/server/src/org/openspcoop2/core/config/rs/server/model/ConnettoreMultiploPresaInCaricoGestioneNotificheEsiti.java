/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import java.util.List;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti  {
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploGestioneNotificheEsitoConsegnaEnum esito = null;
  
  @Schema(description = "")
  private Integer codiceMin = null;
  
  @Schema(description = "")
  private Integer codiceMax = null;
  
  @Schema(description = "")
  private List<Integer> codici = null;
 /**
   * Get esito
   * @return esito
  **/
  @JsonProperty("esito")
  @NotNull
  @Valid
  public ConnettoreMultiploGestioneNotificheEsitoConsegnaEnum getEsito() {
    return this.esito;
  }

  public void setEsito(ConnettoreMultiploGestioneNotificheEsitoConsegnaEnum esito) {
    this.esito = esito;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti esito(ConnettoreMultiploGestioneNotificheEsitoConsegnaEnum esito) {
    this.esito = esito;
    return this;
  }

 /**
   * Get codiceMin
   * @return codiceMin
  **/
  @JsonProperty("codice_min")
  @Valid
  public Integer getCodiceMin() {
    return this.codiceMin;
  }

  public void setCodiceMin(Integer codiceMin) {
    this.codiceMin = codiceMin;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti codiceMin(Integer codiceMin) {
    this.codiceMin = codiceMin;
    return this;
  }

 /**
   * Get codiceMax
   * @return codiceMax
  **/
  @JsonProperty("codice_max")
  @Valid
  public Integer getCodiceMax() {
    return this.codiceMax;
  }

  public void setCodiceMax(Integer codiceMax) {
    this.codiceMax = codiceMax;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti codiceMax(Integer codiceMax) {
    this.codiceMax = codiceMax;
    return this;
  }

 /**
   * Get codici
   * @return codici
  **/
  @JsonProperty("codici")
  @Valid
  public List<Integer> getCodici() {
    return this.codici;
  }

  public void setCodici(List<Integer> codici) {
    this.codici = codici;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti codici(List<Integer> codici) {
    this.codici = codici;
    return this;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti addCodiciItem(Integer codiciItem) {
    this.codici.add(codiciItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti {\n");
    
    sb.append("    esito: ").append(ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti.toIndentedString(this.esito)).append("\n");
    sb.append("    codiceMin: ").append(ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti.toIndentedString(this.codiceMin)).append("\n");
    sb.append("    codiceMax: ").append(ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti.toIndentedString(this.codiceMax)).append("\n");
    sb.append("    codici: ").append(ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti.toIndentedString(this.codici)).append("\n");
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

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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ConnettoreMultiploPresaInCaricoGestioneNotifiche  {
  
  @Schema(description = "")
  private ConnettoreMultiploGestioneNotifichePrioritaEnum priorita = null;
  
  @Schema(description = "")
  private Boolean consegnaImmediata = false;
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti _2xx = null;
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti _3xx = null;
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti _4xx = null;
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti _5xx = null;
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploPresaInCaricoGestioneNotificheFault fault = null;
  
  @Schema(description = "minuti dopo i quali verrà riprovata una consegna precedentemente fallita")
 /**
   * minuti dopo i quali verrà riprovata una consegna precedentemente fallita  
  **/
  private Integer cadenzaRispedizione = null;
 /**
   * Get priorita
   * @return priorita
  **/
  @JsonProperty("priorita")
  @Valid
  public ConnettoreMultiploGestioneNotifichePrioritaEnum getPriorita() {
    return this.priorita;
  }

  public void setPriorita(ConnettoreMultiploGestioneNotifichePrioritaEnum priorita) {
    this.priorita = priorita;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotifiche priorita(ConnettoreMultiploGestioneNotifichePrioritaEnum priorita) {
    this.priorita = priorita;
    return this;
  }

 /**
   * Get consegnaImmediata
   * @return consegnaImmediata
  **/
  @JsonProperty("consegna_immediata")
  @Valid
  public Boolean isConsegnaImmediata() {
    return this.consegnaImmediata;
  }

  public void setConsegnaImmediata(Boolean consegnaImmediata) {
    this.consegnaImmediata = consegnaImmediata;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotifiche consegnaImmediata(Boolean consegnaImmediata) {
    this.consegnaImmediata = consegnaImmediata;
    return this;
  }

 /**
   * Get _2xx
   * @return _2xx
  **/
  @JsonProperty("2xx")
  @NotNull
  @Valid
  public ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti get2xx() {
    return this._2xx;
  }

  public void set2xx(ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti _2xx) {
    this._2xx = _2xx;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotifiche _2xx(ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti _2xx) {
    this._2xx = _2xx;
    return this;
  }

 /**
   * Get _3xx
   * @return _3xx
  **/
  @JsonProperty("3xx")
  @NotNull
  @Valid
  public ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti get3xx() {
    return this._3xx;
  }

  public void set3xx(ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti _3xx) {
    this._3xx = _3xx;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotifiche _3xx(ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti _3xx) {
    this._3xx = _3xx;
    return this;
  }

 /**
   * Get _4xx
   * @return _4xx
  **/
  @JsonProperty("4xx")
  @NotNull
  @Valid
  public ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti get4xx() {
    return this._4xx;
  }

  public void set4xx(ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti _4xx) {
    this._4xx = _4xx;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotifiche _4xx(ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti _4xx) {
    this._4xx = _4xx;
    return this;
  }

 /**
   * Get _5xx
   * @return _5xx
  **/
  @JsonProperty("5xx")
  @NotNull
  @Valid
  public ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti get5xx() {
    return this._5xx;
  }

  public void set5xx(ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti _5xx) {
    this._5xx = _5xx;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotifiche _5xx(ConnettoreMultiploPresaInCaricoGestioneNotificheEsiti _5xx) {
    this._5xx = _5xx;
    return this;
  }

 /**
   * Get fault
   * @return fault
  **/
  @JsonProperty("fault")
  @NotNull
  @Valid
  public ConnettoreMultiploPresaInCaricoGestioneNotificheFault getFault() {
    return this.fault;
  }

  public void setFault(ConnettoreMultiploPresaInCaricoGestioneNotificheFault fault) {
    this.fault = fault;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotifiche fault(ConnettoreMultiploPresaInCaricoGestioneNotificheFault fault) {
    this.fault = fault;
    return this;
  }

 /**
   * minuti dopo i quali verrà riprovata una consegna precedentemente fallita
   * @return cadenzaRispedizione
  **/
  @JsonProperty("cadenza_rispedizione")
  @Valid
  public Integer getCadenzaRispedizione() {
    return this.cadenzaRispedizione;
  }

  public void setCadenzaRispedizione(Integer cadenzaRispedizione) {
    this.cadenzaRispedizione = cadenzaRispedizione;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotifiche cadenzaRispedizione(Integer cadenzaRispedizione) {
    this.cadenzaRispedizione = cadenzaRispedizione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreMultiploPresaInCaricoGestioneNotifiche {\n");
    
    sb.append("    priorita: ").append(ConnettoreMultiploPresaInCaricoGestioneNotifiche.toIndentedString(this.priorita)).append("\n");
    sb.append("    consegnaImmediata: ").append(ConnettoreMultiploPresaInCaricoGestioneNotifiche.toIndentedString(this.consegnaImmediata)).append("\n");
    sb.append("    _2xx: ").append(ConnettoreMultiploPresaInCaricoGestioneNotifiche.toIndentedString(this._2xx)).append("\n");
    sb.append("    _3xx: ").append(ConnettoreMultiploPresaInCaricoGestioneNotifiche.toIndentedString(this._3xx)).append("\n");
    sb.append("    _4xx: ").append(ConnettoreMultiploPresaInCaricoGestioneNotifiche.toIndentedString(this._4xx)).append("\n");
    sb.append("    _5xx: ").append(ConnettoreMultiploPresaInCaricoGestioneNotifiche.toIndentedString(this._5xx)).append("\n");
    sb.append("    fault: ").append(ConnettoreMultiploPresaInCaricoGestioneNotifiche.toIndentedString(this.fault)).append("\n");
    sb.append("    cadenzaRispedizione: ").append(ConnettoreMultiploPresaInCaricoGestioneNotifiche.toIndentedString(this.cadenzaRispedizione)).append("\n");
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

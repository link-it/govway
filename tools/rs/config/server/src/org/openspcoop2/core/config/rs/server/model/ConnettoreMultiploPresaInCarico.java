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

public class ConnettoreMultiploPresaInCarico extends BaseConnettoreMultiplo implements OneOfConnettoreMultiploDatiConnettore {
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploTipologiaEnum tipologia = null;
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploPresaInCaricoGestioneNotifiche gestioneNotifiche = null;
 /**
   * Get tipologia
   * @return tipologia
  **/
  @Override
@JsonProperty("tipologia")
  @NotNull
  @Valid
  public ConnettoreMultiploTipologiaEnum getTipologia() {
    return this.tipologia;
  }

  public void setTipologia(ConnettoreMultiploTipologiaEnum tipologia) {
    this.tipologia = tipologia;
  }

  public ConnettoreMultiploPresaInCarico tipologia(ConnettoreMultiploTipologiaEnum tipologia) {
    this.tipologia = tipologia;
    return this;
  }

 /**
   * Get gestioneNotifiche
   * @return gestioneNotifiche
  **/
  @JsonProperty("gestione_notifiche")
  @NotNull
  @Valid
  public ConnettoreMultiploPresaInCaricoGestioneNotifiche getGestioneNotifiche() {
    return this.gestioneNotifiche;
  }

  public void setGestioneNotifiche(ConnettoreMultiploPresaInCaricoGestioneNotifiche gestioneNotifiche) {
    this.gestioneNotifiche = gestioneNotifiche;
  }

  public ConnettoreMultiploPresaInCarico gestioneNotifiche(ConnettoreMultiploPresaInCaricoGestioneNotifiche gestioneNotifiche) {
    this.gestioneNotifiche = gestioneNotifiche;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreMultiploPresaInCarico {\n");
    sb.append("    ").append(ConnettoreMultiploPresaInCarico.toIndentedString(super.toString())).append("\n");
    sb.append("    tipologia: ").append(ConnettoreMultiploPresaInCarico.toIndentedString(this.tipologia)).append("\n");
    sb.append("    gestioneNotifiche: ").append(ConnettoreMultiploPresaInCarico.toIndentedString(this.gestioneNotifiche)).append("\n");
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

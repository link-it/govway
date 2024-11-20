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
package org.openspcoop2.core.config.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

/**
  * parametri di un periodo statistico per il connettore status
 **/
@Schema(description="parametri di un periodo statistico per il connettore status")
public class ConnettoreStatusVerificaStatistica  {
  
  @Schema(required = true, description = "")
  private TipoPeriodoStatisticoEnum tipoPeriodo = null;
  
  @Schema(required = true, description = "")
  private Integer lunghezzaPeriodo = null;
  
  @Schema(description = "tempo di permanenza in cache di un risultato, se non impostato viene usato il tempo di default della cache del controllo traffico")
 /**
   * tempo di permanenza in cache di un risultato, se non impostato viene usato il tempo di default della cache del controllo traffico  
  **/
  private Integer cacheLifetime = null;
 /**
   * Get tipoPeriodo
   * @return tipoPeriodo
  **/
  @JsonProperty("tipoPeriodo")
  @NotNull
  @Valid
  public TipoPeriodoStatisticoEnum getTipoPeriodo() {
    return this.tipoPeriodo;
  }

  public void setTipoPeriodo(TipoPeriodoStatisticoEnum tipoPeriodo) {
    this.tipoPeriodo = tipoPeriodo;
  }

  public ConnettoreStatusVerificaStatistica tipoPeriodo(TipoPeriodoStatisticoEnum tipoPeriodo) {
    this.tipoPeriodo = tipoPeriodo;
    return this;
  }

 /**
   * Get lunghezzaPeriodo
   * @return lunghezzaPeriodo
  **/
  @JsonProperty("lunghezzaPeriodo")
  @NotNull
  @Valid
  public Integer getLunghezzaPeriodo() {
    return this.lunghezzaPeriodo;
  }

  public void setLunghezzaPeriodo(Integer lunghezzaPeriodo) {
    this.lunghezzaPeriodo = lunghezzaPeriodo;
  }

  public ConnettoreStatusVerificaStatistica lunghezzaPeriodo(Integer lunghezzaPeriodo) {
    this.lunghezzaPeriodo = lunghezzaPeriodo;
    return this;
  }

 /**
   * tempo di permanenza in cache di un risultato, se non impostato viene usato il tempo di default della cache del controllo traffico
   * @return cacheLifetime
  **/
  @JsonProperty("cacheLifetime")
  @Valid
  public Integer getCacheLifetime() {
    return this.cacheLifetime;
  }

  public void setCacheLifetime(Integer cacheLifetime) {
    this.cacheLifetime = cacheLifetime;
  }

  public ConnettoreStatusVerificaStatistica cacheLifetime(Integer cacheLifetime) {
    this.cacheLifetime = cacheLifetime;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreStatusVerificaStatistica {\n");
    
    sb.append("    tipoPeriodo: ").append(ConnettoreStatusVerificaStatistica.toIndentedString(this.tipoPeriodo)).append("\n");
    sb.append("    lunghezzaPeriodo: ").append(ConnettoreStatusVerificaStatistica.toIndentedString(this.lunghezzaPeriodo)).append("\n");
    sb.append("    cacheLifetime: ").append(ConnettoreStatusVerificaStatistica.toIndentedString(this.cacheLifetime)).append("\n");
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

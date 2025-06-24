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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

/**
  * parametri di un periodo statistico per il connettore status
 **/
@Schema(description="parametri di un periodo statistico per il connettore status")
public class ConnettoreStatusVerificaStatistica  {
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "")
  private TipoPeriodoStatisticoEnum frequenza = null;
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "")
  private Integer intervallo = null;
  
  @Schema(description = "tempo di permanenza in cache di un risultato, se non impostato viene usato il tempo di default della cache del controllo traffico")
 /**
   * tempo di permanenza in cache di un risultato, se non impostato viene usato il tempo di default della cache del controllo traffico  
  **/
  private Integer cacheLifeTime = null;
 /**
   * Get frequenza
   * @return frequenza
  **/
  @JsonProperty("frequenza")
  @NotNull
  @Valid
  public TipoPeriodoStatisticoEnum getFrequenza() {
    return this.frequenza;
  }

  public void setFrequenza(TipoPeriodoStatisticoEnum frequenza) {
    this.frequenza = frequenza;
  }

  public ConnettoreStatusVerificaStatistica frequenza(TipoPeriodoStatisticoEnum frequenza) {
    this.frequenza = frequenza;
    return this;
  }

 /**
   * Get intervallo
   * @return intervallo
  **/
  @JsonProperty("intervallo")
  @NotNull
  @Valid
  public Integer getIntervallo() {
    return this.intervallo;
  }

  public void setIntervallo(Integer intervallo) {
    this.intervallo = intervallo;
  }

  public ConnettoreStatusVerificaStatistica intervallo(Integer intervallo) {
    this.intervallo = intervallo;
    return this;
  }

 /**
   * tempo di permanenza in cache di un risultato, se non impostato viene usato il tempo di default della cache del controllo traffico
   * @return cacheLifeTime
  **/
  @JsonProperty("cache_life_time")
  @Valid
  public Integer getCacheLifeTime() {
    return this.cacheLifeTime;
  }

  public void setCacheLifeTime(Integer cacheLifeTime) {
    this.cacheLifeTime = cacheLifeTime;
  }

  public ConnettoreStatusVerificaStatistica cacheLifeTime(Integer cacheLifeTime) {
    this.cacheLifeTime = cacheLifeTime;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreStatusVerificaStatistica {\n");
    
    sb.append("    frequenza: ").append(ConnettoreStatusVerificaStatistica.toIndentedString(this.frequenza)).append("\n");
    sb.append("    intervallo: ").append(ConnettoreStatusVerificaStatistica.toIndentedString(this.intervallo)).append("\n");
    sb.append("    cacheLifeTime: ").append(ConnettoreStatusVerificaStatistica.toIndentedString(this.cacheLifeTime)).append("\n");
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

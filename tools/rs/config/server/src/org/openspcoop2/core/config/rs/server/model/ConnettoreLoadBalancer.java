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

public class ConnettoreLoadBalancer extends BaseConnettoriMultipliStateless implements OneOfConnettoreErogazioneConnettoreMultiplo {
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploEnum tipo = null;
  
  @Schema(required = true, description = "")
  private ConnettoreLoadBalancerStrategiaEnum strategia = null;
  
  @Schema(description = "")
  private ConnettoreLoadBalancerStickySession stickySession = null;
  
  @Schema(description = "")
  private ConnettoreLoadBalancerPassiveHealthCheck passiveHealthCheck = null;
  
  @Schema(description = "")
  private ConnettoreMultiploConfigurazioneCondizionalita consegnaCondizionale = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @Override
@JsonProperty("tipo")
  @NotNull
  @Valid
  public ConnettoreMultiploEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(ConnettoreMultiploEnum tipo) {
    this.tipo = tipo;
  }

  public ConnettoreLoadBalancer tipo(ConnettoreMultiploEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get strategia
   * @return strategia
  **/
  @JsonProperty("strategia")
  @NotNull
  @Valid
  public ConnettoreLoadBalancerStrategiaEnum getStrategia() {
    return this.strategia;
  }

  public void setStrategia(ConnettoreLoadBalancerStrategiaEnum strategia) {
    this.strategia = strategia;
  }

  public ConnettoreLoadBalancer strategia(ConnettoreLoadBalancerStrategiaEnum strategia) {
    this.strategia = strategia;
    return this;
  }

 /**
   * Get stickySession
   * @return stickySession
  **/
  @JsonProperty("sticky_session")
  @Valid
  public ConnettoreLoadBalancerStickySession getStickySession() {
    return this.stickySession;
  }

  public void setStickySession(ConnettoreLoadBalancerStickySession stickySession) {
    this.stickySession = stickySession;
  }

  public ConnettoreLoadBalancer stickySession(ConnettoreLoadBalancerStickySession stickySession) {
    this.stickySession = stickySession;
    return this;
  }

 /**
   * Get passiveHealthCheck
   * @return passiveHealthCheck
  **/
  @JsonProperty("passive_health_check")
  @Valid
  public ConnettoreLoadBalancerPassiveHealthCheck getPassiveHealthCheck() {
    return this.passiveHealthCheck;
  }

  public void setPassiveHealthCheck(ConnettoreLoadBalancerPassiveHealthCheck passiveHealthCheck) {
    this.passiveHealthCheck = passiveHealthCheck;
  }

  public ConnettoreLoadBalancer passiveHealthCheck(ConnettoreLoadBalancerPassiveHealthCheck passiveHealthCheck) {
    this.passiveHealthCheck = passiveHealthCheck;
    return this;
  }

 /**
   * Get consegnaCondizionale
   * @return consegnaCondizionale
  **/
  @JsonProperty("consegna_condizionale")
  @Valid
  public ConnettoreMultiploConfigurazioneCondizionalita getConsegnaCondizionale() {
    return this.consegnaCondizionale;
  }

  public void setConsegnaCondizionale(ConnettoreMultiploConfigurazioneCondizionalita consegnaCondizionale) {
    this.consegnaCondizionale = consegnaCondizionale;
  }

  public ConnettoreLoadBalancer consegnaCondizionale(ConnettoreMultiploConfigurazioneCondizionalita consegnaCondizionale) {
    this.consegnaCondizionale = consegnaCondizionale;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreLoadBalancer {\n");
    sb.append("    ").append(ConnettoreLoadBalancer.toIndentedString(super.toString())).append("\n");
    sb.append("    tipo: ").append(ConnettoreLoadBalancer.toIndentedString(this.tipo)).append("\n");
    sb.append("    strategia: ").append(ConnettoreLoadBalancer.toIndentedString(this.strategia)).append("\n");
    sb.append("    stickySession: ").append(ConnettoreLoadBalancer.toIndentedString(this.stickySession)).append("\n");
    sb.append("    passiveHealthCheck: ").append(ConnettoreLoadBalancer.toIndentedString(this.passiveHealthCheck)).append("\n");
    sb.append("    consegnaCondizionale: ").append(ConnettoreLoadBalancer.toIndentedString(this.consegnaCondizionale)).append("\n");
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

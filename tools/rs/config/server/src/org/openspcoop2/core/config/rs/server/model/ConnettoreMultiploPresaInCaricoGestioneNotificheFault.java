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

public class ConnettoreMultiploPresaInCaricoGestioneNotificheFault  {
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploGestioneNotificheEsitoConsegnaFaultEnum esito = null;
  
  @Schema(description = "permette di identificare puntualmente o tramite una espressione regola un faultCode in Soap o uno status claim in Rest")
 /**
   * permette di identificare puntualmente o tramite una espressione regola un faultCode in Soap o uno status claim in Rest  
  **/
  private String code = null;
  
  @Schema(description = "permette di identificare puntualmente o tramite una espressione regola un faultActor in Soap o un type claim in Rest")
 /**
   * permette di identificare puntualmente o tramite una espressione regola un faultActor in Soap o un type claim in Rest  
  **/
  private String role = null;
  
  @Schema(description = "permette di identificare puntualmente o tramite una espressione regola un faultString in Soap o il claim indicato in Rest (nome=valore)")
 /**
   * permette di identificare puntualmente o tramite una espressione regola un faultString in Soap o il claim indicato in Rest (nome=valore)  
  **/
  private String details = null;
 /**
   * Get esito
   * @return esito
  **/
  @JsonProperty("esito")
  @NotNull
  @Valid
  public ConnettoreMultiploGestioneNotificheEsitoConsegnaFaultEnum getEsito() {
    return this.esito;
  }

  public void setEsito(ConnettoreMultiploGestioneNotificheEsitoConsegnaFaultEnum esito) {
    this.esito = esito;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotificheFault esito(ConnettoreMultiploGestioneNotificheEsitoConsegnaFaultEnum esito) {
    this.esito = esito;
    return this;
  }

 /**
   * permette di identificare puntualmente o tramite una espressione regola un faultCode in Soap o uno status claim in Rest
   * @return code
  **/
  @JsonProperty("code")
  @Valid
 @Size(max=4000)  public String getCode() {
    return this.code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotificheFault code(String code) {
    this.code = code;
    return this;
  }

 /**
   * permette di identificare puntualmente o tramite una espressione regola un faultActor in Soap o un type claim in Rest
   * @return role
  **/
  @JsonProperty("role")
  @Valid
 @Size(max=4000)  public String getRole() {
    return this.role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotificheFault role(String role) {
    this.role = role;
    return this;
  }

 /**
   * permette di identificare puntualmente o tramite una espressione regola un faultString in Soap o il claim indicato in Rest (nome=valore)
   * @return details
  **/
  @JsonProperty("details")
  @Valid
 @Size(max=4000)  public String getDetails() {
    return this.details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public ConnettoreMultiploPresaInCaricoGestioneNotificheFault details(String details) {
    this.details = details;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreMultiploPresaInCaricoGestioneNotificheFault {\n");
    
    sb.append("    esito: ").append(ConnettoreMultiploPresaInCaricoGestioneNotificheFault.toIndentedString(this.esito)).append("\n");
    sb.append("    code: ").append(ConnettoreMultiploPresaInCaricoGestioneNotificheFault.toIndentedString(this.code)).append("\n");
    sb.append("    role: ").append(ConnettoreMultiploPresaInCaricoGestioneNotificheFault.toIndentedString(this.role)).append("\n");
    sb.append("    details: ").append(ConnettoreMultiploPresaInCaricoGestioneNotificheFault.toIndentedString(this.details)).append("\n");
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

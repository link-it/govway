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

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ConnettoreErogazione  {
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "tipo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreHttp.class, name = "http"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreFile.class, name = "file"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreJms.class, name = "jms"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreNull.class, name = "null"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreEcho.class, name = "echo"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettorePlugin.class, name = "plugin"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreApplicativoServer.class, name = "applicativo-server"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreMessageBox.class, name = "message-box")  })
  private OneOfConnettoreErogazioneConnettore connettore = null;
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "tipo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreLoadBalancer.class, name = "load-balancer"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreConsegnaCondizionale.class, name = "consegna-condizionale"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreConsegnaConNotifiche.class, name = "consegna-con-notifiche"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreConsegnaDestinatariMultipli.class, name = "consegna-destinatari-multipli"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreConsegnaMultiplaPlugin.class, name = "plugin")  })
  private OneOfConnettoreErogazioneConnettoreMultiplo connettoreMultiplo = null;
 /**
   * Get connettore
   * @return connettore
  **/
  @JsonProperty("connettore")
  @Valid
  public OneOfConnettoreErogazioneConnettore getConnettore() {
    return this.connettore;
  }

  public void setConnettore(OneOfConnettoreErogazioneConnettore connettore) {
    this.connettore = connettore;
  }

  public ConnettoreErogazione connettore(OneOfConnettoreErogazioneConnettore connettore) {
    this.connettore = connettore;
    return this;
  }

 /**
   * Get connettoreMultiplo
   * @return connettoreMultiplo
  **/
  @JsonProperty("connettore-multiplo")
  @Valid
  public OneOfConnettoreErogazioneConnettoreMultiplo getConnettoreMultiplo() {
    return this.connettoreMultiplo;
  }

  public void setConnettoreMultiplo(OneOfConnettoreErogazioneConnettoreMultiplo connettoreMultiplo) {
    this.connettoreMultiplo = connettoreMultiplo;
  }

  public ConnettoreErogazione connettoreMultiplo(OneOfConnettoreErogazioneConnettoreMultiplo connettoreMultiplo) {
    this.connettoreMultiplo = connettoreMultiplo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreErogazione {\n");
    
    sb.append("    connettore: ").append(ConnettoreErogazione.toIndentedString(this.connettore)).append("\n");
    sb.append("    connettoreMultiplo: ").append(ConnettoreErogazione.toIndentedString(this.connettoreMultiplo)).append("\n");
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

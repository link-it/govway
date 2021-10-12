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

public class ConnettoreConsegnaMultiplaPlugin extends BaseConnettoriMultipliPlugin implements OneOfConnettoreErogazioneConnettoreMultiplo {
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploEnum tipo = null;
  
  @Schema(required = true, description = "")
  private String plugin = null;
  
  @Schema(description = "")
  private List<Proprieta> proprieta = null;
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

  public ConnettoreConsegnaMultiplaPlugin tipo(ConnettoreMultiploEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get plugin
   * @return plugin
  **/
  @JsonProperty("plugin")
  @NotNull
  @Valid
 @Size(max=255)  public String getPlugin() {
    return this.plugin;
  }

  public void setPlugin(String plugin) {
    this.plugin = plugin;
  }

  public ConnettoreConsegnaMultiplaPlugin plugin(String plugin) {
    this.plugin = plugin;
    return this;
  }

 /**
   * Get proprieta
   * @return proprieta
  **/
  @JsonProperty("proprieta")
  @Valid
  public List<Proprieta> getProprieta() {
    return this.proprieta;
  }

  public void setProprieta(List<Proprieta> proprieta) {
    this.proprieta = proprieta;
  }

  public ConnettoreConsegnaMultiplaPlugin proprieta(List<Proprieta> proprieta) {
    this.proprieta = proprieta;
    return this;
  }

  public ConnettoreConsegnaMultiplaPlugin addProprietaItem(Proprieta proprietaItem) {
    this.proprieta.add(proprietaItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreConsegnaMultiplaPlugin {\n");
    sb.append("    ").append(ConnettoreConsegnaMultiplaPlugin.toIndentedString(super.toString())).append("\n");
    sb.append("    tipo: ").append(ConnettoreConsegnaMultiplaPlugin.toIndentedString(this.tipo)).append("\n");
    sb.append("    plugin: ").append(ConnettoreConsegnaMultiplaPlugin.toIndentedString(this.plugin)).append("\n");
    sb.append("    proprieta: ").append(ConnettoreConsegnaMultiplaPlugin.toIndentedString(this.proprieta)).append("\n");
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

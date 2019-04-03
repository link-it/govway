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

import io.swagger.v3.oas.annotations.media.Schema;
import org.openspcoop2.core.monitor.rs.server.model.TipoFiltroMittenteErogazioneEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

/**
  * Descrive le informazioni di filtraggio relative al mittente per la ricerca di transazioni relative ad erogazioni di servizio
 **/
@Schema(description="Descrive le informazioni di filtraggio relative al mittente per la ricerca di transazioni relative ad erogazioni di servizio")
public class FiltroMittenteErogazione  {
  
  @Schema(description = "")
  private TipoFiltroMittenteErogazioneEnum tipo = null;
  
  @Schema(description = "")
  private Object id = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @Valid
  public TipoFiltroMittenteErogazioneEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoFiltroMittenteErogazioneEnum tipo) {
    this.tipo = tipo;
  }

  public FiltroMittenteErogazione tipo(TipoFiltroMittenteErogazioneEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get id
   * @return id
  **/
  @JsonProperty("id")
  @Valid
  public Object getId() {
    return this.id;
  }

  public void setId(Object id) {
    this.id = id;
  }

  public FiltroMittenteErogazione id(Object id) {
    this.id = id;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroMittenteErogazione {\n");
    
    sb.append("    tipo: ").append(FiltroMittenteErogazione.toIndentedString(this.tipo)).append("\n");
    sb.append("    id: ").append(FiltroMittenteErogazione.toIndentedString(this.id)).append("\n");
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

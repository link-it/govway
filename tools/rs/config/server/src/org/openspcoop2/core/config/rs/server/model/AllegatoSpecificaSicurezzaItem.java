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
package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.AllegatoGenericoItem;
import org.openspcoop2.core.config.rs.server.model.TipoSpecificaSicurezzaEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class AllegatoSpecificaSicurezzaItem extends AllegatoGenericoItem {
  
  @Schema(required = true, description = "")
  private TipoSpecificaSicurezzaEnum tipo = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoSpecificaSicurezzaEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoSpecificaSicurezzaEnum tipo) {
    this.tipo = tipo;
  }

  public AllegatoSpecificaSicurezzaItem tipo(TipoSpecificaSicurezzaEnum tipo) {
    this.tipo = tipo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AllegatoSpecificaSicurezzaItem {\n");
    sb.append("    ").append(AllegatoSpecificaSicurezzaItem.toIndentedString(super.toString())).append("\n");
    sb.append("    tipo: ").append(AllegatoSpecificaSicurezzaItem.toIndentedString(this.tipo)).append("\n");
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

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

public class ApiImplAllegatoSpecificaSicurezza extends ApiImplAllegatoGenerico implements OneOfApiImplAllegatoAllegato {
  
  @Schema(required = true, description = "")
  private TipoSpecificaSicurezzaEnum tipoSpecifica = null;
 /**
   * Get tipoSpecifica
   * @return tipoSpecifica
  **/
  @JsonProperty("tipo_specifica")
  @NotNull
  @Valid
  public TipoSpecificaSicurezzaEnum getTipoSpecifica() {
    return this.tipoSpecifica;
  }

  public void setTipoSpecifica(TipoSpecificaSicurezzaEnum tipoSpecifica) {
    this.tipoSpecifica = tipoSpecifica;
  }

  public ApiImplAllegatoSpecificaSicurezza tipoSpecifica(TipoSpecificaSicurezzaEnum tipoSpecifica) {
    this.tipoSpecifica = tipoSpecifica;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiImplAllegatoSpecificaSicurezza {\n");
    sb.append("    ").append(ApiImplAllegatoSpecificaSicurezza.toIndentedString(super.toString())).append("\n");
    sb.append("    tipoSpecifica: ").append(ApiImplAllegatoSpecificaSicurezza.toIndentedString(this.tipoSpecifica)).append("\n");
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

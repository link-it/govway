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

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class AuthenticationHttpsCertificato extends AuthenticationHttpsBaseCertificato implements OneOfAuthenticationHttpsCertificato {
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "")
  private TipoAutenticazioneHttps tipo = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @Override
@JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoAutenticazioneHttps getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoAutenticazioneHttps tipo) {
    this.tipo = tipo;
  }

  public AuthenticationHttpsCertificato tipo(TipoAutenticazioneHttps tipo) {
    this.tipo = tipo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticationHttpsCertificato {\n");
    sb.append("    ").append(AuthenticationHttpsCertificato.toIndentedString(super.toString())).append("\n");
    sb.append("    tipo: ").append(AuthenticationHttpsCertificato.toIndentedString(this.tipo)).append("\n");
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

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 *
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.rs.server.model.TipoAutenticazioneHttps;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class AuthenticationHttps  {
  
  @Schema(required = true, description = "")
  private TipoAutenticazioneHttps tipo = null;
  
  @Schema(description = "")
  private Object certificato = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoAutenticazioneHttps getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoAutenticazioneHttps tipo) {
    this.tipo = tipo;
  }

  public AuthenticationHttps tipo(TipoAutenticazioneHttps tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get certificato
   * @return certificato
  **/
  @JsonProperty("certificato")
  @Valid
  public Object getCertificato() {
    return this.certificato;
  }

  public void setCertificato(Object certificato) {
    this.certificato = certificato;
  }

  public AuthenticationHttps certificato(Object certificato) {
    this.certificato = certificato;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticationHttps {\n");
    
    sb.append("    tipo: ").append(AuthenticationHttps.toIndentedString(this.tipo)).append("\n");
    sb.append("    certificato: ").append(AuthenticationHttps.toIndentedString(this.certificato)).append("\n");
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

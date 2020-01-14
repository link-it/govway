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

import org.openspcoop2.core.config.rs.server.model.TipoAutorizzazioneNewEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class APIImplAutorizzazioneNew  {
  
  @Schema(required = true, description = "")
  private TipoAutorizzazioneNewEnum tipo = null;
  
  @Schema(description = "")
  private Object configurazione = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoAutorizzazioneNewEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoAutorizzazioneNewEnum tipo) {
    this.tipo = tipo;
  }

  public APIImplAutorizzazioneNew tipo(TipoAutorizzazioneNewEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get configurazione
   * @return configurazione
  **/
  @JsonProperty("configurazione")
  @Valid
  public Object getConfigurazione() {
    return this.configurazione;
  }

  public void setConfigurazione(Object configurazione) {
    this.configurazione = configurazione;
  }

  public APIImplAutorizzazioneNew configurazione(Object configurazione) {
    this.configurazione = configurazione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImplAutorizzazioneNew {\n");
    
    sb.append("    tipo: ").append(APIImplAutorizzazioneNew.toIndentedString(this.tipo)).append("\n");
    sb.append("    configurazione: ").append(APIImplAutorizzazioneNew.toIndentedString(this.configurazione)).append("\n");
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

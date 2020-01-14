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

import org.openspcoop2.core.config.rs.server.model.Allegato;
import org.openspcoop2.core.config.rs.server.model.RuoloAllegatoAPIImpl;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ApiImplAllegato  {
  
  @Schema(required = true, description = "")
  private RuoloAllegatoAPIImpl ruolo = null;
  
  @Schema(required = true, description = "")
  private Allegato allegato = null;
  
  @Schema(description = "")
  private String tipoAllegato = null;
 /**
   * Get ruolo
   * @return ruolo
  **/
  @JsonProperty("ruolo")
  @NotNull
  @Valid
  public RuoloAllegatoAPIImpl getRuolo() {
    return this.ruolo;
  }

  public void setRuolo(RuoloAllegatoAPIImpl ruolo) {
    this.ruolo = ruolo;
  }

  public ApiImplAllegato ruolo(RuoloAllegatoAPIImpl ruolo) {
    this.ruolo = ruolo;
    return this;
  }

 /**
   * Get allegato
   * @return allegato
  **/
  @JsonProperty("allegato")
  @NotNull
  @Valid
  public Allegato getAllegato() {
    return this.allegato;
  }

  public void setAllegato(Allegato allegato) {
    this.allegato = allegato;
  }

  public ApiImplAllegato allegato(Allegato allegato) {
    this.allegato = allegato;
    return this;
  }

 /**
   * Get tipoAllegato
   * @return tipoAllegato
  **/
  @JsonProperty("tipo_allegato")
  @Valid
  public String getTipoAllegato() {
    return this.tipoAllegato;
  }

  public void setTipoAllegato(String tipoAllegato) {
    this.tipoAllegato = tipoAllegato;
  }

  public ApiImplAllegato tipoAllegato(String tipoAllegato) {
    this.tipoAllegato = tipoAllegato;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiImplAllegato {\n");
    
    sb.append("    ruolo: ").append(ApiImplAllegato.toIndentedString(this.ruolo)).append("\n");
    sb.append("    allegato: ").append(ApiImplAllegato.toIndentedString(this.allegato)).append("\n");
    sb.append("    tipoAllegato: ").append(ApiImplAllegato.toIndentedString(this.tipoAllegato)).append("\n");
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

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

import org.openspcoop2.core.config.rs.server.model.ApiImplConfigurazioneStato;
import org.openspcoop2.core.config.rs.server.model.StatoFunzionalitaConWarningEnum;
import org.openspcoop2.core.config.rs.server.model.TipoValidazioneEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class Validazione extends ApiImplConfigurazioneStato {
  
  @Schema(required = true, description = "")
  private StatoFunzionalitaConWarningEnum stato = null;
  
  @Schema(required = true, description = "")
  private TipoValidazioneEnum tipo = null;
  
  @Schema(example = "false", description = "")
  private Boolean mtom = false;
 /**
   * Get stato
   * @return stato
  **/
  @JsonProperty("stato")
  @NotNull
  @Valid
  public StatoFunzionalitaConWarningEnum getStato() {
    return this.stato;
  }

  public void setStato(StatoFunzionalitaConWarningEnum stato) {
    this.stato = stato;
  }

  public Validazione stato(StatoFunzionalitaConWarningEnum stato) {
    this.stato = stato;
    return this;
  }

 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoValidazioneEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoValidazioneEnum tipo) {
    this.tipo = tipo;
  }

  public Validazione tipo(TipoValidazioneEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get mtom
   * @return mtom
  **/
  @JsonProperty("mtom")
  @Valid
  public Boolean isMtom() {
    return this.mtom;
  }

  public void setMtom(Boolean mtom) {
    this.mtom = mtom;
  }

  public Validazione mtom(Boolean mtom) {
    this.mtom = mtom;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Validazione {\n");
    sb.append("    ").append(Validazione.toIndentedString(super.toString())).append("\n");
    sb.append("    stato: ").append(Validazione.toIndentedString(this.stato)).append("\n");
    sb.append("    tipo: ").append(Validazione.toIndentedString(this.tipo)).append("\n");
    sb.append("    mtom: ").append(Validazione.toIndentedString(this.mtom)).append("\n");
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

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

import org.openspcoop2.core.config.rs.server.model.RegistrazioneMessaggiConfigurazioneRegola;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RegistrazioneMessaggiConfigurazione  {
  
  @Schema(required = true, description = "")
  private Boolean abilitato = null;
  
  @Schema(description = "")
  private RegistrazioneMessaggiConfigurazioneRegola ingresso = null;
  
  @Schema(description = "")
  private RegistrazioneMessaggiConfigurazioneRegola uscita = null;
 /**
   * Get abilitato
   * @return abilitato
  **/
  @JsonProperty("abilitato")
  @NotNull
  @Valid
  public Boolean isAbilitato() {
    return this.abilitato;
  }

  public void setAbilitato(Boolean abilitato) {
    this.abilitato = abilitato;
  }

  public RegistrazioneMessaggiConfigurazione abilitato(Boolean abilitato) {
    this.abilitato = abilitato;
    return this;
  }

 /**
   * Get ingresso
   * @return ingresso
  **/
  @JsonProperty("ingresso")
  @Valid
  public RegistrazioneMessaggiConfigurazioneRegola getIngresso() {
    return this.ingresso;
  }

  public void setIngresso(RegistrazioneMessaggiConfigurazioneRegola ingresso) {
    this.ingresso = ingresso;
  }

  public RegistrazioneMessaggiConfigurazione ingresso(RegistrazioneMessaggiConfigurazioneRegola ingresso) {
    this.ingresso = ingresso;
    return this;
  }

 /**
   * Get uscita
   * @return uscita
  **/
  @JsonProperty("uscita")
  @Valid
  public RegistrazioneMessaggiConfigurazioneRegola getUscita() {
    return this.uscita;
  }

  public void setUscita(RegistrazioneMessaggiConfigurazioneRegola uscita) {
    this.uscita = uscita;
  }

  public RegistrazioneMessaggiConfigurazione uscita(RegistrazioneMessaggiConfigurazioneRegola uscita) {
    this.uscita = uscita;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegistrazioneMessaggiConfigurazione {\n");
    
    sb.append("    abilitato: ").append(RegistrazioneMessaggiConfigurazione.toIndentedString(this.abilitato)).append("\n");
    sb.append("    ingresso: ").append(RegistrazioneMessaggiConfigurazione.toIndentedString(this.ingresso)).append("\n");
    sb.append("    uscita: ").append(RegistrazioneMessaggiConfigurazione.toIndentedString(this.uscita)).append("\n");
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

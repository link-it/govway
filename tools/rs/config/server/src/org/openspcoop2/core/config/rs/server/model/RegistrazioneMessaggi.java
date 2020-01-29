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
import org.openspcoop2.core.config.rs.server.model.RegistrazioneMessaggiConfigurazione;
import org.openspcoop2.core.config.rs.server.model.StatoDefaultRidefinitoEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RegistrazioneMessaggi extends ApiImplConfigurazioneStato {
  
  @Schema(required = true, description = "")
  private StatoDefaultRidefinitoEnum stato = null;
  
  @Schema(description = "")
  private RegistrazioneMessaggiConfigurazione richiesta = null;
  
  @Schema(description = "")
  private RegistrazioneMessaggiConfigurazione risposta = null;
 /**
   * Get stato
   * @return stato
  **/
  @JsonProperty("stato")
  @NotNull
  @Valid
  public StatoDefaultRidefinitoEnum getStato() {
    return this.stato;
  }

  public void setStato(StatoDefaultRidefinitoEnum stato) {
    this.stato = stato;
  }

  public RegistrazioneMessaggi stato(StatoDefaultRidefinitoEnum stato) {
    this.stato = stato;
    return this;
  }

 /**
   * Get richiesta
   * @return richiesta
  **/
  @JsonProperty("richiesta")
  @Valid
  public RegistrazioneMessaggiConfigurazione getRichiesta() {
    return this.richiesta;
  }

  public void setRichiesta(RegistrazioneMessaggiConfigurazione richiesta) {
    this.richiesta = richiesta;
  }

  public RegistrazioneMessaggi richiesta(RegistrazioneMessaggiConfigurazione richiesta) {
    this.richiesta = richiesta;
    return this;
  }

 /**
   * Get risposta
   * @return risposta
  **/
  @JsonProperty("risposta")
  @Valid
  public RegistrazioneMessaggiConfigurazione getRisposta() {
    return this.risposta;
  }

  public void setRisposta(RegistrazioneMessaggiConfigurazione risposta) {
    this.risposta = risposta;
  }

  public RegistrazioneMessaggi risposta(RegistrazioneMessaggiConfigurazione risposta) {
    this.risposta = risposta;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegistrazioneMessaggi {\n");
    sb.append("    ").append(RegistrazioneMessaggi.toIndentedString(super.toString())).append("\n");
    sb.append("    stato: ").append(RegistrazioneMessaggi.toIndentedString(this.stato)).append("\n");
    sb.append("    richiesta: ").append(RegistrazioneMessaggi.toIndentedString(this.richiesta)).append("\n");
    sb.append("    risposta: ").append(RegistrazioneMessaggi.toIndentedString(this.risposta)).append("\n");
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

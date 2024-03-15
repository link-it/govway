/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

public class RegistrazioneTransazioniConfigurazioneInformazioniRegistrate  {
  
  @Schema(required = true, description = "")
  private TracciamentoTransazioniStatoFase token = null;
  
  @Schema(required = true, description = "")
  private TracciamentoTransazioniStatoFase tempiElaborazione = null;
 /**
   * Get token
   * @return token
  **/
  @JsonProperty("token")
  @NotNull
  @Valid
  public TracciamentoTransazioniStatoFase getToken() {
    return this.token;
  }

  public void setToken(TracciamentoTransazioniStatoFase token) {
    this.token = token;
  }

  public RegistrazioneTransazioniConfigurazioneInformazioniRegistrate token(TracciamentoTransazioniStatoFase token) {
    this.token = token;
    return this;
  }

 /**
   * Get tempiElaborazione
   * @return tempiElaborazione
  **/
  @JsonProperty("tempi_elaborazione")
  @NotNull
  @Valid
  public TracciamentoTransazioniStatoFase getTempiElaborazione() {
    return this.tempiElaborazione;
  }

  public void setTempiElaborazione(TracciamentoTransazioniStatoFase tempiElaborazione) {
    this.tempiElaborazione = tempiElaborazione;
  }

  public RegistrazioneTransazioniConfigurazioneInformazioniRegistrate tempiElaborazione(TracciamentoTransazioniStatoFase tempiElaborazione) {
    this.tempiElaborazione = tempiElaborazione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegistrazioneTransazioniConfigurazioneInformazioniRegistrate {\n");
    
    sb.append("    token: ").append(RegistrazioneTransazioniConfigurazioneInformazioniRegistrate.toIndentedString(this.token)).append("\n");
    sb.append("    tempiElaborazione: ").append(RegistrazioneTransazioniConfigurazioneInformazioniRegistrate.toIndentedString(this.tempiElaborazione)).append("\n");
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

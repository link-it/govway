/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.monitor.rs.server.model;

import org.openspcoop2.utils.service.beans.FiltroRicercaId;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class FiltroMittenteIndirizzoIP extends FiltroRicercaId implements OneOfRicercaIntervalloTemporaleMittente, OneOfRicercaStatisticaAndamentoTemporaleMittente, OneOfRicercaStatisticaDistribuzioneApiMittente, OneOfRicercaStatisticaDistribuzioneAzioneMittente, OneOfRicercaStatisticaDistribuzioneErroriMittente, OneOfRicercaStatisticaDistribuzioneEsitiMittente, OneOfRicercaStatisticaDistribuzioneSoggettoLocaleMittente, OneOfRicercaStatisticaDistribuzioneSoggettoRemotoMittente {
  
  @Schema(required = true, description = "")
  private TipoFiltroMittenteEnum identificazione = null;
  
  @Schema(description = "")
  private TipoFiltroMittenteIndirizzoIPEnum tipo = null;
 /**
   * Get identificazione
   * @return identificazione
  **/
  @Override
@JsonProperty("identificazione")
  @NotNull
  @Valid
  public TipoFiltroMittenteEnum getIdentificazione() {
    return this.identificazione;
  }

  public void setIdentificazione(TipoFiltroMittenteEnum identificazione) {
    this.identificazione = identificazione;
  }

  public FiltroMittenteIndirizzoIP identificazione(TipoFiltroMittenteEnum identificazione) {
    this.identificazione = identificazione;
    return this;
  }

 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @Valid
  public TipoFiltroMittenteIndirizzoIPEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoFiltroMittenteIndirizzoIPEnum tipo) {
    this.tipo = tipo;
  }

  public FiltroMittenteIndirizzoIP tipo(TipoFiltroMittenteIndirizzoIPEnum tipo) {
    this.tipo = tipo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroMittenteIndirizzoIP {\n");
    sb.append("    ").append(FiltroMittenteIndirizzoIP.toIndentedString(super.toString())).append("\n");
    sb.append("    identificazione: ").append(FiltroMittenteIndirizzoIP.toIndentedString(this.identificazione)).append("\n");
    sb.append("    tipo: ").append(FiltroMittenteIndirizzoIP.toIndentedString(this.tipo)).append("\n");
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

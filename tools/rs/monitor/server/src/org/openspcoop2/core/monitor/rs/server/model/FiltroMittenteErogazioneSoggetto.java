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
package org.openspcoop2.core.monitor.rs.server.model;

import org.openspcoop2.core.monitor.rs.server.model.TipoFiltroMittenteEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class FiltroMittenteErogazioneSoggetto  implements OneOfRicercaIntervalloTemporaleMittente, OneOfRicercaStatisticaAndamentoTemporaleMittente, OneOfRicercaStatisticaDistribuzioneApiMittente, OneOfRicercaStatisticaDistribuzioneAzioneMittente, OneOfRicercaStatisticaDistribuzioneEsitiMittente, OneOfRicercaStatisticaDistribuzioneSoggettoLocaleMittente {
  
  @Schema(required = true, description = "")
  private TipoFiltroMittenteEnum identificazione = null;
  
  @Schema(required = true, description = "")
  private String soggetto = null;
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

  public FiltroMittenteErogazioneSoggetto identificazione(TipoFiltroMittenteEnum identificazione) {
    this.identificazione = identificazione;
    return this;
  }

 /**
   * Get soggetto
   * @return soggetto
  **/
  @JsonProperty("soggetto")
  @NotNull
  @Valid
 @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255)  public String getSoggetto() {
    return this.soggetto;
  }

  public void setSoggetto(String soggetto) {
    this.soggetto = soggetto;
  }

  public FiltroMittenteErogazioneSoggetto soggetto(String soggetto) {
    this.soggetto = soggetto;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroMittenteErogazioneSoggetto {\n");
    
    sb.append("    identificazione: ").append(FiltroMittenteErogazioneSoggetto.toIndentedString(this.identificazione)).append("\n");
    sb.append("    soggetto: ").append(FiltroMittenteErogazioneSoggetto.toIndentedString(this.soggetto)).append("\n");
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

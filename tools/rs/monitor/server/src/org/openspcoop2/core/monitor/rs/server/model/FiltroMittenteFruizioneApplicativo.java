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

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class FiltroMittenteFruizioneApplicativo  implements OneOfRicercaIntervalloTemporaleMittente, OneOfRicercaStatisticaAndamentoTemporaleMittente, OneOfRicercaStatisticaDistribuzioneApiMittente, OneOfRicercaStatisticaDistribuzioneAzioneMittente, OneOfRicercaStatisticaDistribuzioneErroriMittente, OneOfRicercaStatisticaDistribuzioneEsitiMittente, OneOfRicercaStatisticaDistribuzioneSoggettoLocaleMittente, OneOfRicercaStatisticaDistribuzioneSoggettoRemotoMittente {
  
  @Schema(required = true, description = "")
  private TipoFiltroMittenteEnum identificazione = null;
  
  @Schema(description = "")
  private TipoIdentificazioneApplicativoEnum tipoIdentificazioneApplicativo = null;
  
  @Schema(required = true, description = "")
  private String applicativo = null;
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

  public FiltroMittenteFruizioneApplicativo identificazione(TipoFiltroMittenteEnum identificazione) {
    this.identificazione = identificazione;
    return this;
  }

 /**
   * Get tipoIdentificazioneApplicativo
   * @return tipoIdentificazioneApplicativo
  **/
  @JsonProperty("tipo_identificazione_applicativo")
  @Valid
  public TipoIdentificazioneApplicativoEnum getTipoIdentificazioneApplicativo() {
    return this.tipoIdentificazioneApplicativo;
  }

  public void setTipoIdentificazioneApplicativo(TipoIdentificazioneApplicativoEnum tipoIdentificazioneApplicativo) {
    this.tipoIdentificazioneApplicativo = tipoIdentificazioneApplicativo;
  }

  public FiltroMittenteFruizioneApplicativo tipoIdentificazioneApplicativo(TipoIdentificazioneApplicativoEnum tipoIdentificazioneApplicativo) {
    this.tipoIdentificazioneApplicativo = tipoIdentificazioneApplicativo;
    return this;
  }

 /**
   * Get applicativo
   * @return applicativo
  **/
  @JsonProperty("applicativo")
  @NotNull
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getApplicativo() {
    return this.applicativo;
  }

  public void setApplicativo(String applicativo) {
    this.applicativo = applicativo;
  }

  public FiltroMittenteFruizioneApplicativo applicativo(String applicativo) {
    this.applicativo = applicativo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroMittenteFruizioneApplicativo {\n");
    
    sb.append("    identificazione: ").append(FiltroMittenteFruizioneApplicativo.toIndentedString(this.identificazione)).append("\n");
    sb.append("    tipoIdentificazioneApplicativo: ").append(FiltroMittenteFruizioneApplicativo.toIndentedString(this.tipoIdentificazioneApplicativo)).append("\n");
    sb.append("    applicativo: ").append(FiltroMittenteFruizioneApplicativo.toIndentedString(this.applicativo)).append("\n");
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

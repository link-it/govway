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

import org.openspcoop2.core.monitor.rs.server.model.FiltroTokenClaimBase;
import org.openspcoop2.core.monitor.rs.server.model.TipoFiltroMittenteEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class FiltroMittenteTokenClaim extends FiltroTokenClaimBase implements OneOfRicercaIntervalloTemporaleMittente, OneOfRicercaStatisticaAndamentoTemporaleMittente, OneOfRicercaStatisticaDistribuzioneApiMittente, OneOfRicercaStatisticaDistribuzioneAzioneMittente, OneOfRicercaStatisticaDistribuzioneEsitiMittente, OneOfRicercaStatisticaDistribuzioneSoggettoLocaleMittente, OneOfRicercaStatisticaDistribuzioneSoggettoRemotoMittente {
  
  @Schema(example = "false", description = "")
  private Boolean ricercaEsatta = true;
  
  @Schema(example = "false", description = "")
  private Boolean caseSensitive = true;
  
  @Schema(example = "abc123", required = true, description = "")
  private String id = null;
  
  @Schema(required = true, description = "")
  private TipoFiltroMittenteEnum identificazione = null;
 /**
   * Get ricercaEsatta
   * @return ricercaEsatta
  **/
  @JsonProperty("ricerca_esatta")
  @Valid
  public Boolean isRicercaEsatta() {
    return this.ricercaEsatta;
  }

  public void setRicercaEsatta(Boolean ricercaEsatta) {
    this.ricercaEsatta = ricercaEsatta;
  }

  public FiltroMittenteTokenClaim ricercaEsatta(Boolean ricercaEsatta) {
    this.ricercaEsatta = ricercaEsatta;
    return this;
  }

 /**
   * Get caseSensitive
   * @return caseSensitive
  **/
  @JsonProperty("case_sensitive")
  @Valid
  public Boolean isCaseSensitive() {
    return this.caseSensitive;
  }

  public void setCaseSensitive(Boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  public FiltroMittenteTokenClaim caseSensitive(Boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
    return this;
  }

 /**
   * Get id
   * @return id
  **/
  @JsonProperty("id")
  @NotNull
  @Valid
  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public FiltroMittenteTokenClaim id(String id) {
    this.id = id;
    return this;
  }

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

  public FiltroMittenteTokenClaim identificazione(TipoFiltroMittenteEnum identificazione) {
    this.identificazione = identificazione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroMittenteTokenClaim {\n");
    sb.append("    ").append(FiltroMittenteTokenClaim.toIndentedString(super.toString())).append("\n");
    sb.append("    ricercaEsatta: ").append(FiltroMittenteTokenClaim.toIndentedString(this.ricercaEsatta)).append("\n");
    sb.append("    caseSensitive: ").append(FiltroMittenteTokenClaim.toIndentedString(this.caseSensitive)).append("\n");
    sb.append("    id: ").append(FiltroMittenteTokenClaim.toIndentedString(this.id)).append("\n");
    sb.append("    identificazione: ").append(FiltroMittenteTokenClaim.toIndentedString(this.identificazione)).append("\n");
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

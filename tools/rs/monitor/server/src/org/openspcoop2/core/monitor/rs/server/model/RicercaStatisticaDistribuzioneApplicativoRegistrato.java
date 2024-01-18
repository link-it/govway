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
package org.openspcoop2.core.monitor.rs.server.model;

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class RicercaStatisticaDistribuzioneApplicativoRegistrato extends RicercaStatisticaDistribuzioneApplicativo {
  
  @Schema(description = "")
  private TipoIdentificazioneApplicativoEnum tipoIdentificazioneApplicativo = null;
  
  @Schema(description = "")
  private String soggettoMittente = null;
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

  public RicercaStatisticaDistribuzioneApplicativoRegistrato tipoIdentificazioneApplicativo(TipoIdentificazioneApplicativoEnum tipoIdentificazioneApplicativo) {
    this.tipoIdentificazioneApplicativo = tipoIdentificazioneApplicativo;
    return this;
  }

 /**
   * Get soggettoMittente
   * @return soggettoMittente
  **/
  @JsonProperty("soggetto_mittente")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getSoggettoMittente() {
    return this.soggettoMittente;
  }

  public void setSoggettoMittente(String soggettoMittente) {
    this.soggettoMittente = soggettoMittente;
  }

  public RicercaStatisticaDistribuzioneApplicativoRegistrato soggettoMittente(String soggettoMittente) {
    this.soggettoMittente = soggettoMittente;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RicercaStatisticaDistribuzioneApplicativoRegistrato {\n");
    sb.append("    ").append(RicercaStatisticaDistribuzioneApplicativoRegistrato.toIndentedString(super.toString())).append("\n");
    sb.append("    tipoIdentificazioneApplicativo: ").append(RicercaStatisticaDistribuzioneApplicativoRegistrato.toIndentedString(this.tipoIdentificazioneApplicativo)).append("\n");
    sb.append("    soggettoMittente: ").append(RicercaStatisticaDistribuzioneApplicativoRegistrato.toIndentedString(this.soggettoMittente)).append("\n");
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

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

public class RicercaStatisticaDistribuzioneTokenInfo extends RicercaStatisticaDistribuzioneApplicativo {
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "")
  private TokenClaimDistribuzioneStatisticaEnum claim = null;
  
  @Schema(description = "")
  private BaseOggettoWithSimpleName soggetto = null;
 /**
   * Get claim
   * @return claim
  **/
  @JsonProperty("claim")
  @NotNull
  @Valid
  public TokenClaimDistribuzioneStatisticaEnum getClaim() {
    return this.claim;
  }

  public void setClaim(TokenClaimDistribuzioneStatisticaEnum claim) {
    this.claim = claim;
  }

  public RicercaStatisticaDistribuzioneTokenInfo claim(TokenClaimDistribuzioneStatisticaEnum claim) {
    this.claim = claim;
    return this;
  }

 /**
   * Get soggetto
   * @return soggetto
  **/
  @JsonProperty("soggetto")
  @Valid
  public BaseOggettoWithSimpleName getSoggetto() {
    return this.soggetto;
  }

  public void setSoggetto(BaseOggettoWithSimpleName soggetto) {
    this.soggetto = soggetto;
  }

  public RicercaStatisticaDistribuzioneTokenInfo soggetto(BaseOggettoWithSimpleName soggetto) {
    this.soggetto = soggetto;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RicercaStatisticaDistribuzioneTokenInfo {\n");
    sb.append("    ").append(RicercaStatisticaDistribuzioneTokenInfo.toIndentedString(super.toString())).append("\n");
    sb.append("    claim: ").append(RicercaStatisticaDistribuzioneTokenInfo.toIndentedString(this.claim)).append("\n");
    sb.append("    soggetto: ").append(RicercaStatisticaDistribuzioneTokenInfo.toIndentedString(this.soggetto)).append("\n");
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

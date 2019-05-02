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
package org.openspcoop2.core.monitor.rs.server.model;

import org.openspcoop2.core.monitor.rs.server.model.FiltroTokenClaimBase;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class FiltroMittenteErogazioneTokenClaim extends FiltroTokenClaimBase {
  
  @Schema(description = "")
  private Boolean ricercaEsatta = true;
  
  @Schema(description = "")
  private Boolean caseSensitive = true;
  
  @Schema(example = "abc123", required = true, description = "")
  private String id = null;
  
  @Schema(description = "")
  private String soggetto = null;
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

  public FiltroMittenteErogazioneTokenClaim ricercaEsatta(Boolean ricercaEsatta) {
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

  public FiltroMittenteErogazioneTokenClaim caseSensitive(Boolean caseSensitive) {
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

  public FiltroMittenteErogazioneTokenClaim id(String id) {
    this.id = id;
    return this;
  }

 /**
   * Get soggetto
   * @return soggetto
  **/
  @JsonProperty("soggetto")
  @Valid
 @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255)  public String getSoggetto() {
    return this.soggetto;
  }

  public void setSoggetto(String soggetto) {
    this.soggetto = soggetto;
  }

  public FiltroMittenteErogazioneTokenClaim soggetto(String soggetto) {
    this.soggetto = soggetto;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroMittenteErogazioneTokenClaim {\n");
    sb.append("    ").append(FiltroMittenteErogazioneTokenClaim.toIndentedString(super.toString())).append("\n");
    sb.append("    ricercaEsatta: ").append(FiltroMittenteErogazioneTokenClaim.toIndentedString(this.ricercaEsatta)).append("\n");
    sb.append("    caseSensitive: ").append(FiltroMittenteErogazioneTokenClaim.toIndentedString(this.caseSensitive)).append("\n");
    sb.append("    id: ").append(FiltroMittenteErogazioneTokenClaim.toIndentedString(this.id)).append("\n");
    sb.append("    soggetto: ").append(FiltroMittenteErogazioneTokenClaim.toIndentedString(this.soggetto)).append("\n");
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

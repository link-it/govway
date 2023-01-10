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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class FiltroApiSoggetti extends FiltroApiBase {
  
  @Schema(description = "")
  private String erogatore = null;
  
  @Schema(description = "")
  private String soggettoRemoto = null;
  
  @Schema(description = "")
  private FiltroApiImplementata apiImplementata = null;
 /**
   * Get erogatore
   * @return erogatore
  **/
  @JsonProperty("erogatore")
  @Valid
 @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255)  public String getErogatore() {
    return this.erogatore;
  }

  public void setErogatore(String erogatore) {
    this.erogatore = erogatore;
  }

  public FiltroApiSoggetti erogatore(String erogatore) {
    this.erogatore = erogatore;
    return this;
  }

 /**
   * Get soggettoRemoto
   * @return soggettoRemoto
  **/
  @JsonProperty("soggetto_remoto")
  @Valid
 @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255)  public String getSoggettoRemoto() {
    return this.soggettoRemoto;
  }

  public void setSoggettoRemoto(String soggettoRemoto) {
    this.soggettoRemoto = soggettoRemoto;
  }

  public FiltroApiSoggetti soggettoRemoto(String soggettoRemoto) {
    this.soggettoRemoto = soggettoRemoto;
    return this;
  }

 /**
   * Get apiImplementata
   * @return apiImplementata
  **/
  @JsonProperty("api_implementata")
  @Valid
  public FiltroApiImplementata getApiImplementata() {
    return this.apiImplementata;
  }

  public void setApiImplementata(FiltroApiImplementata apiImplementata) {
    this.apiImplementata = apiImplementata;
  }

  public FiltroApiSoggetti apiImplementata(FiltroApiImplementata apiImplementata) {
    this.apiImplementata = apiImplementata;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroApiSoggetti {\n");
    sb.append("    ").append(FiltroApiSoggetti.toIndentedString(super.toString())).append("\n");
    sb.append("    erogatore: ").append(FiltroApiSoggetti.toIndentedString(this.erogatore)).append("\n");
    sb.append("    soggettoRemoto: ").append(FiltroApiSoggetti.toIndentedString(this.soggettoRemoto)).append("\n");
    sb.append("    apiImplementata: ").append(FiltroApiSoggetti.toIndentedString(this.apiImplementata)).append("\n");
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

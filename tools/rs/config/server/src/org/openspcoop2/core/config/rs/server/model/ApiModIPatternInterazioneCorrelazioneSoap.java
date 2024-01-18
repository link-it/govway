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

public class ApiModIPatternInterazioneCorrelazioneSoap  {
  
  @Schema(required = true, description = "")
  private String apiNome = null;
  
  @Schema(required = true, description = "")
  private Integer apiVersione = null;
  
  @Schema(required = true, description = "")
  private String servizio = null;
  
  @Schema(description = "")
  private String azione = null;
 /**
   * Get apiNome
   * @return apiNome
  **/
  @JsonProperty("api_nome")
  @NotNull
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getApiNome() {
    return this.apiNome;
  }

  public void setApiNome(String apiNome) {
    this.apiNome = apiNome;
  }

  public ApiModIPatternInterazioneCorrelazioneSoap apiNome(String apiNome) {
    this.apiNome = apiNome;
    return this;
  }

 /**
   * Get apiVersione
   * @return apiVersione
  **/
  @JsonProperty("api_versione")
  @NotNull
  @Valid
  public Integer getApiVersione() {
    return this.apiVersione;
  }

  public void setApiVersione(Integer apiVersione) {
    this.apiVersione = apiVersione;
  }

  public ApiModIPatternInterazioneCorrelazioneSoap apiVersione(Integer apiVersione) {
    this.apiVersione = apiVersione;
    return this;
  }

 /**
   * Get servizio
   * @return servizio
  **/
  @JsonProperty("servizio")
  @NotNull
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getServizio() {
    return this.servizio;
  }

  public void setServizio(String servizio) {
    this.servizio = servizio;
  }

  public ApiModIPatternInterazioneCorrelazioneSoap servizio(String servizio) {
    this.servizio = servizio;
    return this;
  }

 /**
   * Get azione
   * @return azione
  **/
  @JsonProperty("azione")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getAzione() {
    return this.azione;
  }

  public void setAzione(String azione) {
    this.azione = azione;
  }

  public ApiModIPatternInterazioneCorrelazioneSoap azione(String azione) {
    this.azione = azione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiModIPatternInterazioneCorrelazioneSoap {\n");
    
    sb.append("    apiNome: ").append(ApiModIPatternInterazioneCorrelazioneSoap.toIndentedString(this.apiNome)).append("\n");
    sb.append("    apiVersione: ").append(ApiModIPatternInterazioneCorrelazioneSoap.toIndentedString(this.apiVersione)).append("\n");
    sb.append("    servizio: ").append(ApiModIPatternInterazioneCorrelazioneSoap.toIndentedString(this.servizio)).append("\n");
    sb.append("    azione: ").append(ApiModIPatternInterazioneCorrelazioneSoap.toIndentedString(this.azione)).append("\n");
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

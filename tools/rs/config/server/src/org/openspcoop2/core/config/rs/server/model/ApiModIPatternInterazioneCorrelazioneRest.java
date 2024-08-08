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

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ApiModIPatternInterazioneCorrelazioneRest  {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private String apiNome = null;
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private Integer apiVersione = null;
  
  @Schema(description = "")
  private HttpMethodEnum risorsaHttpMethod = null;
  
  @Schema(example = "/libri", description = "")
  private String risorsaPath = null;
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

  public ApiModIPatternInterazioneCorrelazioneRest apiNome(String apiNome) {
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

  public ApiModIPatternInterazioneCorrelazioneRest apiVersione(Integer apiVersione) {
    this.apiVersione = apiVersione;
    return this;
  }

 /**
   * Get risorsaHttpMethod
   * @return risorsaHttpMethod
  **/
  @JsonProperty("risorsa_http_method")
  @Valid
  public HttpMethodEnum getRisorsaHttpMethod() {
    return this.risorsaHttpMethod;
  }

  public void setRisorsaHttpMethod(HttpMethodEnum risorsaHttpMethod) {
    this.risorsaHttpMethod = risorsaHttpMethod;
  }

  public ApiModIPatternInterazioneCorrelazioneRest risorsaHttpMethod(HttpMethodEnum risorsaHttpMethod) {
    this.risorsaHttpMethod = risorsaHttpMethod;
    return this;
  }

 /**
   * Get risorsaPath
   * @return risorsaPath
  **/
  @JsonProperty("risorsa_path")
  @Valid
 @Size(max=255)  public String getRisorsaPath() {
    return this.risorsaPath;
  }

  public void setRisorsaPath(String risorsaPath) {
    this.risorsaPath = risorsaPath;
  }

  public ApiModIPatternInterazioneCorrelazioneRest risorsaPath(String risorsaPath) {
    this.risorsaPath = risorsaPath;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiModIPatternInterazioneCorrelazioneRest {\n");
    
    sb.append("    apiNome: ").append(ApiModIPatternInterazioneCorrelazioneRest.toIndentedString(this.apiNome)).append("\n");
    sb.append("    apiVersione: ").append(ApiModIPatternInterazioneCorrelazioneRest.toIndentedString(this.apiVersione)).append("\n");
    sb.append("    risorsaHttpMethod: ").append(ApiModIPatternInterazioneCorrelazioneRest.toIndentedString(this.risorsaHttpMethod)).append("\n");
    sb.append("    risorsaPath: ").append(ApiModIPatternInterazioneCorrelazioneRest.toIndentedString(this.risorsaPath)).append("\n");
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

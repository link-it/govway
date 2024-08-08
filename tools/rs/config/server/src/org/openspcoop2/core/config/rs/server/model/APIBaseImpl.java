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

import java.util.List;
import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class APIBaseImpl  {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private String apiNome = null;
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private Integer apiVersione = null;
  
  @Schema(description = "")
  private String apiReferente = null;
  
  @Schema(description = "")
  private String apiSoapServizio = null;
  
  @Schema(example = "[\"PagamentiTelematici\",\"Anagrafica\"]", description = "")
  private List<String> apiTags = null;
  
  @Schema(description = "")
  private String tipoServizio = null;
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

  public APIBaseImpl apiNome(String apiNome) {
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

  public APIBaseImpl apiVersione(Integer apiVersione) {
    this.apiVersione = apiVersione;
    return this;
  }

 /**
   * Get apiReferente
   * @return apiReferente
  **/
  @JsonProperty("api_referente")
  @Valid
 @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255)  public String getApiReferente() {
    return this.apiReferente;
  }

  public void setApiReferente(String apiReferente) {
    this.apiReferente = apiReferente;
  }

  public APIBaseImpl apiReferente(String apiReferente) {
    this.apiReferente = apiReferente;
    return this;
  }

 /**
   * Get apiSoapServizio
   * @return apiSoapServizio
  **/
  @JsonProperty("api_soap_servizio")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getApiSoapServizio() {
    return this.apiSoapServizio;
  }

  public void setApiSoapServizio(String apiSoapServizio) {
    this.apiSoapServizio = apiSoapServizio;
  }

  public APIBaseImpl apiSoapServizio(String apiSoapServizio) {
    this.apiSoapServizio = apiSoapServizio;
    return this;
  }

 /**
   * Get apiTags
   * @return apiTags
  **/
  @JsonProperty("api_tags")
  @Valid
  public List<String> getApiTags() {
    return this.apiTags;
  }

  public void setApiTags(List<String> apiTags) {
    this.apiTags = apiTags;
  }

  public APIBaseImpl apiTags(List<String> apiTags) {
    this.apiTags = apiTags;
    return this;
  }

  public APIBaseImpl addApiTagsItem(String apiTagsItem) {
    this.apiTags.add(apiTagsItem);
    return this;
  }

 /**
   * Get tipoServizio
   * @return tipoServizio
  **/
  @JsonProperty("tipo_servizio")
  @Valid
 @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20)  public String getTipoServizio() {
    return this.tipoServizio;
  }

  public void setTipoServizio(String tipoServizio) {
    this.tipoServizio = tipoServizio;
  }

  public APIBaseImpl tipoServizio(String tipoServizio) {
    this.tipoServizio = tipoServizio;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIBaseImpl {\n");
    
    sb.append("    apiNome: ").append(APIBaseImpl.toIndentedString(this.apiNome)).append("\n");
    sb.append("    apiVersione: ").append(APIBaseImpl.toIndentedString(this.apiVersione)).append("\n");
    sb.append("    apiReferente: ").append(APIBaseImpl.toIndentedString(this.apiReferente)).append("\n");
    sb.append("    apiSoapServizio: ").append(APIBaseImpl.toIndentedString(this.apiSoapServizio)).append("\n");
    sb.append("    apiTags: ").append(APIBaseImpl.toIndentedString(this.apiTags)).append("\n");
    sb.append("    tipoServizio: ").append(APIBaseImpl.toIndentedString(this.tipoServizio)).append("\n");
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

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
package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import org.openspcoop2.utils.service.beans.BaseSoggettoItem;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ApiImplVersioneApiView extends BaseSoggettoItem {
  
  @Schema(required = true, description = "")
  private String apiNome = null;
  
  @Schema(required = true, description = "")
  private Integer apiVersione = null;
  
  @Schema(description = "")
  private String apiReferente = null;
  
  @Schema(description = "")
  private String apiSoapServizio = null;
  
  @Schema(example = "[\"PagamentiTelematici\",\"Anagrafica\"]", description = "")
  private List<String> apiTags = null;
  
  @Schema(description = "")
  private String tipoServizio = null;
  
  @Schema(example = "[1,2,3]", required = true, description = "")
  private List<Integer> versioni = new ArrayList<Integer>();
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

  public ApiImplVersioneApiView apiNome(String apiNome) {
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

  public ApiImplVersioneApiView apiVersione(Integer apiVersione) {
    this.apiVersione = apiVersione;
    return this;
  }

 /**
   * Get apiReferente
   * @return apiReferente
  **/
  @JsonProperty("api_referente")
  @Valid
 @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255)  public String getApiReferente() {
    return this.apiReferente;
  }

  public void setApiReferente(String apiReferente) {
    this.apiReferente = apiReferente;
  }

  public ApiImplVersioneApiView apiReferente(String apiReferente) {
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

  public ApiImplVersioneApiView apiSoapServizio(String apiSoapServizio) {
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

  public ApiImplVersioneApiView apiTags(List<String> apiTags) {
    this.apiTags = apiTags;
    return this;
  }

  public ApiImplVersioneApiView addApiTagsItem(String apiTagsItem) {
    this.apiTags.add(apiTagsItem);
    return this;
  }

 /**
   * Get tipoServizio
   * @return tipoServizio
  **/
  @JsonProperty("tipo_servizio")
  @Valid
 @Size(max=20)  public String getTipoServizio() {
    return this.tipoServizio;
  }

  public void setTipoServizio(String tipoServizio) {
    this.tipoServizio = tipoServizio;
  }

  public ApiImplVersioneApiView tipoServizio(String tipoServizio) {
    this.tipoServizio = tipoServizio;
    return this;
  }

 /**
   * Get versioni
   * @return versioni
  **/
  @JsonProperty("versioni")
  @NotNull
  @Valid
  public List<Integer> getVersioni() {
    return this.versioni;
  }

  public void setVersioni(List<Integer> versioni) {
    this.versioni = versioni;
  }

  public ApiImplVersioneApiView versioni(List<Integer> versioni) {
    this.versioni = versioni;
    return this;
  }

  public ApiImplVersioneApiView addVersioniItem(Integer versioniItem) {
    this.versioni.add(versioniItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiImplVersioneApiView {\n");
    sb.append("    ").append(ApiImplVersioneApiView.toIndentedString(super.toString())).append("\n");
    sb.append("    apiNome: ").append(ApiImplVersioneApiView.toIndentedString(this.apiNome)).append("\n");
    sb.append("    apiVersione: ").append(ApiImplVersioneApiView.toIndentedString(this.apiVersione)).append("\n");
    sb.append("    apiReferente: ").append(ApiImplVersioneApiView.toIndentedString(this.apiReferente)).append("\n");
    sb.append("    apiSoapServizio: ").append(ApiImplVersioneApiView.toIndentedString(this.apiSoapServizio)).append("\n");
    sb.append("    apiTags: ").append(ApiImplVersioneApiView.toIndentedString(this.apiTags)).append("\n");
    sb.append("    tipoServizio: ").append(ApiImplVersioneApiView.toIndentedString(this.tipoServizio)).append("\n");
    sb.append("    versioni: ").append(ApiImplVersioneApiView.toIndentedString(this.versioni)).append("\n");
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

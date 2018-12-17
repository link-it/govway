package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class APIBaseImpl  {
  
  @Schema(required = true, description = "")
  private String apiNome = null;
  
  @Schema(required = true, description = "")
  private Integer apiVersione = null;
  
  @Schema(description = "")
  private String apiSoapServizio = null;
  
  @Schema(description = "")
  private String tipoServizio = null;
 /**
   * Get apiNome
   * @return apiNome
  **/
  @JsonProperty("api_nome")
  @NotNull
  public String getApiNome() {
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
   * Get apiSoapServizio
   * @return apiSoapServizio
  **/
  @JsonProperty("api_soap_servizio")
  public String getApiSoapServizio() {
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
   * Get tipoServizio
   * @return tipoServizio
  **/
  @JsonProperty("tipo_servizio")
  public String getTipoServizio() {
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
    sb.append("    apiSoapServizio: ").append(APIBaseImpl.toIndentedString(this.apiSoapServizio)).append("\n");
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

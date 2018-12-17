package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import org.openspcoop2.core.config.rs.server.model.BaseSoggettoItem;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiImplVersioneApiView extends BaseSoggettoItem {
  
  @Schema(required = true, description = "")
  private String apiNome = null;
  
  @Schema(required = true, description = "")
  private Integer apiVersione = null;
  
  @Schema(description = "")
  private String apiSoapServizio = null;
  
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
  public String getApiNome() {
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

  public ApiImplVersioneApiView apiSoapServizio(String apiSoapServizio) {
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
    sb.append("    apiSoapServizio: ").append(ApiImplVersioneApiView.toIndentedString(this.apiSoapServizio)).append("\n");
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

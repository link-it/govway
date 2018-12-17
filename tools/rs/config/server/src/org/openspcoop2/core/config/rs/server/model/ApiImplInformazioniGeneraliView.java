package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.BaseItem;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiImplInformazioniGeneraliView extends BaseItem {
  
  @Schema(description = "")
  private String tipo = null;
  
  @Schema(required = true, description = "")
  private String nome = null;
  
  @Schema(description = "")
  private String apiSoapServizio = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  public String getTipo() {
    return this.tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public ApiImplInformazioniGeneraliView tipo(String tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @NotNull
  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public ApiImplInformazioniGeneraliView nome(String nome) {
    this.nome = nome;
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

  public ApiImplInformazioniGeneraliView apiSoapServizio(String apiSoapServizio) {
    this.apiSoapServizio = apiSoapServizio;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiImplInformazioniGeneraliView {\n");
    sb.append("    ").append(ApiImplInformazioniGeneraliView.toIndentedString(super.toString())).append("\n");
    sb.append("    tipo: ").append(ApiImplInformazioniGeneraliView.toIndentedString(this.tipo)).append("\n");
    sb.append("    nome: ").append(ApiImplInformazioniGeneraliView.toIndentedString(this.nome)).append("\n");
    sb.append("    apiSoapServizio: ").append(ApiImplInformazioniGeneraliView.toIndentedString(this.apiSoapServizio)).append("\n");
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

package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class FiltroIdApplicativo  {
  
  @Schema(description = "")
  private Boolean ricercaEsatta = null;
  
  @Schema(description = "")
  private Boolean caseSensitive = null;
  
  @Schema(example = "abc123", description = "")
  private String idApplicativo = null;
 /**
   * Get ricercaEsatta
   * @return ricercaEsatta
  **/
  @JsonProperty("ricercaEsatta")
  @Valid
  public Boolean isRicercaEsatta() {
    return this.ricercaEsatta;
  }

  public void setRicercaEsatta(Boolean ricercaEsatta) {
    this.ricercaEsatta = ricercaEsatta;
  }

  public FiltroIdApplicativo ricercaEsatta(Boolean ricercaEsatta) {
    this.ricercaEsatta = ricercaEsatta;
    return this;
  }

 /**
   * Get caseSensitive
   * @return caseSensitive
  **/
  @JsonProperty("caseSensitive")
  @Valid
  public Boolean isCaseSensitive() {
    return this.caseSensitive;
  }

  public void setCaseSensitive(Boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  public FiltroIdApplicativo caseSensitive(Boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
    return this;
  }

 /**
   * Get idApplicativo
   * @return idApplicativo
  **/
  @JsonProperty("idApplicativo")
  @Valid
  public String getIdApplicativo() {
    return this.idApplicativo;
  }

  public void setIdApplicativo(String idApplicativo) {
    this.idApplicativo = idApplicativo;
  }

  public FiltroIdApplicativo idApplicativo(String idApplicativo) {
    this.idApplicativo = idApplicativo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroIdApplicativo {\n");
    
    sb.append("    ricercaEsatta: ").append(FiltroIdApplicativo.toIndentedString(this.ricercaEsatta)).append("\n");
    sb.append("    caseSensitive: ").append(FiltroIdApplicativo.toIndentedString(this.caseSensitive)).append("\n");
    sb.append("    idApplicativo: ").append(FiltroIdApplicativo.toIndentedString(this.idApplicativo)).append("\n");
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

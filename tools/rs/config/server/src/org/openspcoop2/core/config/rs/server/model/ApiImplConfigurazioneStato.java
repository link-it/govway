package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiImplConfigurazioneStato  {
  
  @Schema(required = true, description = "funzionalità non supportata al momento, indicare sempre 'true'.")
 /**
   * funzionalità non supportata al momento, indicare sempre 'true'.  
  **/
  private Boolean ridefinito = true;
 /**
   * funzionalità non supportata al momento, indicare sempre &#x27;true&#x27;.
   * @return ridefinito
  **/
  @JsonProperty("ridefinito")
  @NotNull
  public Boolean isRidefinito() {
    return this.ridefinito;
  }

  public void setRidefinito(Boolean ridefinito) {
    this.ridefinito = ridefinito;
  }

  public ApiImplConfigurazioneStato ridefinito(Boolean ridefinito) {
    this.ridefinito = ridefinito;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiImplConfigurazioneStato {\n");
    
    sb.append("    ridefinito: ").append(ApiImplConfigurazioneStato.toIndentedString(this.ridefinito)).append("\n");
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

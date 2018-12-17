package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazioneBaseConfig;
import org.openspcoop2.core.config.rs.server.model.AllAnyEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class APIImplAutorizzazioneConfig extends APIImplAutorizzazioneBaseConfig {
  
  @Schema(description = "")
  private Boolean scope = false;
  
  @Schema(description = "")
  private AllAnyEnum scopeRichiesti = null;
  
  @Schema(description = "")
  private Boolean token = false;
  
  @Schema(description = "Indicare per riga i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola")
 /**
   * Indicare per riga i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola  
  **/
  private String tokenClaims = null;
 /**
   * Get scope
   * @return scope
  **/
  @JsonProperty("scope")
  public Boolean isScope() {
    return this.scope;
  }

  public void setScope(Boolean scope) {
    this.scope = scope;
  }

  public APIImplAutorizzazioneConfig scope(Boolean scope) {
    this.scope = scope;
    return this;
  }

 /**
   * Get scopeRichiesti
   * @return scopeRichiesti
  **/
  @JsonProperty("scope_richiesti")
  public AllAnyEnum getScopeRichiesti() {
    return this.scopeRichiesti;
  }

  public void setScopeRichiesti(AllAnyEnum scopeRichiesti) {
    this.scopeRichiesti = scopeRichiesti;
  }

  public APIImplAutorizzazioneConfig scopeRichiesti(AllAnyEnum scopeRichiesti) {
    this.scopeRichiesti = scopeRichiesti;
    return this;
  }

 /**
   * Get token
   * @return token
  **/
  @JsonProperty("token")
  public Boolean isToken() {
    return this.token;
  }

  public void setToken(Boolean token) {
    this.token = token;
  }

  public APIImplAutorizzazioneConfig token(Boolean token) {
    this.token = token;
    return this;
  }

 /**
   * Indicare per riga i claims richiesti (nome&#x3D;valore); è possibile elencare differenti valori ammissibili separandoli con la virgola
   * @return tokenClaims
  **/
  @JsonProperty("token_claims")
 @Size(max=4000)  public String getTokenClaims() {
    return this.tokenClaims;
  }

  public void setTokenClaims(String tokenClaims) {
    this.tokenClaims = tokenClaims;
  }

  public APIImplAutorizzazioneConfig tokenClaims(String tokenClaims) {
    this.tokenClaims = tokenClaims;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImplAutorizzazioneConfig {\n");
    sb.append("    ").append(APIImplAutorizzazioneConfig.toIndentedString(super.toString())).append("\n");
    sb.append("    scope: ").append(APIImplAutorizzazioneConfig.toIndentedString(this.scope)).append("\n");
    sb.append("    scopeRichiesti: ").append(APIImplAutorizzazioneConfig.toIndentedString(this.scopeRichiesti)).append("\n");
    sb.append("    token: ").append(APIImplAutorizzazioneConfig.toIndentedString(this.token)).append("\n");
    sb.append("    tokenClaims: ").append(APIImplAutorizzazioneConfig.toIndentedString(this.tokenClaims)).append("\n");
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

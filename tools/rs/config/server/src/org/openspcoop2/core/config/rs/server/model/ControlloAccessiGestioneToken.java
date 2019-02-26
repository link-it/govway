package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.ApiImplConfigurazioneStato;
import org.openspcoop2.core.config.rs.server.model.StatoFunzionalitaConWarningEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ControlloAccessiGestioneToken extends ApiImplConfigurazioneStato {
  
  @Schema(description = "indica se la gestione del token è abilitata o meno")
 /**
   * indica se la gestione del token è abilitata o meno  
  **/
  private Boolean abilitato = null;
  
  @Schema(description = "identificativo della Policy da utilizzare per la gestione del token")
 /**
   * identificativo della Policy da utilizzare per la gestione del token  
  **/
  private String policy = null;
  
  @Schema(description = "indica se la presenza del token è obbligatoria o opzionale")
 /**
   * indica se la presenza del token è obbligatoria o opzionale  
  **/
  private Boolean tokenOpzionale = false;
  
  @Schema(description = "")
  private StatoFunzionalitaConWarningEnum validazioneJwt = null;
  
  @Schema(description = "")
  private StatoFunzionalitaConWarningEnum introspection = null;
  
  @Schema(description = "")
  private StatoFunzionalitaConWarningEnum userInfo = null;
  
  @Schema(description = "indica se il forward del token, nelle modalità descritte nella policy, è attivo o meno")
 /**
   * indica se il forward del token, nelle modalità descritte nella policy, è attivo o meno  
  **/
  private Boolean tokenForward = true;
 /**
   * indica se la gestione del token è abilitata o meno
   * @return abilitato
  **/
  @JsonProperty("abilitato")
  @Valid
  public Boolean isAbilitato() {
    return this.abilitato;
  }

  public void setAbilitato(Boolean abilitato) {
    this.abilitato = abilitato;
  }

  public ControlloAccessiGestioneToken abilitato(Boolean abilitato) {
    this.abilitato = abilitato;
    return this;
  }

 /**
   * identificativo della Policy da utilizzare per la gestione del token
   * @return policy
  **/
  @JsonProperty("policy")
  @Valid
 @Size(max=255)  public String getPolicy() {
    return this.policy;
  }

  public void setPolicy(String policy) {
    this.policy = policy;
  }

  public ControlloAccessiGestioneToken policy(String policy) {
    this.policy = policy;
    return this;
  }

 /**
   * indica se la presenza del token è obbligatoria o opzionale
   * @return tokenOpzionale
  **/
  @JsonProperty("token_opzionale")
  @Valid
  public Boolean isTokenOpzionale() {
    return this.tokenOpzionale;
  }

  public void setTokenOpzionale(Boolean tokenOpzionale) {
    this.tokenOpzionale = tokenOpzionale;
  }

  public ControlloAccessiGestioneToken tokenOpzionale(Boolean tokenOpzionale) {
    this.tokenOpzionale = tokenOpzionale;
    return this;
  }

 /**
   * Get validazioneJwt
   * @return validazioneJwt
  **/
  @JsonProperty("validazione_jwt")
  @Valid
  public StatoFunzionalitaConWarningEnum getValidazioneJwt() {
    return this.validazioneJwt;
  }

  public void setValidazioneJwt(StatoFunzionalitaConWarningEnum validazioneJwt) {
    this.validazioneJwt = validazioneJwt;
  }

  public ControlloAccessiGestioneToken validazioneJwt(StatoFunzionalitaConWarningEnum validazioneJwt) {
    this.validazioneJwt = validazioneJwt;
    return this;
  }

 /**
   * Get introspection
   * @return introspection
  **/
  @JsonProperty("introspection")
  @Valid
  public StatoFunzionalitaConWarningEnum getIntrospection() {
    return this.introspection;
  }

  public void setIntrospection(StatoFunzionalitaConWarningEnum introspection) {
    this.introspection = introspection;
  }

  public ControlloAccessiGestioneToken introspection(StatoFunzionalitaConWarningEnum introspection) {
    this.introspection = introspection;
    return this;
  }

 /**
   * Get userInfo
   * @return userInfo
  **/
  @JsonProperty("user_info")
  @Valid
  public StatoFunzionalitaConWarningEnum getUserInfo() {
    return this.userInfo;
  }

  public void setUserInfo(StatoFunzionalitaConWarningEnum userInfo) {
    this.userInfo = userInfo;
  }

  public ControlloAccessiGestioneToken userInfo(StatoFunzionalitaConWarningEnum userInfo) {
    this.userInfo = userInfo;
    return this;
  }

 /**
   * indica se il forward del token, nelle modalità descritte nella policy, è attivo o meno
   * @return tokenForward
  **/
  @JsonProperty("token_forward")
  @Valid
  public Boolean isTokenForward() {
    return this.tokenForward;
  }

  public void setTokenForward(Boolean tokenForward) {
    this.tokenForward = tokenForward;
  }

  public ControlloAccessiGestioneToken tokenForward(Boolean tokenForward) {
    this.tokenForward = tokenForward;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ControlloAccessiGestioneToken {\n");
    sb.append("    ").append(ControlloAccessiGestioneToken.toIndentedString(super.toString())).append("\n");
    sb.append("    abilitato: ").append(ControlloAccessiGestioneToken.toIndentedString(this.abilitato)).append("\n");
    sb.append("    policy: ").append(ControlloAccessiGestioneToken.toIndentedString(this.policy)).append("\n");
    sb.append("    tokenOpzionale: ").append(ControlloAccessiGestioneToken.toIndentedString(this.tokenOpzionale)).append("\n");
    sb.append("    validazioneJwt: ").append(ControlloAccessiGestioneToken.toIndentedString(this.validazioneJwt)).append("\n");
    sb.append("    introspection: ").append(ControlloAccessiGestioneToken.toIndentedString(this.introspection)).append("\n");
    sb.append("    userInfo: ").append(ControlloAccessiGestioneToken.toIndentedString(this.userInfo)).append("\n");
    sb.append("    tokenForward: ").append(ControlloAccessiGestioneToken.toIndentedString(this.tokenForward)).append("\n");
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

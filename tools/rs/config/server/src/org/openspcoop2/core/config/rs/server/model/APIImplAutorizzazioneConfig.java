/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.rs.server.model.AllAnyEnum;
import org.openspcoop2.core.config.rs.server.model.FonteEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class APIImplAutorizzazioneConfig  {
  
  @Schema(required = true, description = "")
  private Boolean richiedente = true;
  
  @Schema(required = true, description = "")
  private Boolean ruoli = false;
  
  @Schema(description = "")
  private FonteEnum ruoliFonte = null;
  
  @Schema(description = "")
  private AllAnyEnum ruoliRichiesti = null;
  
  @Schema(required = true, description = "")
  private Boolean scope = false;
  
  @Schema(description = "")
  private AllAnyEnum scopeRichiesti = null;
  
  @Schema(required = true, description = "")
  private Boolean token = false;
  
  @Schema(description = "Indicare per riga i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola")
 /**
   * Indicare per riga i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola  
  **/
  private String tokenClaims = null;
 /**
   * Get richiedente
   * @return richiedente
  **/
  @JsonProperty("richiedente")
  @NotNull
  @Valid
  public Boolean isRichiedente() {
    return this.richiedente;
  }

  public void setRichiedente(Boolean richiedente) {
    this.richiedente = richiedente;
  }

  public APIImplAutorizzazioneConfig richiedente(Boolean richiedente) {
    this.richiedente = richiedente;
    return this;
  }

 /**
   * Get ruoli
   * @return ruoli
  **/
  @JsonProperty("ruoli")
  @NotNull
  @Valid
  public Boolean isRuoli() {
    return this.ruoli;
  }

  public void setRuoli(Boolean ruoli) {
    this.ruoli = ruoli;
  }

  public APIImplAutorizzazioneConfig ruoli(Boolean ruoli) {
    this.ruoli = ruoli;
    return this;
  }

 /**
   * Get ruoliFonte
   * @return ruoliFonte
  **/
  @JsonProperty("ruoli_fonte")
  @Valid
  public FonteEnum getRuoliFonte() {
    return this.ruoliFonte;
  }

  public void setRuoliFonte(FonteEnum ruoliFonte) {
    this.ruoliFonte = ruoliFonte;
  }

  public APIImplAutorizzazioneConfig ruoliFonte(FonteEnum ruoliFonte) {
    this.ruoliFonte = ruoliFonte;
    return this;
  }

 /**
   * Get ruoliRichiesti
   * @return ruoliRichiesti
  **/
  @JsonProperty("ruoli_richiesti")
  @Valid
  public AllAnyEnum getRuoliRichiesti() {
    return this.ruoliRichiesti;
  }

  public void setRuoliRichiesti(AllAnyEnum ruoliRichiesti) {
    this.ruoliRichiesti = ruoliRichiesti;
  }

  public APIImplAutorizzazioneConfig ruoliRichiesti(AllAnyEnum ruoliRichiesti) {
    this.ruoliRichiesti = ruoliRichiesti;
    return this;
  }

 /**
   * Get scope
   * @return scope
  **/
  @JsonProperty("scope")
  @NotNull
  @Valid
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
  @Valid
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
  @NotNull
  @Valid
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
   * Indicare per riga i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola
   * @return tokenClaims
  **/
  @JsonProperty("token_claims")
  @Valid
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
    
    sb.append("    richiedente: ").append(APIImplAutorizzazioneConfig.toIndentedString(this.richiedente)).append("\n");
    sb.append("    ruoli: ").append(APIImplAutorizzazioneConfig.toIndentedString(this.ruoli)).append("\n");
    sb.append("    ruoliFonte: ").append(APIImplAutorizzazioneConfig.toIndentedString(this.ruoliFonte)).append("\n");
    sb.append("    ruoliRichiesti: ").append(APIImplAutorizzazioneConfig.toIndentedString(this.ruoliRichiesti)).append("\n");
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

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

public class APIImplAutorizzazioneAbilitata  implements OneOfControlloAccessiAutorizzazioneAutorizzazione, OneOfControlloAccessiAutorizzazioneViewAutorizzazione {
  
  @Schema(required = true, description = "")
  private TipoAutorizzazioneEnum tipo = null;
  
  @Schema(example = "false", required = true, description = "")
  private Boolean richiedente = true;
  
  @Schema(example = "false", required = true, description = "")
  private Boolean ruoli = false;
  
  @Schema(description = "")
  private FonteEnum ruoliFonte = null;
  
  @Schema(description = "")
  private AllAnyEnum ruoliRichiesti = null;
  
  @Schema(example = "false", description = "")
  private Boolean tokenRichiedente = true;
  
  @Schema(example = "false", description = "")
  private Boolean tokenRuoli = false;
  
  @Schema(description = "")
  private FonteEnum tokenRuoliFonte = null;
  
  @Schema(description = "")
  private AllAnyEnum tokenRuoliRichiesti = null;
  
  @Schema(example = "false", required = true, description = "")
  private Boolean scope = false;
  
  @Schema(description = "")
  private AllAnyEnum scopeRichiesti = null;
  
  @Schema(example = "false", required = true, description = "")
  private Boolean token = false;
  
  @Schema(description = "Indicare i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola")
 /**
   * Indicare i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola  
  **/
  private List<String> tokenClaims = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @Override
@JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoAutorizzazioneEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoAutorizzazioneEnum tipo) {
    this.tipo = tipo;
  }

  public APIImplAutorizzazioneAbilitata tipo(TipoAutorizzazioneEnum tipo) {
    this.tipo = tipo;
    return this;
  }

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

  public APIImplAutorizzazioneAbilitata richiedente(Boolean richiedente) {
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

  public APIImplAutorizzazioneAbilitata ruoli(Boolean ruoli) {
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

  public APIImplAutorizzazioneAbilitata ruoliFonte(FonteEnum ruoliFonte) {
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

  public APIImplAutorizzazioneAbilitata ruoliRichiesti(AllAnyEnum ruoliRichiesti) {
    this.ruoliRichiesti = ruoliRichiesti;
    return this;
  }

 /**
   * Get tokenRichiedente
   * @return tokenRichiedente
  **/
  @JsonProperty("token_richiedente")
  @Valid
  public Boolean isTokenRichiedente() {
    return this.tokenRichiedente;
  }

  public void setTokenRichiedente(Boolean tokenRichiedente) {
    this.tokenRichiedente = tokenRichiedente;
  }

  public APIImplAutorizzazioneAbilitata tokenRichiedente(Boolean tokenRichiedente) {
    this.tokenRichiedente = tokenRichiedente;
    return this;
  }

 /**
   * Get tokenRuoli
   * @return tokenRuoli
  **/
  @JsonProperty("token_ruoli")
  @Valid
  public Boolean isTokenRuoli() {
    return this.tokenRuoli;
  }

  public void setTokenRuoli(Boolean tokenRuoli) {
    this.tokenRuoli = tokenRuoli;
  }

  public APIImplAutorizzazioneAbilitata tokenRuoli(Boolean tokenRuoli) {
    this.tokenRuoli = tokenRuoli;
    return this;
  }

 /**
   * Get tokenRuoliFonte
   * @return tokenRuoliFonte
  **/
  @JsonProperty("token_ruoli_fonte")
  @Valid
  public FonteEnum getTokenRuoliFonte() {
    return this.tokenRuoliFonte;
  }

  public void setTokenRuoliFonte(FonteEnum tokenRuoliFonte) {
    this.tokenRuoliFonte = tokenRuoliFonte;
  }

  public APIImplAutorizzazioneAbilitata tokenRuoliFonte(FonteEnum tokenRuoliFonte) {
    this.tokenRuoliFonte = tokenRuoliFonte;
    return this;
  }

 /**
   * Get tokenRuoliRichiesti
   * @return tokenRuoliRichiesti
  **/
  @JsonProperty("token_ruoli_richiesti")
  @Valid
  public AllAnyEnum getTokenRuoliRichiesti() {
    return this.tokenRuoliRichiesti;
  }

  public void setTokenRuoliRichiesti(AllAnyEnum tokenRuoliRichiesti) {
    this.tokenRuoliRichiesti = tokenRuoliRichiesti;
  }

  public APIImplAutorizzazioneAbilitata tokenRuoliRichiesti(AllAnyEnum tokenRuoliRichiesti) {
    this.tokenRuoliRichiesti = tokenRuoliRichiesti;
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

  public APIImplAutorizzazioneAbilitata scope(Boolean scope) {
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

  public APIImplAutorizzazioneAbilitata scopeRichiesti(AllAnyEnum scopeRichiesti) {
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

  public APIImplAutorizzazioneAbilitata token(Boolean token) {
    this.token = token;
    return this;
  }

 /**
   * Indicare i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola
   * @return tokenClaims
  **/
  @JsonProperty("token_claims")
  @Valid
  public List<String> getTokenClaims() {
    return this.tokenClaims;
  }

  public void setTokenClaims(List<String> tokenClaims) {
    this.tokenClaims = tokenClaims;
  }

  public APIImplAutorizzazioneAbilitata tokenClaims(List<String> tokenClaims) {
    this.tokenClaims = tokenClaims;
    return this;
  }

  public APIImplAutorizzazioneAbilitata addTokenClaimsItem(String tokenClaimsItem) {
    this.tokenClaims.add(tokenClaimsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImplAutorizzazioneAbilitata {\n");
    
    sb.append("    tipo: ").append(APIImplAutorizzazioneAbilitata.toIndentedString(this.tipo)).append("\n");
    sb.append("    richiedente: ").append(APIImplAutorizzazioneAbilitata.toIndentedString(this.richiedente)).append("\n");
    sb.append("    ruoli: ").append(APIImplAutorizzazioneAbilitata.toIndentedString(this.ruoli)).append("\n");
    sb.append("    ruoliFonte: ").append(APIImplAutorizzazioneAbilitata.toIndentedString(this.ruoliFonte)).append("\n");
    sb.append("    ruoliRichiesti: ").append(APIImplAutorizzazioneAbilitata.toIndentedString(this.ruoliRichiesti)).append("\n");
    sb.append("    tokenRichiedente: ").append(APIImplAutorizzazioneAbilitata.toIndentedString(this.tokenRichiedente)).append("\n");
    sb.append("    tokenRuoli: ").append(APIImplAutorizzazioneAbilitata.toIndentedString(this.tokenRuoli)).append("\n");
    sb.append("    tokenRuoliFonte: ").append(APIImplAutorizzazioneAbilitata.toIndentedString(this.tokenRuoliFonte)).append("\n");
    sb.append("    tokenRuoliRichiesti: ").append(APIImplAutorizzazioneAbilitata.toIndentedString(this.tokenRuoliRichiesti)).append("\n");
    sb.append("    scope: ").append(APIImplAutorizzazioneAbilitata.toIndentedString(this.scope)).append("\n");
    sb.append("    scopeRichiesti: ").append(APIImplAutorizzazioneAbilitata.toIndentedString(this.scopeRichiesti)).append("\n");
    sb.append("    token: ").append(APIImplAutorizzazioneAbilitata.toIndentedString(this.token)).append("\n");
    sb.append("    tokenClaims: ").append(APIImplAutorizzazioneAbilitata.toIndentedString(this.tokenClaims)).append("\n");
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

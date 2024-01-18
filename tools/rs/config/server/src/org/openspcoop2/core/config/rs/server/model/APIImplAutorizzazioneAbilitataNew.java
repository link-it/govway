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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class APIImplAutorizzazioneAbilitataNew  implements OneOfAPIImplAutorizzazione {
  
  @Schema(required = true, description = "")
  private TipoAutorizzazioneEnum tipo = null;
  
  @Schema(example = "false", required = true, description = "")
  private Boolean richiedente = true;
  
  @Schema(description = "")
  private String soggetto = null;
  
  @Schema(example = "false", required = true, description = "")
  private Boolean ruoli = false;
  
  @Schema(description = "")
  private FonteEnum ruoliFonte = null;
  
  @Schema(description = "")
  private AllAnyEnum ruoliRichiesti = null;
  
  @Schema(description = "")
  private String ruolo = null;
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

  public APIImplAutorizzazioneAbilitataNew tipo(TipoAutorizzazioneEnum tipo) {
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

  public APIImplAutorizzazioneAbilitataNew richiedente(Boolean richiedente) {
    this.richiedente = richiedente;
    return this;
  }

 /**
   * Get soggetto
   * @return soggetto
  **/
  @JsonProperty("soggetto")
  @Valid
 @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255)  public String getSoggetto() {
    return this.soggetto;
  }

  public void setSoggetto(String soggetto) {
    this.soggetto = soggetto;
  }

  public APIImplAutorizzazioneAbilitataNew soggetto(String soggetto) {
    this.soggetto = soggetto;
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

  public APIImplAutorizzazioneAbilitataNew ruoli(Boolean ruoli) {
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

  public APIImplAutorizzazioneAbilitataNew ruoliFonte(FonteEnum ruoliFonte) {
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

  public APIImplAutorizzazioneAbilitataNew ruoliRichiesti(AllAnyEnum ruoliRichiesti) {
    this.ruoliRichiesti = ruoliRichiesti;
    return this;
  }

 /**
   * Get ruolo
   * @return ruolo
  **/
  @JsonProperty("ruolo")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getRuolo() {
    return this.ruolo;
  }

  public void setRuolo(String ruolo) {
    this.ruolo = ruolo;
  }

  public APIImplAutorizzazioneAbilitataNew ruolo(String ruolo) {
    this.ruolo = ruolo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImplAutorizzazioneAbilitataNew {\n");
    
    sb.append("    tipo: ").append(APIImplAutorizzazioneAbilitataNew.toIndentedString(this.tipo)).append("\n");
    sb.append("    richiedente: ").append(APIImplAutorizzazioneAbilitataNew.toIndentedString(this.richiedente)).append("\n");
    sb.append("    soggetto: ").append(APIImplAutorizzazioneAbilitataNew.toIndentedString(this.soggetto)).append("\n");
    sb.append("    ruoli: ").append(APIImplAutorizzazioneAbilitataNew.toIndentedString(this.ruoli)).append("\n");
    sb.append("    ruoliFonte: ").append(APIImplAutorizzazioneAbilitataNew.toIndentedString(this.ruoliFonte)).append("\n");
    sb.append("    ruoliRichiesti: ").append(APIImplAutorizzazioneAbilitataNew.toIndentedString(this.ruoliRichiesti)).append("\n");
    sb.append("    ruolo: ").append(APIImplAutorizzazioneAbilitataNew.toIndentedString(this.ruolo)).append("\n");
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

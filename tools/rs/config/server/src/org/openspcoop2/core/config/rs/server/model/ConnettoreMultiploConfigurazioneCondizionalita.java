/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ConnettoreMultiploConfigurazioneCondizionalita  {
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploCondizionalitaEnum condizione = null;
  
  @Schema(description = "La semantica cambia in funzione del tipo:   * header-based: nome dell'header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * soap-action-based: in questa modalità questo parametro non viene utilizzato   * content-based: xpath o json path da applicare sul contenuto   * indirizzo-ip: il campo non deve essere valorizzato, viene utilizzato l'indirizzo ip del socket come condizione   * indirizzo-ip-forwarded: il campo non deve essere valorizzato, viene utilizzato l'indirizzo ip inoltrato via header http come condizione   * template: template in formato 'govway'   * freemarker: template in formato 'freemarker'   * velocity: template in formato 'velocity'")
 /**
   * La semantica cambia in funzione del tipo:   * header-based: nome dell'header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * soap-action-based: in questa modalità questo parametro non viene utilizzato   * content-based: xpath o json path da applicare sul contenuto   * indirizzo-ip: il campo non deve essere valorizzato, viene utilizzato l'indirizzo ip del socket come condizione   * indirizzo-ip-forwarded: il campo non deve essere valorizzato, viene utilizzato l'indirizzo ip inoltrato via header http come condizione   * template: template in formato 'govway'   * freemarker: template in formato 'freemarker'   * velocity: template in formato 'velocity'  
  **/
  private String nome = null;
  
  @Schema(description = "")
  private String prefisso = null;
  
  @Schema(description = "")
  private String suffisso = null;
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploConfigurazioneCondizionalitaFallita identificazioneCondizioneFallita = null;
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploConfigurazioneCondizionalitaFallita nessunConnettoreUtilizzabile = null;
  
  @Schema(description = "")
  private List<ConnettoreMultiploConfigurazioneCondizionalitaRegola> regole = null;
 /**
   * Get condizione
   * @return condizione
  **/
  @JsonProperty("condizione")
  @NotNull
  @Valid
  public ConnettoreMultiploCondizionalitaEnum getCondizione() {
    return this.condizione;
  }

  public void setCondizione(ConnettoreMultiploCondizionalitaEnum condizione) {
    this.condizione = condizione;
  }

  public ConnettoreMultiploConfigurazioneCondizionalita condizione(ConnettoreMultiploCondizionalitaEnum condizione) {
    this.condizione = condizione;
    return this;
  }

 /**
   * La semantica cambia in funzione del tipo:   * header-based: nome dell'header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * soap-action-based: in questa modalità questo parametro non viene utilizzato   * content-based: xpath o json path da applicare sul contenuto   * indirizzo-ip: il campo non deve essere valorizzato, viene utilizzato l'indirizzo ip del socket come condizione   * indirizzo-ip-forwarded: il campo non deve essere valorizzato, viene utilizzato l'indirizzo ip inoltrato via header http come condizione   * template: template in formato 'govway'   * freemarker: template in formato 'freemarker'   * velocity: template in formato 'velocity'
   * @return nome
  **/
  @JsonProperty("nome")
  @Valid
 @Size(max=4000)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public ConnettoreMultiploConfigurazioneCondizionalita nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get prefisso
   * @return prefisso
  **/
  @JsonProperty("prefisso")
  @Valid
 @Size(max=255)  public String getPrefisso() {
    return this.prefisso;
  }

  public void setPrefisso(String prefisso) {
    this.prefisso = prefisso;
  }

  public ConnettoreMultiploConfigurazioneCondizionalita prefisso(String prefisso) {
    this.prefisso = prefisso;
    return this;
  }

 /**
   * Get suffisso
   * @return suffisso
  **/
  @JsonProperty("suffisso")
  @Valid
 @Size(max=255)  public String getSuffisso() {
    return this.suffisso;
  }

  public void setSuffisso(String suffisso) {
    this.suffisso = suffisso;
  }

  public ConnettoreMultiploConfigurazioneCondizionalita suffisso(String suffisso) {
    this.suffisso = suffisso;
    return this;
  }

 /**
   * Get identificazioneCondizioneFallita
   * @return identificazioneCondizioneFallita
  **/
  @JsonProperty("identificazione_condizione_fallita")
  @NotNull
  @Valid
  public ConnettoreMultiploConfigurazioneCondizionalitaFallita getIdentificazioneCondizioneFallita() {
    return this.identificazioneCondizioneFallita;
  }

  public void setIdentificazioneCondizioneFallita(ConnettoreMultiploConfigurazioneCondizionalitaFallita identificazioneCondizioneFallita) {
    this.identificazioneCondizioneFallita = identificazioneCondizioneFallita;
  }

  public ConnettoreMultiploConfigurazioneCondizionalita identificazioneCondizioneFallita(ConnettoreMultiploConfigurazioneCondizionalitaFallita identificazioneCondizioneFallita) {
    this.identificazioneCondizioneFallita = identificazioneCondizioneFallita;
    return this;
  }

 /**
   * Get nessunConnettoreUtilizzabile
   * @return nessunConnettoreUtilizzabile
  **/
  @JsonProperty("nessun_connettore_utilizzabile")
  @NotNull
  @Valid
  public ConnettoreMultiploConfigurazioneCondizionalitaFallita getNessunConnettoreUtilizzabile() {
    return this.nessunConnettoreUtilizzabile;
  }

  public void setNessunConnettoreUtilizzabile(ConnettoreMultiploConfigurazioneCondizionalitaFallita nessunConnettoreUtilizzabile) {
    this.nessunConnettoreUtilizzabile = nessunConnettoreUtilizzabile;
  }

  public ConnettoreMultiploConfigurazioneCondizionalita nessunConnettoreUtilizzabile(ConnettoreMultiploConfigurazioneCondizionalitaFallita nessunConnettoreUtilizzabile) {
    this.nessunConnettoreUtilizzabile = nessunConnettoreUtilizzabile;
    return this;
  }

 /**
   * Get regole
   * @return regole
  **/
  @JsonProperty("regole")
  @Valid
  public List<ConnettoreMultiploConfigurazioneCondizionalitaRegola> getRegole() {
    return this.regole;
  }

  public void setRegole(List<ConnettoreMultiploConfigurazioneCondizionalitaRegola> regole) {
    this.regole = regole;
  }

  public ConnettoreMultiploConfigurazioneCondizionalita regole(List<ConnettoreMultiploConfigurazioneCondizionalitaRegola> regole) {
    this.regole = regole;
    return this;
  }

  public ConnettoreMultiploConfigurazioneCondizionalita addRegoleItem(ConnettoreMultiploConfigurazioneCondizionalitaRegola regoleItem) {
    this.regole.add(regoleItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreMultiploConfigurazioneCondizionalita {\n");
    
    sb.append("    condizione: ").append(ConnettoreMultiploConfigurazioneCondizionalita.toIndentedString(this.condizione)).append("\n");
    sb.append("    nome: ").append(ConnettoreMultiploConfigurazioneCondizionalita.toIndentedString(this.nome)).append("\n");
    sb.append("    prefisso: ").append(ConnettoreMultiploConfigurazioneCondizionalita.toIndentedString(this.prefisso)).append("\n");
    sb.append("    suffisso: ").append(ConnettoreMultiploConfigurazioneCondizionalita.toIndentedString(this.suffisso)).append("\n");
    sb.append("    identificazioneCondizioneFallita: ").append(ConnettoreMultiploConfigurazioneCondizionalita.toIndentedString(this.identificazioneCondizioneFallita)).append("\n");
    sb.append("    nessunConnettoreUtilizzabile: ").append(ConnettoreMultiploConfigurazioneCondizionalita.toIndentedString(this.nessunConnettoreUtilizzabile)).append("\n");
    sb.append("    regole: ").append(ConnettoreMultiploConfigurazioneCondizionalita.toIndentedString(this.regole)).append("\n");
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

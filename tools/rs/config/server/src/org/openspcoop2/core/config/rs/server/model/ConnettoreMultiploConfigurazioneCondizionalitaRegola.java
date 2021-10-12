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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ConnettoreMultiploConfigurazioneCondizionalitaRegola  {
  
  @Schema(required = true, description = "")
  private String nomeRegola = null;
  
  @Schema(required = true, description = "espressione regolare che individui una o più operazioni")
 /**
   * espressione regolare che individui una o più operazioni  
  **/
  private String azione = null;
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploCondizionalitaRegolaEnum condizione = null;
  
  @Schema(description = "La semantica cambia in funzione del tipo:   * static: informazione utilizzata come filtro   * header-based: nome dell'header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * soap-action-based: in questa modalità questo parametro non viene utilizzato   * content-based: xpath o json path da applicare sul contenuto   * indirizzo-ip: il campo non deve essere valorizzato, viene utilizzato l'indirizzo ip del socket come condizione   * indirizzo-ip-forwarded: il campo non deve essere valorizzato, viene utilizzato l'indirizzo ip inoltrato via header http come condizione   * template: template in formato 'govway'   * freemarker: template in formato 'freemarker'   * velocity: template in formato 'velocity'")
 /**
   * La semantica cambia in funzione del tipo:   * static: informazione utilizzata come filtro   * header-based: nome dell'header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * soap-action-based: in questa modalità questo parametro non viene utilizzato   * content-based: xpath o json path da applicare sul contenuto   * indirizzo-ip: il campo non deve essere valorizzato, viene utilizzato l'indirizzo ip del socket come condizione   * indirizzo-ip-forwarded: il campo non deve essere valorizzato, viene utilizzato l'indirizzo ip inoltrato via header http come condizione   * template: template in formato 'govway'   * freemarker: template in formato 'freemarker'   * velocity: template in formato 'velocity'  
  **/
  private String nome = null;
  
  @Schema(description = "")
  private String prefisso = null;
  
  @Schema(description = "")
  private String suffisso = null;
 /**
   * Get nomeRegola
   * @return nomeRegola
  **/
  @JsonProperty("nome_regola")
  @NotNull
  @Valid
 @Size(max=255)  public String getNomeRegola() {
    return this.nomeRegola;
  }

  public void setNomeRegola(String nomeRegola) {
    this.nomeRegola = nomeRegola;
  }

  public ConnettoreMultiploConfigurazioneCondizionalitaRegola nomeRegola(String nomeRegola) {
    this.nomeRegola = nomeRegola;
    return this;
  }

 /**
   * espressione regolare che individui una o più operazioni
   * @return azione
  **/
  @JsonProperty("azione")
  @NotNull
  @Valid
 @Size(max=4000)  public String getAzione() {
    return this.azione;
  }

  public void setAzione(String azione) {
    this.azione = azione;
  }

  public ConnettoreMultiploConfigurazioneCondizionalitaRegola azione(String azione) {
    this.azione = azione;
    return this;
  }

 /**
   * Get condizione
   * @return condizione
  **/
  @JsonProperty("condizione")
  @NotNull
  @Valid
  public ConnettoreMultiploCondizionalitaRegolaEnum getCondizione() {
    return this.condizione;
  }

  public void setCondizione(ConnettoreMultiploCondizionalitaRegolaEnum condizione) {
    this.condizione = condizione;
  }

  public ConnettoreMultiploConfigurazioneCondizionalitaRegola condizione(ConnettoreMultiploCondizionalitaRegolaEnum condizione) {
    this.condizione = condizione;
    return this;
  }

 /**
   * La semantica cambia in funzione del tipo:   * static: informazione utilizzata come filtro   * header-based: nome dell'header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * soap-action-based: in questa modalità questo parametro non viene utilizzato   * content-based: xpath o json path da applicare sul contenuto   * indirizzo-ip: il campo non deve essere valorizzato, viene utilizzato l'indirizzo ip del socket come condizione   * indirizzo-ip-forwarded: il campo non deve essere valorizzato, viene utilizzato l'indirizzo ip inoltrato via header http come condizione   * template: template in formato 'govway'   * freemarker: template in formato 'freemarker'   * velocity: template in formato 'velocity'
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

  public ConnettoreMultiploConfigurazioneCondizionalitaRegola nome(String nome) {
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

  public ConnettoreMultiploConfigurazioneCondizionalitaRegola prefisso(String prefisso) {
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

  public ConnettoreMultiploConfigurazioneCondizionalitaRegola suffisso(String suffisso) {
    this.suffisso = suffisso;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreMultiploConfigurazioneCondizionalitaRegola {\n");
    
    sb.append("    nomeRegola: ").append(ConnettoreMultiploConfigurazioneCondizionalitaRegola.toIndentedString(this.nomeRegola)).append("\n");
    sb.append("    azione: ").append(ConnettoreMultiploConfigurazioneCondizionalitaRegola.toIndentedString(this.azione)).append("\n");
    sb.append("    condizione: ").append(ConnettoreMultiploConfigurazioneCondizionalitaRegola.toIndentedString(this.condizione)).append("\n");
    sb.append("    nome: ").append(ConnettoreMultiploConfigurazioneCondizionalitaRegola.toIndentedString(this.nome)).append("\n");
    sb.append("    prefisso: ").append(ConnettoreMultiploConfigurazioneCondizionalitaRegola.toIndentedString(this.prefisso)).append("\n");
    sb.append("    suffisso: ").append(ConnettoreMultiploConfigurazioneCondizionalitaRegola.toIndentedString(this.suffisso)).append("\n");
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

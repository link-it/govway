/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.core.monitor.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class InfoImplementazioneApi  {
  
  @Schema(description = "")
  private String nome = null;
  
  @Schema(description = "")
  private String tipo = null;
  
  @Schema(description = "")
  private Integer versione = null;
  
  @Schema(description = "")
  private String soggetto = null;
  
  @Schema(example = "20", description = "Il numero di azioni\\risorse della api")
 /**
   * Il numero di azioni\\risorse della api  
  **/
  private Integer azioni = null;
  
  @Schema(example = "2", description = "Il numero di erogazioni che implementano la api")
 /**
   * Il numero di erogazioni che implementano la api  
  **/
  private Integer erogazioni = null;
  
  @Schema(example = "2", description = "Il numero di fruizioni che implementano la api")
 /**
   * Il numero di fruizioni che implementano la api  
  **/
  private Integer fruizioni = null;
 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public InfoImplementazioneApi nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @Valid
 @Size(max=20)  public String getTipo() {
    return this.tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public InfoImplementazioneApi tipo(String tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get versione
   * @return versione
  **/
  @JsonProperty("versione")
  @Valid
  public Integer getVersione() {
    return this.versione;
  }

  public void setVersione(Integer versione) {
    this.versione = versione;
  }

  public InfoImplementazioneApi versione(Integer versione) {
    this.versione = versione;
    return this;
  }

 /**
   * Get soggetto
   * @return soggetto
  **/
  @JsonProperty("soggetto")
  @Valid
 @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255)  public String getSoggetto() {
    return this.soggetto;
  }

  public void setSoggetto(String soggetto) {
    this.soggetto = soggetto;
  }

  public InfoImplementazioneApi soggetto(String soggetto) {
    this.soggetto = soggetto;
    return this;
  }

 /**
   * Il numero di azioni\\risorse della api
   * @return azioni
  **/
  @JsonProperty("azioni")
  @Valid
  public Integer getAzioni() {
    return this.azioni;
  }

  public void setAzioni(Integer azioni) {
    this.azioni = azioni;
  }

  public InfoImplementazioneApi azioni(Integer azioni) {
    this.azioni = azioni;
    return this;
  }

 /**
   * Il numero di erogazioni che implementano la api
   * @return erogazioni
  **/
  @JsonProperty("erogazioni")
  @Valid
  public Integer getErogazioni() {
    return this.erogazioni;
  }

  public void setErogazioni(Integer erogazioni) {
    this.erogazioni = erogazioni;
  }

  public InfoImplementazioneApi erogazioni(Integer erogazioni) {
    this.erogazioni = erogazioni;
    return this;
  }

 /**
   * Il numero di fruizioni che implementano la api
   * @return fruizioni
  **/
  @JsonProperty("fruizioni")
  @Valid
  public Integer getFruizioni() {
    return this.fruizioni;
  }

  public void setFruizioni(Integer fruizioni) {
    this.fruizioni = fruizioni;
  }

  public InfoImplementazioneApi fruizioni(Integer fruizioni) {
    this.fruizioni = fruizioni;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InfoImplementazioneApi {\n");
    
    sb.append("    nome: ").append(InfoImplementazioneApi.toIndentedString(this.nome)).append("\n");
    sb.append("    tipo: ").append(InfoImplementazioneApi.toIndentedString(this.tipo)).append("\n");
    sb.append("    versione: ").append(InfoImplementazioneApi.toIndentedString(this.versione)).append("\n");
    sb.append("    soggetto: ").append(InfoImplementazioneApi.toIndentedString(this.soggetto)).append("\n");
    sb.append("    azioni: ").append(InfoImplementazioneApi.toIndentedString(this.azioni)).append("\n");
    sb.append("    erogazioni: ").append(InfoImplementazioneApi.toIndentedString(this.erogazioni)).append("\n");
    sb.append("    fruizioni: ").append(InfoImplementazioneApi.toIndentedString(this.fruizioni)).append("\n");
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

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

public class Soggetto extends BaseCredenziali {
  
  @Schema(required = true, description = "")
  private String nome = null;
  
  @Schema(description = "")
  private DominioEnum dominio = null;
  
  @Schema(example = "descrizione del soggetto EnteEsterno", description = "")
  private String descrizione = null;
  
  @Schema(example = "[\"ruolo1\",\"ruolo2\"]", description = "")
  private List<String> ruoli = null;
  
  @Schema(description = "")
  private List<Proprieta4000> proprieta = null;
 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @NotNull
  @Valid
 @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public Soggetto nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get dominio
   * @return dominio
  **/
  @JsonProperty("dominio")
  @Valid
  public DominioEnum getDominio() {
    return this.dominio;
  }

  public void setDominio(DominioEnum dominio) {
    this.dominio = dominio;
  }

  public Soggetto dominio(DominioEnum dominio) {
    this.dominio = dominio;
    return this;
  }

 /**
   * Get descrizione
   * @return descrizione
  **/
  @JsonProperty("descrizione")
  @Valid
 @Size(max=255)  public String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  public Soggetto descrizione(String descrizione) {
    this.descrizione = descrizione;
    return this;
  }

 /**
   * Get ruoli
   * @return ruoli
  **/
  @JsonProperty("ruoli")
  @Valid
  public List<String> getRuoli() {
    return this.ruoli;
  }

  public void setRuoli(List<String> ruoli) {
    this.ruoli = ruoli;
  }

  public Soggetto ruoli(List<String> ruoli) {
    this.ruoli = ruoli;
    return this;
  }

  public Soggetto addRuoliItem(String ruoliItem) {
    this.ruoli.add(ruoliItem);
    return this;
  }

 /**
   * Get proprieta
   * @return proprieta
  **/
  @JsonProperty("proprieta")
  @Valid
  public List<Proprieta4000> getProprieta() {
    return this.proprieta;
  }

  public void setProprieta(List<Proprieta4000> proprieta) {
    this.proprieta = proprieta;
  }

  public Soggetto proprieta(List<Proprieta4000> proprieta) {
    this.proprieta = proprieta;
    return this;
  }

  public Soggetto addProprietaItem(Proprieta4000 proprietaItem) {
    this.proprieta.add(proprietaItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Soggetto {\n");
    sb.append("    ").append(Soggetto.toIndentedString(super.toString())).append("\n");
    sb.append("    nome: ").append(Soggetto.toIndentedString(this.nome)).append("\n");
    sb.append("    dominio: ").append(Soggetto.toIndentedString(this.dominio)).append("\n");
    sb.append("    descrizione: ").append(Soggetto.toIndentedString(this.descrizione)).append("\n");
    sb.append("    ruoli: ").append(Soggetto.toIndentedString(this.ruoli)).append("\n");
    sb.append("    proprieta: ").append(Soggetto.toIndentedString(this.proprieta)).append("\n");
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

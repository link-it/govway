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

public class ApiBase  {
  
  @Schema(description = "")
  private String referente = null;
  
  @Schema(required = true, description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "protocollo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ApiInterfacciaRest.class, name = "rest"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ApiInterfacciaSoap.class, name = "soap")  })
  private OneOfApiBaseTipoInterfaccia tipoInterfaccia = null;
  
  @Schema(required = true, description = "")
  private String nome = null;
  
  @Schema(example = "descrizione API", description = "")
  private String descrizione = null;
  
  @Schema(required = true, description = "")
  private Integer versione = null;
  
  @Schema(example = "[\"PagamentiTelematici\",\"Anagrafica\"]", description = "")
  private List<String> tags = null;
 /**
   * Get referente
   * @return referente
  **/
  @JsonProperty("referente")
  @Valid
 @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255)  public String getReferente() {
    return this.referente;
  }

  public void setReferente(String referente) {
    this.referente = referente;
  }

  public ApiBase referente(String referente) {
    this.referente = referente;
    return this;
  }

 /**
   * Get tipoInterfaccia
   * @return tipoInterfaccia
  **/
  @JsonProperty("tipo_interfaccia")
  @NotNull
  @Valid
  public OneOfApiBaseTipoInterfaccia getTipoInterfaccia() {
    return this.tipoInterfaccia;
  }

  public void setTipoInterfaccia(OneOfApiBaseTipoInterfaccia tipoInterfaccia) {
    this.tipoInterfaccia = tipoInterfaccia;
  }

  public ApiBase tipoInterfaccia(OneOfApiBaseTipoInterfaccia tipoInterfaccia) {
    this.tipoInterfaccia = tipoInterfaccia;
    return this;
  }

 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @NotNull
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public ApiBase nome(String nome) {
    this.nome = nome;
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

  public ApiBase descrizione(String descrizione) {
    this.descrizione = descrizione;
    return this;
  }

 /**
   * Get versione
   * @return versione
  **/
  @JsonProperty("versione")
  @NotNull
  @Valid
  public Integer getVersione() {
    return this.versione;
  }

  public void setVersione(Integer versione) {
    this.versione = versione;
  }

  public ApiBase versione(Integer versione) {
    this.versione = versione;
    return this;
  }

 /**
   * Get tags
   * @return tags
  **/
  @JsonProperty("tags")
  @Valid
  public List<String> getTags() {
    return this.tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public ApiBase tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public ApiBase addTagsItem(String tagsItem) {
    this.tags.add(tagsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiBase {\n");
    
    sb.append("    referente: ").append(ApiBase.toIndentedString(this.referente)).append("\n");
    sb.append("    tipoInterfaccia: ").append(ApiBase.toIndentedString(this.tipoInterfaccia)).append("\n");
    sb.append("    nome: ").append(ApiBase.toIndentedString(this.nome)).append("\n");
    sb.append("    descrizione: ").append(ApiBase.toIndentedString(this.descrizione)).append("\n");
    sb.append("    versione: ").append(ApiBase.toIndentedString(this.versione)).append("\n");
    sb.append("    tags: ").append(ApiBase.toIndentedString(this.tags)).append("\n");
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

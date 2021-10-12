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

public class BaseConnettoreMultiplo  {
  
  @Schema(required = true, description = "")
  private String nome = null;
  
  @Schema(description = "")
  private String descrizione = null;
  
  @Schema(required = true, description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "tipo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreHttp.class, name = "http"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreFile.class, name = "file"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreJms.class, name = "jms"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreNull.class, name = "null"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreEcho.class, name = "echo"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettorePlugin.class, name = "plugin"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreApplicativoServer.class, name = "applicativo-server"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConnettoreMessageBox.class, name = "message-box")  })
  private OneOfBaseConnettoreMultiploConnettore connettore = null;
  
  @Schema(description = "")
  private List<String> filtri = null;
 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @NotNull
  @Valid
 @Size(max=255)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public BaseConnettoreMultiplo nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get descrizione
   * @return descrizione
  **/
  @JsonProperty("descrizione")
  @Valid
 @Size(max=4000)  public String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  public BaseConnettoreMultiplo descrizione(String descrizione) {
    this.descrizione = descrizione;
    return this;
  }

 /**
   * Get connettore
   * @return connettore
  **/
  @JsonProperty("connettore")
  @NotNull
  @Valid
  public OneOfBaseConnettoreMultiploConnettore getConnettore() {
    return this.connettore;
  }

  public void setConnettore(OneOfBaseConnettoreMultiploConnettore connettore) {
    this.connettore = connettore;
  }

  public BaseConnettoreMultiplo connettore(OneOfBaseConnettoreMultiploConnettore connettore) {
    this.connettore = connettore;
    return this;
  }

 /**
   * Get filtri
   * @return filtri
  **/
  @JsonProperty("filtri")
  @Valid
  public List<String> getFiltri() {
    return this.filtri;
  }

  public void setFiltri(List<String> filtri) {
    this.filtri = filtri;
  }

  public BaseConnettoreMultiplo filtri(List<String> filtri) {
    this.filtri = filtri;
    return this;
  }

  public BaseConnettoreMultiplo addFiltriItem(String filtriItem) {
    this.filtri.add(filtriItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BaseConnettoreMultiplo {\n");
    
    sb.append("    nome: ").append(BaseConnettoreMultiplo.toIndentedString(this.nome)).append("\n");
    sb.append("    descrizione: ").append(BaseConnettoreMultiplo.toIndentedString(this.descrizione)).append("\n");
    sb.append("    connettore: ").append(BaseConnettoreMultiplo.toIndentedString(this.connettore)).append("\n");
    sb.append("    filtri: ").append(BaseConnettoreMultiplo.toIndentedString(this.filtri)).append("\n");
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

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

public class ApplicativoServer  {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private String nome = null;
  
  @Schema(example = "descrizione dell'applicativo", description = "")
  private String descrizione = null;
  
  @Schema(description = "")
  private List<Proprieta4000> proprieta = null;
  
  @Schema(description = "")
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
  private OneOfApplicativoServerConnettore connettore = null;
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

  public ApplicativoServer nome(String nome) {
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

  public ApplicativoServer descrizione(String descrizione) {
    this.descrizione = descrizione;
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

  public ApplicativoServer proprieta(List<Proprieta4000> proprieta) {
    this.proprieta = proprieta;
    return this;
  }

  public ApplicativoServer addProprietaItem(Proprieta4000 proprietaItem) {
    this.proprieta.add(proprietaItem);
    return this;
  }

 /**
   * Get connettore
   * @return connettore
  **/
  @JsonProperty("connettore")
  @Valid
  public OneOfApplicativoServerConnettore getConnettore() {
    return this.connettore;
  }

  public void setConnettore(OneOfApplicativoServerConnettore connettore) {
    this.connettore = connettore;
  }

  public ApplicativoServer connettore(OneOfApplicativoServerConnettore connettore) {
    this.connettore = connettore;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApplicativoServer {\n");
    
    sb.append("    nome: ").append(ApplicativoServer.toIndentedString(this.nome)).append("\n");
    sb.append("    descrizione: ").append(ApplicativoServer.toIndentedString(this.descrizione)).append("\n");
    sb.append("    proprieta: ").append(ApplicativoServer.toIndentedString(this.proprieta)).append("\n");
    sb.append("    connettore: ").append(ApplicativoServer.toIndentedString(this.connettore)).append("\n");
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

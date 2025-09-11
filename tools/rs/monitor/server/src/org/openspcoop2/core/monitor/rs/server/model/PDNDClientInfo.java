/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class PDNDClientInfo  {
  
  @Schema(description = "")
  private String nome = null;
  
  @Schema(description = "")
  private String descrizione = null;
  
  @Schema(description = "")
  private String clientId = null;
 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @Valid
  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public PDNDClientInfo nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get descrizione
   * @return descrizione
  **/
  @JsonProperty("descrizione")
  @Valid
  public String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  public PDNDClientInfo descrizione(String descrizione) {
    this.descrizione = descrizione;
    return this;
  }

 /**
   * Get clientId
   * @return clientId
  **/
  @JsonProperty("client_id")
  @Valid
  public String getClientId() {
    return this.clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public PDNDClientInfo clientId(String clientId) {
    this.clientId = clientId;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PDNDClientInfo {\n");
    
    sb.append("    nome: ").append(PDNDClientInfo.toIndentedString(this.nome)).append("\n");
    sb.append("    descrizione: ").append(PDNDClientInfo.toIndentedString(this.descrizione)).append("\n");
    sb.append("    clientId: ").append(PDNDClientInfo.toIndentedString(this.clientId)).append("\n");
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

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
package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class PDNDOrganizationInfo  {
  
  @Schema(description = "")
  private String nome = null;
  
  @Schema(description = "")
  private String categoria = null;
  
  @Schema(description = "")
  private PDNDOrganizationExternalId externalId = null;
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

  public PDNDOrganizationInfo nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get categoria
   * @return categoria
  **/
  @JsonProperty("categoria")
  @Valid
  public String getCategoria() {
    return this.categoria;
  }

  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }

  public PDNDOrganizationInfo categoria(String categoria) {
    this.categoria = categoria;
    return this;
  }

 /**
   * Get externalId
   * @return externalId
  **/
  @JsonProperty("external_id")
  @Valid
  public PDNDOrganizationExternalId getExternalId() {
    return this.externalId;
  }

  public void setExternalId(PDNDOrganizationExternalId externalId) {
    this.externalId = externalId;
  }

  public PDNDOrganizationInfo externalId(PDNDOrganizationExternalId externalId) {
    this.externalId = externalId;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PDNDOrganizationInfo {\n");
    
    sb.append("    nome: ").append(PDNDOrganizationInfo.toIndentedString(this.nome)).append("\n");
    sb.append("    categoria: ").append(PDNDOrganizationInfo.toIndentedString(this.categoria)).append("\n");
    sb.append("    externalId: ").append(PDNDOrganizationInfo.toIndentedString(this.externalId)).append("\n");
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

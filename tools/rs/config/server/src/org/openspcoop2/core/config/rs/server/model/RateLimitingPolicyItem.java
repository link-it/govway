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
package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RateLimitingPolicyItem  {
  
  @Schema(required = true, description = "")
  private String identificativo = null;
  
  @Schema(description = "")
  private String nome = null;
 /**
   * Get identificativo
   * @return identificativo
  **/
  @JsonProperty("identificativo")
  @NotNull
  @Valid
 @Size(max=255)  public String getIdentificativo() {
    return this.identificativo;
  }

  public void setIdentificativo(String identificativo) {
    this.identificativo = identificativo;
  }

  public RateLimitingPolicyItem identificativo(String identificativo) {
    this.identificativo = identificativo;
    return this;
  }

 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @Valid
 @Size(max=255)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public RateLimitingPolicyItem nome(String nome) {
    this.nome = nome;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateLimitingPolicyItem {\n");
    
    sb.append("    identificativo: ").append(RateLimitingPolicyItem.toIndentedString(this.identificativo)).append("\n");
    sb.append("    nome: ").append(RateLimitingPolicyItem.toIndentedString(this.nome)).append("\n");
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

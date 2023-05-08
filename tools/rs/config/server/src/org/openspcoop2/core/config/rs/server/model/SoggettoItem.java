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

import org.openspcoop2.utils.service.beans.BaseItem;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class SoggettoItem extends BaseItem {
  
  @Schema(required = true, description = "")
  private String nome = null;
  
  @Schema(required = true, description = "")
  private DominioEnum dominio = null;
  
  @Schema(example = "0", description = "")
  private Integer countRuoli = null;
 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @NotNull
  @Valid
 @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public SoggettoItem nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get dominio
   * @return dominio
  **/
  @JsonProperty("dominio")
  @NotNull
  @Valid
  public DominioEnum getDominio() {
    return this.dominio;
  }

  public void setDominio(DominioEnum dominio) {
    this.dominio = dominio;
  }

  public SoggettoItem dominio(DominioEnum dominio) {
    this.dominio = dominio;
    return this;
  }

 /**
   * Get countRuoli
   * minimum: 0
   * @return countRuoli
  **/
  @JsonProperty("count_ruoli")
  @Valid
 @Min(0)  public Integer getCountRuoli() {
    return this.countRuoli;
  }

  public void setCountRuoli(Integer countRuoli) {
    this.countRuoli = countRuoli;
  }

  public SoggettoItem countRuoli(Integer countRuoli) {
    this.countRuoli = countRuoli;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SoggettoItem {\n");
    sb.append("    ").append(SoggettoItem.toIndentedString(super.toString())).append("\n");
    sb.append("    nome: ").append(SoggettoItem.toIndentedString(this.nome)).append("\n");
    sb.append("    dominio: ").append(SoggettoItem.toIndentedString(this.dominio)).append("\n");
    sb.append("    countRuoli: ").append(SoggettoItem.toIndentedString(this.countRuoli)).append("\n");
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

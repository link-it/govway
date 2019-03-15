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

public class RiepilogoApiItem  {
  
  @Schema(description = "")
  private String fruitore = null;
  
  @Schema(required = true, description = "")
  private String erogatore = null;
  
  @Schema(description = "")
  private String nome = null;
  
  @Schema(description = "")
  private String tipo = null;
  
  @Schema(description = "")
  private Integer versione = null;
 /**
   * Get fruitore
   * @return fruitore
  **/
  @JsonProperty("fruitore")
  @Valid
 @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255)  public String getFruitore() {
    return this.fruitore;
  }

  public void setFruitore(String fruitore) {
    this.fruitore = fruitore;
  }

  public RiepilogoApiItem fruitore(String fruitore) {
    this.fruitore = fruitore;
    return this;
  }

 /**
   * Get erogatore
   * @return erogatore
  **/
  @JsonProperty("erogatore")
  @NotNull
  @Valid
 @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255)  public String getErogatore() {
    return this.erogatore;
  }

  public void setErogatore(String erogatore) {
    this.erogatore = erogatore;
  }

  public RiepilogoApiItem erogatore(String erogatore) {
    this.erogatore = erogatore;
    return this;
  }

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

  public RiepilogoApiItem nome(String nome) {
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

  public RiepilogoApiItem tipo(String tipo) {
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

  public RiepilogoApiItem versione(Integer versione) {
    this.versione = versione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RiepilogoApiItem {\n");
    
    sb.append("    fruitore: ").append(RiepilogoApiItem.toIndentedString(this.fruitore)).append("\n");
    sb.append("    erogatore: ").append(RiepilogoApiItem.toIndentedString(this.erogatore)).append("\n");
    sb.append("    nome: ").append(RiepilogoApiItem.toIndentedString(this.nome)).append("\n");
    sb.append("    tipo: ").append(RiepilogoApiItem.toIndentedString(this.tipo)).append("\n");
    sb.append("    versione: ").append(RiepilogoApiItem.toIndentedString(this.versione)).append("\n");
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

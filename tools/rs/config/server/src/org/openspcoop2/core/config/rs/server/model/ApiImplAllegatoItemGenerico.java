/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

public class ApiImplAllegatoItemGenerico  implements OneOfApiImplAllegatoItemAllegato {
  
  @Schema(required = true, description = "")
  private String nome = null;
  
  @Schema(required = true, description = "")
  private RuoloAllegatoAPIImpl ruolo = null;
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

  public ApiImplAllegatoItemGenerico nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get ruolo
   * @return ruolo
  **/
  @Override
@JsonProperty("ruolo")
  @NotNull
  @Valid
  public RuoloAllegatoAPIImpl getRuolo() {
    return this.ruolo;
  }

  public void setRuolo(RuoloAllegatoAPIImpl ruolo) {
    this.ruolo = ruolo;
  }

  public ApiImplAllegatoItemGenerico ruolo(RuoloAllegatoAPIImpl ruolo) {
    this.ruolo = ruolo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiImplAllegatoItemGenerico {\n");
    
    sb.append("    nome: ").append(ApiImplAllegatoItemGenerico.toIndentedString(this.nome)).append("\n");
    sb.append("    ruolo: ").append(ApiImplAllegatoItemGenerico.toIndentedString(this.ruolo)).append("\n");
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

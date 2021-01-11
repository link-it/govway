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
package org.openspcoop2.utils.service.beans;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneContenutoMessaggioHeader", propOrder =
    { "nome", "valore"
})

@XmlRootElement(name="TransazioneContenutoMessaggioHeader")
public class TransazioneContenutoMessaggioHeader  {
  @XmlElement(name="nome", required = true)
  
  @Schema(example = "Content-Type", required = true, description = "")
  private String nome = null;
  @XmlElement(name="valore", required = true)
  
  @Schema(example = "application/json", required = true, description = "")
  private String valore = null;
 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @NotNull
  @Valid
  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public TransazioneContenutoMessaggioHeader nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get valore
   * @return valore
  **/
  @JsonProperty("valore")
  @NotNull
  @Valid
  public String getValore() {
    return this.valore;
  }

  public void setValore(String valore) {
    this.valore = valore;
  }

  public TransazioneContenutoMessaggioHeader valore(String valore) {
    this.valore = valore;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneContenutoMessaggioHeader {\n");
    
    sb.append("    nome: ").append(TransazioneContenutoMessaggioHeader.toIndentedString(this.nome)).append("\n");
    sb.append("    valore: ").append(TransazioneContenutoMessaggioHeader.toIndentedString(this.valore)).append("\n");
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

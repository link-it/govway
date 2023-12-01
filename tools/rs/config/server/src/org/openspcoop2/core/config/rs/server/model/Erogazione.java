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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class Erogazione extends APIImpl {
  
  @Schema(description = "")
  private String descrizione = null;
  
  @Schema(description = "")
  private String erogazioneNome = null;
  
  @Schema(description = "")
  private Integer erogazioneVersione = null;
  
  @Schema(description = "")
  private String canale = null;
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "protocollo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ErogazioneModISoap.class, name = "soap"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ErogazioneModIRest.class, name = "rest")  })
  private OneOfErogazioneModi modi = null;
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

  public Erogazione descrizione(String descrizione) {
    this.descrizione = descrizione;
    return this;
  }

 /**
   * Get erogazioneNome
   * @return erogazioneNome
  **/
  @JsonProperty("erogazione_nome")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getErogazioneNome() {
    return this.erogazioneNome;
  }

  public void setErogazioneNome(String erogazioneNome) {
    this.erogazioneNome = erogazioneNome;
  }

  public Erogazione erogazioneNome(String erogazioneNome) {
    this.erogazioneNome = erogazioneNome;
    return this;
  }

 /**
   * Get erogazioneVersione
   * @return erogazioneVersione
  **/
  @JsonProperty("erogazione_versione")
  @Valid
  public Integer getErogazioneVersione() {
    return this.erogazioneVersione;
  }

  public void setErogazioneVersione(Integer erogazioneVersione) {
    this.erogazioneVersione = erogazioneVersione;
  }

  public Erogazione erogazioneVersione(Integer erogazioneVersione) {
    this.erogazioneVersione = erogazioneVersione;
    return this;
  }

 /**
   * Get canale
   * @return canale
  **/
  @JsonProperty("canale")
  @Valid
 @Pattern(regexp="^[^\\s]+$") @Size(max=255)  public String getCanale() {
    return this.canale;
  }

  public void setCanale(String canale) {
    this.canale = canale;
  }

  public Erogazione canale(String canale) {
    this.canale = canale;
    return this;
  }

 /**
   * Get modi
   * @return modi
  **/
  @JsonProperty("modi")
  @Valid
  public OneOfErogazioneModi getModi() {
    return this.modi;
  }

  public void setModi(OneOfErogazioneModi modi) {
    this.modi = modi;
  }

  public Erogazione modi(OneOfErogazioneModi modi) {
    this.modi = modi;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Erogazione {\n");
    sb.append("    ").append(Erogazione.toIndentedString(super.toString())).append("\n");
    sb.append("    descrizione: ").append(Erogazione.toIndentedString(this.descrizione)).append("\n");
    sb.append("    erogazioneNome: ").append(Erogazione.toIndentedString(this.erogazioneNome)).append("\n");
    sb.append("    erogazioneVersione: ").append(Erogazione.toIndentedString(this.erogazioneVersione)).append("\n");
    sb.append("    canale: ").append(Erogazione.toIndentedString(this.canale)).append("\n");
    sb.append("    modi: ").append(Erogazione.toIndentedString(this.modi)).append("\n");
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
